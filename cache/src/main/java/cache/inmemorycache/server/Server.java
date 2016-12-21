package cache.inmemorycache.server;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

import cache.inmemorycache.server.commands.CommandFactory;
import cache.inmemorycache.server.config.DataStoreModule;
import cache.inmemorycache.server.config.ServerModule;

public class Server {
  private static final String CONFIG_FILE = "/config.properties";
  @Inject
  private CommandFactory commandFactory;
  private ServerSocket serverSocket;
  private int port;

  public static void main(String[] args) throws IOException, ClassNotFoundException {
    Injector injector = Guice.createInjector(new ServerModule(), new DataStoreModule());
    Server main = injector.getInstance(Server.class);
    main.run();
  }

  private void run() throws IOException, ClassNotFoundException {
    readConfigFile();
    serverSocket = new ServerSocket(port);
    while (true) {
      Socket socket = serverSocket.accept();
      new CacheRequestHandlerThread(socket, commandFactory).start();
    }
  }

  private void readConfigFile() throws IOException {
    Properties prop = new Properties();
    InputStream is = getClass().getResourceAsStream(CONFIG_FILE);
    prop.load(is);
    port = Integer.parseInt(prop.getProperty("port"));
  }
}
