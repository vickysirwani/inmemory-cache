package cache.inmemorycache.server.internal.datastore;

import java.util.Objects;
import java.util.UUID;

public class Lock {
  private Boolean locked = false;
  private String lockId;

  public boolean isLocked() {
    return locked;
  }

  public String lock() {
    if (locked) {
      throw new IllegalStateException();
    } else {
      String lockId = UUID.randomUUID().toString();
      this.lockId = lockId;
      this.locked = true;
      return lockId;
    }
  }

  public boolean unlock(String lockId) {
    if (Objects.equals(this.lockId, lockId)) {
      this.locked = false;
      this.lockId = null;
      return true;
    }
    return false;
  }

  @Override
  public String toString() {
    return "Lock{" +
            "locked=" + locked +
            ", lockId='" + lockId + '\'' +
            '}';
  }
}
