package cache.inmemorycache.server.internal.transaction;

import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cache.inmemorycache.server.internal.datastore.CacheValue;
import cache.inmemorycache.server.internal.datastore.Snapshot;

@Singleton
public class TransactionManager implements ITransactionManager {
  private Map<String, Transaction> transactions = new HashMap<>();
  private List<Transaction> inaciveTransactions = new ArrayList<>();

  @Override
  public synchronized Transaction getTransaction(String transactionId) {
    return transactions.get(transactionId);
  }

  @Override
  public synchronized Transaction createTransaction(Map<String, CacheValue> inMemoryMap, Map<String, Snapshot> snapshots, Map<String, Integer> valueCount) {
    if (inaciveTransactions.size() == 0) {
      String transactionId = UUID.randomUUID().toString();
      Transaction transaction = new Transaction(transactionId, inMemoryMap, snapshots, valueCount);
      transactions.put(transactionId, transaction);
      return transaction;
    } else {
      return inaciveTransactions.remove(0);
    }
  }

  @Override
  public boolean isValidTransaction(String transactionId) {
    return transactions.containsKey(transactionId);
  }

  @Override
  public void removeTransaction(Transaction transaction) {
    transactions.remove(transaction.getId());
  }

  @Override
  public String toString() {
    return "TransactionManager{" +
            "transactions=" + transactions +
            ", inaciveTransactions=" + inaciveTransactions +
            '}';
  }
}
