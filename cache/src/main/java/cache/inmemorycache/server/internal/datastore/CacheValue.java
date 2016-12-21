package cache.inmemorycache.server.internal.datastore;

import java.sql.Timestamp;

public class CacheValue implements Cloneable{

  public Timestamp getTimestamp() {
    return timestamp;
  }

  private Timestamp timestamp = new Timestamp(System.currentTimeMillis());

  public String getData() {
    return data;
  }

  public synchronized void setData(String data) {
    this.data = data;
    incrementVersion();
    this.timestamp = new Timestamp(System.currentTimeMillis());
  }

  private synchronized void incrementVersion() {
    version++;
  }

  private String data;

  public long getVersion() {
    return version;
  }

  private long version = 0;
  private Lock lock = new Lock();
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
    return "CacheValue{" +
            "data='" + data + '\'' +
            ", version=" + version +
            ", lock=" + lock +
            '}';
  }

  public CacheValue clone() throws CloneNotSupportedException {
    return (CacheValue) super.clone();
  }
}
