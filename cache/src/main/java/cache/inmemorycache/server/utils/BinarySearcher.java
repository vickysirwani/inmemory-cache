package cache.inmemorycache.server.utils;

import java.sql.Timestamp;

public class BinarySearcher {

  public static Timestamp greatestIndexNotExceeding(Timestamp[] data, Timestamp limit) {
    if (data.length < 1) {
      return null;
    }
    return greatestIndexNotExceeding(data, limit, 0, data.length - 1);
  }

  private static Timestamp greatestIndexNotExceeding(Timestamp[] data, Timestamp limit, int lb, int ub) {
    final int mid = (lb + ub) / 2;

    if (mid == lb && data[mid].after(limit)) {
      return null;
    }

    if ((data[mid].before(limit) || data[mid].equals(limit)) && (mid == ub || data[mid + 1].after(limit))) {
      return data[mid];
    }

    if (data[mid].before(limit) || data[mid].equals(limit)) {
      return greatestIndexNotExceeding(data, limit, mid + 1, ub);
    } else {
      return greatestIndexNotExceeding(data, limit, lb, mid);
    }
  }
}
