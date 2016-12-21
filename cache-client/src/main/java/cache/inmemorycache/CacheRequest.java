package cache.inmemorycache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CacheRequest implements Serializable {
  private String commandId;
  private String transactionId;

  public List<String> getOptions() {
    return options;
  }

  public void setOptions(List<String> options) {
    this.options = options;
  }

  List<String> options = new ArrayList<>();

  public String getCommandId() {
    return commandId;
  }

  public void setCommandId(String commandId) {
    this.commandId = commandId;
  }

  public String getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(String transactionId) {
    this.transactionId = transactionId;
  }
}
