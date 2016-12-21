package cache.inmemorycache.server.config;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import cache.inmemorycache.server.internal.datastore.DataStore;
import cache.inmemorycache.server.internal.datastore.IDataStore;
import cache.inmemorycache.server.internal.transaction.ITransactionManager;
import cache.inmemorycache.server.internal.transaction.TransactionManager;

public class DataStoreModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(IDataStore.class).to(DataStore.class).in(Singleton.class);
    bind(ITransactionManager.class).to(TransactionManager.class).in(Singleton.class);
  }
}
