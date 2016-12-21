package cache.inmemorycache.server.internal.transaction;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import cache.inmemorycache.server.internal.datastore.CacheValue;
import cache.inmemorycache.server.internal.datastore.Snapshot;

public class Transaction {

  private final Map<String, Integer> valueCount;

  public Map<String, CacheValue> getUpdatedKeys() {
    return updatedKeys;
  }

  private Map<String, CacheValue> updatedKeys = new HashMap<>();

  private Map<String, CacheValue> deletedKeys = new HashMap<>();

  private Map<String, CacheValue> getKeys = new HashMap<>();
  private Map<String, Integer> getCounts = new HashMap<>();

  private final Map<String, CacheValue> inMemoryMap;
  private final Map<String, Snapshot> snapshots;

  private final Timestamp timestamp = new Timestamp(System.currentTimeMillis());

  public Transaction(String id, Map<String, CacheValue> inMemoryMap, Map<String, Snapshot> snapshots, Map<String, Integer> valueCount) {
    this.inMemoryMap = inMemoryMap;
    this.snapshots = snapshots;
    this.id = id;
    this.valueCount = valueCount;
  }

  public String getId() {
    return id;
  }

  private String id;

  public void set(String key, String value) throws CloneNotSupportedException {
    CacheValue cacheValue;
    if (inMemoryMap.containsKey(key)) {
      cacheValue = inMemoryMap.get(key).clone();
      getKeys.put(key, cacheValue.clone());
    } else if (updatedKeys.containsKey(key)) {
      cacheValue = updatedKeys.get(key);
    } else {
      cacheValue = new CacheValue();
    }
    cacheValue.setData(value);
    updatedKeys.put(key, cacheValue);
  }

  public CacheValue get(String key) {
    CacheValue result = null;
    if (deletedKeys.containsKey(key)) {
      result = null;
    } else if (updatedKeys.containsKey(key)) {
      result = updatedKeys.get(key);
    } else if (getKeys.containsKey(key)) {
      result = getKeys.get(key);
    } else if (snapshots.containsKey(key) && snapshots.get(key).getNearestTimestampValue(timestamp) != null) {
      result = snapshots.get(key).getNearestTimestampValue(timestamp);
    } else {
      synchronized (snapshots) {
        synchronized (inMemoryMap) {
          if (inMemoryMap.containsKey(key)) {
            result = inMemoryMap.get(key);
            if (result != null && result.getTimestamp().after(this.timestamp)) {
              result = null;
            }
          } else if (snapshots.containsKey(key)) {
            result = snapshots.get(key).getNearestTimestampValue(timestamp);

          }
        }
      }
    }

    getKeys.put(key, result);
    return result;
  }

  public Timestamp getTimestamp() {
    return timestamp;
  }

  public Map<String, CacheValue> getDeletedKeys() {
    return deletedKeys;
  }

  public void delete(String key, String transactionId) {
    if (updatedKeys.containsKey(key)) {
      updatedKeys.remove(key);
    } else {
      deletedKeys.put(key, get(key));
    }
  }

  public Map<String, CacheValue> getGetKeys() {
    return getKeys;
  }

  public int getCount(String value) {
    int count = 0;
    return count;
  }
}

