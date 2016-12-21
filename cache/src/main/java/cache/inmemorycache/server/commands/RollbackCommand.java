package cache.inmemorycache.server.commands;

import com.google.inject.Inject;

import cache.inmemorycache.CacheResponse;
import cache.inmemorycache.server.internal.datastore.IDataStore;

public class RollbackCommand extends BaseCommand {

  private static final String INVALID_TRANSACTION = "Invalid Transaction";

  @Inject
  public RollbackCommand(IDataStore dataStore) {
    super(dataStore);
  }


  @Override
  public CacheResponse run() {
    if (cacheRequest.getTransactionId() == null) {
      return CacheResponse.createErrorResponse(INVALID_TRANSACTION, null);
    } else {
      dataStore.rollback(cacheRequest.getTransactionId());
      cacheResponse.setStatus(true);
      cacheResponse.setTransactionId(null);
    }
    return cacheResponse;
  }

  @Override
  public void validateCacheRequest() {

  }

  @Override
  protected void initializeArguments() {

  }
}
