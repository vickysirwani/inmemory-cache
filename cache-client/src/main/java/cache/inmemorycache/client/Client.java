package cache.inmemorycache.client;

import com.google.inject.Guice;
import com.google.inject.Injector;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;

import cache.inmemorycache.CacheRequest;
import cache.inmemorycache.CacheResponse;

public class Client {

  private static final String REPL_STRING = ">>> ";
  private static final String WHITE_STRING_REGEX = "\\s+";
  private static final String CONFIG_FILE = "/config.properties";
  private int port;
  private String host;

  public static void main(String[] args) throws IOException, ClassNotFoundException {
    Injector injector = Guice.createInjector();
    Client main = injector.getInstance(Client.class);
    main.run();
  }

  private void run() throws IOException, ClassNotFoundException {
    readConfigFile();
    handleInteractiveShellCommands();
  }

  private void handleInteractiveShellCommands() throws IOException, ClassNotFoundException {
    String displayString = getDisplayString();
    System.out.print(displayString);
    CacheRequest cacheRequest;
    CacheResponse cacheResponse = new CacheResponse();
    Scanner scanner = new Scanner(System.in);
    while (scanner.hasNextLine()) {
      Socket clientSocket = new Socket(host, port);
      String commandString = scanner.nextLine();
      cacheRequest = getCacheRequest(commandString, cacheResponse);
      ObjectOutputStream oStream = new ObjectOutputStream(clientSocket.getOutputStream());
      oStream.writeObject(cacheRequest);

      ObjectInputStream iStream = new ObjectInputStream(clientSocket.getInputStream());
      cacheResponse = (CacheResponse) iStream.readObject();
      handleResponse(cacheResponse);
      System.out.print(displayString);
    }
  }

  private static void handleResponse(CacheResponse cacheResponse) {
    if (cacheResponse.isStatus() && cacheResponse.getResult() != null) {
      System.out.println(cacheResponse.getResult());
    } else if (!cacheResponse.isStatus()) {
      if (cacheResponse.getErrorResponse() != null) {
        System.out.println(cacheResponse.getErrorResponse());
      } else {
        System.out.println("Something went wrong");
      }
    }
  }

  private static CacheRequest getCacheRequest(String commandString, CacheResponse cacheResponse) {
    CacheRequest cacheRequest = new CacheRequest();
    if (cacheResponse.getTransactionId() != null) {
      cacheRequest.setTransactionId(cacheResponse.getTransactionId());
    }
    String[] commandStringSplit = commandString.split(WHITE_STRING_REGEX);
    cacheRequest.setCommandId(commandStringSplit[0]);
    for (int i = 1; i < commandStringSplit.length; i++) {
      cacheRequest.getOptions().add(commandStringSplit[i]);
    }
    return cacheRequest;
  }

  private static String getDisplayString() {
    return REPL_STRING;
  }

  private void readConfigFile() throws IOException {
    Properties prop = new Properties();
    InputStream is = getClass().getResourceAsStream(CONFIG_FILE);
    prop.load(is);
    port = Integer.parseInt(prop.getProperty("port"));
    host = prop.getProperty("host");
  }


}
