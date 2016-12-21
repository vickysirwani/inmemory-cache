package cache.inmemorycache.server.internal.datastore;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import cache.inmemorycache.server.utils.BinarySearcher;

import static java.util.Arrays.sort;

public class Snapshot {
  public Map<Timestamp, CacheValue> getSnapshots() {
    return snapshots;
  }

  private Map<Timestamp, CacheValue> snapshots = new HashMap<>();
  private Lock lock = new Lock();

  public CacheValue getNearestTimestampValue(Timestamp timestamp) {
    Timestamp[] timestamps = snapshots.keySet().toArray(new Timestamp[snapshots.keySet().size()]);
    sort(timestamps);
    Timestamp keyTimestamp = BinarySearcher.greatestIndexNotExceeding(timestamps, timestamp);
    if (keyTimestamp != null) {
      return snapshots.get(keyTimestamp);
    } else {
      return null;
    }
  }

  public synchronized String getLock() {
    while (lock.isLocked()) {
    }
    return lock.lock();
  }

  public synchronized boolean unlock(String lockId) {
    return lock.unlock(lockId);
  }

  @Override
  public String toString() {
    return "Snapshot{" +
            "snapshots=" + snapshots +
            ", lock=" + lock +
            '}';
  }
}
