package cache.inmemorycache.server;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;

import cache.inmemorycache.server.commands.CommandFactory;
import cache.inmemorycache.server.config.ServerModule;
import cache.inmemorycache.server.internal.datastore.config.DataStoreModule;


public class Server {
  @Inject
  private CommandFactory commandFactory;
  private ServerSocket serverSocket;
  public static void main(String[] args) throws IOException, ParseException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
    Injector injector = Guice.createInjector(new ServerModule(), new DataStoreModule());
    Server main = injector.getInstance(Server.class);
    main.run();
  }

  private void run() throws IOException, ClassNotFoundException {
    int port = 6379;
    serverSocket = new ServerSocket(port);
    while (true) {
      System.out.println("waiting");
      Socket socket = serverSocket.accept();
      new CacheRequestHandlerThread(socket, commandFactory).start();
    }
  }
}
