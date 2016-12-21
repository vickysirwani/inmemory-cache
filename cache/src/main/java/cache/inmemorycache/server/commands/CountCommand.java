package cache.inmemorycache.server.commands;

import com.google.inject.Inject;

import cache.inmemorycache.CacheResponse;
import cache.inmemorycache.server.internal.datastore.IDataStore;

public class CountCommand extends BaseCommand {


  private String value;
  private static final String INVALID_NUMBER_OF_ARGUMENTS = "Invalid number of arguments";

  @Inject
  public CountCommand(IDataStore dataStore) {
    super(dataStore);
  }

  @Override
  public CacheResponse run() {
    int count;
    if (cacheRequest.getTransactionId() != null) {
      count = dataStore.getCount(value, cacheRequest.getTransactionId());
    } else {
      count = dataStore.getCount(value);
    }
    cacheResponse.setStatus(true);
    cacheResponse.setResult(String.valueOf(count));
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
    value = cacheRequest.getOptions().get(0);
  }
}
