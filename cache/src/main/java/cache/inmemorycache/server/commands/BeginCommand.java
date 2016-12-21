package cache.inmemorycache.server.commands;

import com.google.inject.Inject;

import cache.inmemorycache.CacheResponse;
import cache.inmemorycache.server.internal.datastore.IDataStore;

public class BeginCommand extends BaseCommand {


  @Inject
  public BeginCommand(IDataStore dataStore) {
    super(dataStore);
  }

  @Override
  public CacheResponse run() {
    cacheResponse.setTransactionId(dataStore.begin().getId());
    cacheResponse.setStatus(true);
    return cacheResponse;
  }

  @Override
  public void validateCacheRequest() {

  }

  @Override
  protected void initializeArguments() {

  }
}
