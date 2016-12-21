package cache.inmemorycache.server.config;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;

import cache.inmemorycache.server.commands.BaseCommand;
import cache.inmemorycache.server.commands.BeginCommand;
import cache.inmemorycache.server.commands.CommandFactory;
import cache.inmemorycache.server.commands.CommitCommand;
import cache.inmemorycache.server.commands.CountCommand;
import cache.inmemorycache.server.commands.DeleteCommand;
import cache.inmemorycache.server.commands.GetCommand;
import cache.inmemorycache.server.commands.RollbackCommand;
import cache.inmemorycache.server.commands.SetCommand;

public class ServerModule extends AbstractModule {
  @Override
  protected void configure() {
    MapBinder<String, BaseCommand> myBinder =
            MapBinder.newMapBinder(binder(), String.class, BaseCommand.class);
    myBinder.addBinding("SET").to(SetCommand.class);
    myBinder.addBinding("GET").to(GetCommand.class);
    myBinder.addBinding("BEGIN").to(BeginCommand.class);
    myBinder.addBinding("COUNT").to(CountCommand.class);
    myBinder.addBinding("COMMIT").to(CommitCommand.class);
    myBinder.addBinding("ROLLBACK").to(RollbackCommand.class);
    myBinder.addBinding("DELETE").to(DeleteCommand.class);
    bind(CommandFactory.class);
  }

}
