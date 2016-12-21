package cache.inmemorycache.server.commands;

import com.google.inject.Inject;

import java.io.ObjectOutputStream;

import cache.inmemorycache.CacheRequest;
import cache.inmemorycache.CacheResponse;
import cache.inmemorycache.server.internal.datastore.IDataStore;

public abstract class BaseCommand {
  private static final String INVALID_TRANSACTION = "Invalid transaction";
  protected IDataStore dataStore;
  protected CacheRequest cacheRequest;
  protected CacheResponse cacheResponse = new CacheResponse();
  protected ObjectOutputStream oStream;

  @Inject
  public BaseCommand(IDataStore dataStore) {
    this.dataStore = dataStore;
  }


  public BaseCommand withRequest(CacheRequest cacheRequest, ObjectOutputStream oStream) {
    this.cacheRequest = cacheRequest;
    this.oStream = oStream;
    initializeArguments();
    dataStore.print();
    cacheResponse.setTransactionId(cacheRequest.getTransactionId());
    if (cacheRequest.getTransactionId()!= null && !dataStore.isValidTransaction(cacheRequest.getTransactionId())) {
      throw new IllegalArgumentException(INVALID_TRANSACTION);
    }
    return this;
  }

  public abstract CacheResponse run() throws InterruptedException, CloneNotSupportedException;

  public abstract void validateCacheRequest() throws IllegalArgumentException;

  protected abstract void initializeArguments();
}
