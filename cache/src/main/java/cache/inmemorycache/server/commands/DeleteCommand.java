package cache.inmemorycache.server.commands;

import com.google.inject.Inject;

import cache.inmemorycache.CacheResponse;
import cache.inmemorycache.server.internal.datastore.IDataStore;

public class DeleteCommand extends BaseCommand {


  private static final String INVALID_NUMBER_OF_ARGUMENTS = "Invalid number of arguments";
  private String key;
  private String transactionId;

  @Inject
  public DeleteCommand(IDataStore dataStore) {
    super(dataStore);
  }

  @Override
  public CacheResponse run() throws CloneNotSupportedException {
    if (cacheRequest.getTransactionId() != null) {
      dataStore.delete(key, transactionId);
    } else {
      dataStore.delete(key);
    }
    cacheResponse.setTransactionId(transactionId);
    cacheResponse.setStatus(true);
    return cacheResponse;
  }

  @Override
  public void validateCacheRequest() {
    if (cacheRequest.getOptions().size() != 1) {
      throw new IllegalArgumentException(INVALID_NUMBER_OF_ARGUMENTS);
    }
  }

  @Override
  protected void initializeArguments() {
    if (cacheRequest.getOptions().size() == 1) {
      key = cacheRequest.getOptions().get(0);
      transactionId = cacheRequest.getTransactionId();
    }
  }


}
