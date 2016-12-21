package cache.inmemorycache.server.commands;

import com.google.inject.Inject;

import cache.inmemorycache.CacheResponse;
import cache.inmemorycache.server.internal.datastore.CacheValue;
import cache.inmemorycache.server.internal.datastore.IDataStore;

public class GetCommand extends BaseCommand {

  private static final String INVALID_TRANSACTION_ERROR = "Invalid Transaction";
  private static final String KEY_NOT_FOUND_ERROR = "Key not found";
  private static final String INVALID_NUMBER_OF_ARGUMENTS = "Invalid number of arguments";
  private String key;

  private String transactionId;

  @Inject
  public GetCommand(IDataStore dataStore) {
    super(dataStore);
  }

  @Override
  public CacheResponse run() {
    CacheValue cacheValue = null;
    if (cacheRequest.getTransactionId() != null && !isValidTransaction()) {
      cacheResponse = createInvalidTransactionResponse();
    } else if (cacheRequest.getTransactionId() != null) {
      cacheValue = dataStore.get(key, cacheRequest.getTransactionId());
    } else {
      cacheValue = dataStore.get(key);
    }
    cacheResponse = createCacheResponse(cacheValue);
    return cacheResponse;
  }

  private CacheResponse createCacheResponse(CacheValue cacheValue) {
    if (cacheValue == null) {
      return CacheResponse.createErrorResponse(KEY_NOT_FOUND_ERROR, transactionId);
    } else {
      return CacheResponse.createSuccessResponse(cacheValue.getData(), transactionId);
    }
  }

  private CacheResponse createInvalidTransactionResponse() {
    return CacheResponse.createErrorResponse(INVALID_TRANSACTION_ERROR, transactionId);
  }

  private boolean isValidTransaction() {
    return dataStore.isValidTransaction(cacheRequest.getTransactionId());
  }

  public void validateCacheRequest() throws IllegalArgumentException {
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
