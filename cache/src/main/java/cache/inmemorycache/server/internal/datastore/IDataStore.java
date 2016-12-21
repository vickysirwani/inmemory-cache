package cache.inmemorycache.server.internal.datastore;

import java.sql.Timestamp;

import cache.inmemorycache.server.internal.transaction.Transaction;

public interface IDataStore {

  public void set(String key, String value) throws InterruptedException, CloneNotSupportedException;

  CacheValue get(java.lang.String key);

  public Transaction begin();

  void set(String key, String value, String transactionId) throws CloneNotSupportedException;

  CacheValue get(String key, String transactionId);

  boolean containsKey(String key);


  boolean isValidTransaction(String transactionId);

  int getCount(String value);

  void commit(Transaction transaction);

  void commit(String transactionId);

  void delete(String key) throws CloneNotSupportedException;

  void delete(String key, String transactionId) throws CloneNotSupportedException;

  void print();

  Timestamp getTransactionTimestamp(String transactionId);

  void rollback(String transactionId);

  int getCount(String value, String transactionId);
}
