package cache.inmemorycache.server.internal.transaction;

import java.util.Map;

import cache.inmemorycache.server.internal.datastore.CacheValue;
import cache.inmemorycache.server.internal.datastore.Snapshot;

public interface ITransactionManager {

  public Transaction getTransaction(String transactionId);

  public Transaction createTransaction(Map<String, CacheValue> inMemoryMap, Map<String, Snapshot> snapshots, Map<String, Integer> valueCount);

  boolean isValidTransaction(String transactionId);

  void removeTransaction(Transaction transaction);
}
