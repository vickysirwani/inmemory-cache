package cache.inmemorycache.server.commands;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Map;

@Singleton
public class CommandFactory {

  private final Map<String, BaseCommand> baseCommandMap;

  @Inject
  public CommandFactory(Map<String, BaseCommand> baseCommandMap) {
    this.baseCommandMap = baseCommandMap;
  }

  public BaseCommand getBaseCommand(String command) {
    return baseCommandMap.get(command);
  }

  public boolean isValidCommand(String command) {
    return baseCommandMap.containsKey(command);
  }

}

