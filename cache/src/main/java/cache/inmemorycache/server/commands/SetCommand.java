package cache.inmemorycache.server.commands;

import com.google.inject.Inject;

import cache.inmemorycache.CacheResponse;
import cache.inmemorycache.server.internal.datastore.IDataStore;

public class SetCommand extends BaseCommand {

  private static final String INVALID_NUMBER_OF_ARGUMENTS = "Invalid number of arguments";
  private String key;

  private String value;

  private String transactionId;

  @Inject
  public SetCommand(IDataStore dataStore) {
    super(dataStore);
  }

  @Override
  public CacheResponse run() throws CloneNotSupportedException, InterruptedException {
    if (cacheRequest.getTransactionId() != null) {
      dataStore.set(key, value, transactionId);
    } else {
      dataStore.set(key, value);
    }
    cacheResponse.setTransactionId(transactionId);
    cacheResponse.setStatus(true);
    return cacheResponse;
  }

  public void initializeArguments() {
    if (cacheRequest.getOptions().size() == 2) {
      key = cacheRequest.getOptions().get(0);
      value = cacheRequest.getOptions().get(1);
      transactionId = cacheRequest.getTransactionId();
    }
  }

  @Override
  public void validateCacheRequest() {
    if (cacheRequest.getOptions().size() != 2) {
      throw new IllegalArgumentException(INVALID_NUMBER_OF_ARGUMENTS);
    }
  }

}
