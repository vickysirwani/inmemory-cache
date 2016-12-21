package cache.inmemorycache.server.commands;

import com.google.inject.Inject;

import cache.inmemorycache.CacheResponse;
import cache.inmemorycache.server.internal.datastore.IDataStore;

public class CommitCommand extends BaseCommand {


  private static final String INVALID_TRANSACTION = "Invalid Transaction";

  @Inject
  public CommitCommand(IDataStore dataStore) {
    super(dataStore);
  }

  @Override
  public CacheResponse run() {
    if (!dataStore.isValidTransaction(cacheRequest.getTransactionId())) {
      return CacheResponse.createErrorResponse(INVALID_TRANSACTION, null);
    }
    if (cacheRequest.getTransactionId() != null) {
      dataStore.commit(cacheRequest.getTransactionId());
      cacheResponse.setStatus(true);
      cacheResponse.setResult("Success");
    } else {
      cacheResponse.setStatus(false);
      cacheResponse.setErrorResponse("Invalid transaction");
    }
    cacheResponse.setTransactionId(null);
    return cacheResponse;
  }

  @Override
  public void validateCacheRequest() {

  }

  @Override
  protected void initializeArguments() {

  }
}
