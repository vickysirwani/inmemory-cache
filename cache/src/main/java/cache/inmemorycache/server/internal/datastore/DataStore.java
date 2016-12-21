package cache.inmemorycache.server.internal.datastore;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cache.inmemorycache.server.internal.transaction.ITransactionManager;
import cache.inmemorycache.server.internal.transaction.Transaction;

import static java.util.Collections.sort;

@Singleton
public class DataStore implements IDataStore {

  private static final String COMMIT_FAILED = "Commit Failed";
  private Map<String, CacheValue> inMemoryCache = new HashMap<>();

  private Map<String, Integer> valueCount = new HashMap<>();

  private Map<String, Snapshot> keySnapshots = new HashMap<>();

  @Inject
  private ITransactionManager transactionManager;

  @Override
  public void set(String key, String value) throws CloneNotSupportedException {
    Transaction transaction = begin();
    set(key, value, transaction.getId());
    commit(transaction);
  }

  @Override
  public CacheValue get(String key) {
    if (inMemoryCache.containsKey(key)) {
      return inMemoryCache.get(key);
    } else {
      return null;
    }
  }

  @Override
  public synchronized Transaction begin() {
    return transactionManager.createTransaction(inMemoryCache, keySnapshots, valueCount);
  }

  @Override
  public synchronized void set(String key, String value, String transactionId) throws CloneNotSupportedException {
    transactionManager.getTransaction(transactionId).set(key, value);
  }

  @Override
  public CacheValue get(String key, String transactionId) {
    Transaction transaction = transactionManager.getTransaction(transactionId);
    return transaction.get(key);
  }

  @Override
  public synchronized boolean containsKey(java.lang.String key) {
    return inMemoryCache.containsKey(key);
  }

  @Override
  public synchronized boolean isValidTransaction(java.lang.String transactionId) {
    return transactionManager.isValidTransaction(transactionId);
  }

  @Override
  public synchronized int getCount(String value) {
    return valueCount.getOrDefault(value, 0);
  }

  @Override
  public synchronized void commit(Transaction transaction) {
    Map<String, CacheValue> updatedKeys = transaction.getUpdatedKeys();
    Map<String, CacheValue> deletedKeys = transaction.getDeletedKeys();

    List<String> updatedKeySet = new ArrayList<>(updatedKeys.keySet());
    List<String> deletedKeySet = new ArrayList<>(deletedKeys.keySet());
    sort(updatedKeySet);
    sort(deletedKeySet);
    Map<String, CacheValue> getKeys = getLatestUpdatedAndDeletedKeys(updatedKeySet, deletedKeySet);
    if (validateKeyVersions(transaction, updatedKeys, getKeys, updatedKeySet)) {
      throw new IllegalStateException(COMMIT_FAILED);
    }
    if (validateKeyVersionsForDelete(transaction, deletedKeys, getKeys, deletedKeySet)) {
      throw new IllegalStateException(COMMIT_FAILED);
    }
    Map<String, String> updatedKeySetLocks = new HashMap<>();
    Map<String, String> deletedKeySetLocks = new HashMap<>();
    getCommitLocks(updatedKeySet, deletedKeySet, updatedKeySetLocks, deletedKeySetLocks);
    deletedKeySet.forEach(this::deleteKeyFromStore);
    for (String s : updatedKeySet) {
      updateDataStore(s, updatedKeys.get(s));
    }
    for (String s : updatedKeySetLocks.keySet()) {
      updatedKeys.get(s).unlock(updatedKeySetLocks.get(s));
    }
    for (String s : deletedKeySetLocks.keySet()) {
      deletedKeys.get(s).unlock(deletedKeySetLocks.get(s));
    }
    postCommitCleanup(transaction);

  }

  private boolean validateKeyVersionsForDelete(Transaction transaction, Map<String, CacheValue> deletedKeys, Map<String, CacheValue> getKeys, List<String> deletedKeySet) {
    for (String s : deletedKeySet) {
      if (getKeys.containsKey(s) && getKeys.get(s).getVersion() != deletedKeys.get(s).getVersion()) {
        postCommitCleanup(transaction);
        return true;
      }
    }
    return false;
  }

  private Map<String, CacheValue> getLatestUpdatedAndDeletedKeys(List<String> updatedKeySet, List<String> deletedKeySet) {
    Map<String, CacheValue> getKeys = new HashMap<>();
    updatedKeySet.stream().filter(s -> inMemoryCache.containsKey(s)).forEach(s -> getKeys.put(s, inMemoryCache.get(s)));
    deletedKeySet.stream().filter(s -> inMemoryCache.containsKey(s)).forEach(s -> getKeys.put(s, inMemoryCache.get(s)));
    return getKeys;
  }

  private boolean validateKeyVersions(Transaction transaction, Map<String, CacheValue> updatedKeys, Map<String, CacheValue> getKeys, List<String> updatedKeySet) {
    for (String s : updatedKeySet) {
      if (getKeys.containsKey(s) && getKeys.get(s).getVersion() != updatedKeys.get(s).getVersion() - 1) {
        postCommitCleanup(transaction);
        return true;
      }
    }
    return false;
  }

  private void postCommitCleanup(Transaction transaction) {
    transactionManager.removeTransaction(transaction);
  }

  private void getCommitLocks(List<String> updatedKeySet, List<String> deletedKeySet, Map<String, String> updatedKeySetLocks, Map<String, String> deletedKeySetLocks) {
    updatedKeySet.stream().filter(anUpdatedKeySet -> inMemoryCache.containsKey(anUpdatedKeySet)).forEach(anUpdatedKeySet -> updatedKeySetLocks.put(anUpdatedKeySet, inMemoryCache.get(anUpdatedKeySet).getLock()));
    deletedKeySet.stream().filter(aDeletedKeySet -> inMemoryCache.containsKey(aDeletedKeySet)).forEach(aDeletedKeySet -> deletedKeySetLocks.put(aDeletedKeySet, inMemoryCache.get(aDeletedKeySet).getLock()));
  }

  private void updateDataStore(String key, CacheValue value) {
    CacheValue oldValue = updateInMemoryCache(key, value);
    updateValueCount(value.getData(), valueCount.getOrDefault(value.getData(), 0) + 1);
    if (oldValue != null) {
      updateValueCount(oldValue.getData(), valueCount.getOrDefault(value.getData(), 0) - 1);
    }
    updateSnapshots(key, value);
  }

  private void updateSnapshots(String key, CacheValue value) {
    if (keySnapshots.containsKey(key)) {
      if (value != null) {
        keySnapshots.get(key).getSnapshots().put(new Timestamp(System.currentTimeMillis()), value);
      }
    } else {
      Snapshot snapshot = new Snapshot();
      if (value != null) {
        snapshot.getSnapshots().put(new Timestamp(System.currentTimeMillis()), value);
      }
      keySnapshots.put(key, snapshot);
    }
  }

  private void updateValueCount(String data, int value2) {
    valueCount.put(data, value2);
  }

  private CacheValue updateInMemoryCache(String key, CacheValue value) {
    CacheValue oldValue = inMemoryCache.getOrDefault(key, null);
    inMemoryCache.put(key, value);
    return oldValue;
  }

  private void deleteKeyFromStore(String key) {
    updateValueCount(inMemoryCache.get(key).getData(), valueCount.get(inMemoryCache.get(key).getData()) - 1);
    inMemoryCache.remove(key);
  }

  @Override
  public void commit(String transactionId) {
    commit(transactionManager.getTransaction(transactionId));
  }

  @Override
  public void delete(String key) throws CloneNotSupportedException {
    Transaction transaction = begin();
    delete(key, transaction.getId());
    commit(transaction);
  }

  @Override
  public void delete(String key, String transactionId) throws CloneNotSupportedException {
    transactionManager.getTransaction(transactionId).delete(key, transactionId);
  }

  @Override
  public void print() {
    System.out.println(valueCount);
    System.out.println(inMemoryCache);
    System.out.println(keySnapshots);
    System.out.println(transactionManager);
  }

  @Override
  public Timestamp getTransactionTimestamp(String transactionId) {
    return transactionManager.getTransaction(transactionId).getTimestamp();
  }

  @Override
  public void rollback(String transactionId) {
    transactionManager.removeTransaction(transactionManager.getTransaction(transactionId));
  }

  @Override
  public int getCount(String value, String transactionId) {
    return transactionManager.getTransaction(transactionId).getCount(value);
  }


}
