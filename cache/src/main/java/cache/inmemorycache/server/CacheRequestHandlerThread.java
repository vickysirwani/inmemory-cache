package cache.inmemorycache.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import cache.inmemorycache.CacheRequest;
import cache.inmemorycache.CacheResponse;
import cache.inmemorycache.server.commands.BaseCommand;
import cache.inmemorycache.server.commands.CommandFactory;

class CacheRequestHandlerThread extends Thread {
  private static final String INVALID_COMMAND = "Invalid Command";
  private final CommandFactory commandFactory;
  private Socket socket;


  CacheRequestHandlerThread(Socket socket, CommandFactory commandFactory) {
    this.socket = socket;
    this.commandFactory = commandFactory;
  }

  public void run() {
    ObjectInputStream iStream = getObjectInputStream();
    ObjectOutputStream oStream = getObjectOutputStream();
    CacheRequest cacheRequest = getCacheRequest(iStream);
    cacheRequest.setCommandId(cacheRequest.getCommandId().toUpperCase());
    CacheResponse cacheResponse = null;
    if (commandFactory.isValidCommand(cacheRequest.getCommandId())) {
      BaseCommand command = commandFactory.getBaseCommand(cacheRequest.getCommandId()).withRequest(cacheRequest, oStream);

      try {
        command.validateCacheRequest();
        cacheResponse = command.run();
      } catch (InterruptedException | CloneNotSupportedException e) {
        e.printStackTrace();
      } catch (IllegalArgumentException e) {
        cacheResponse = CacheResponse.createErrorResponse(e.getLocalizedMessage(), cacheRequest.getTransactionId());
      } catch (IllegalStateException e) {
        cacheResponse = CacheResponse.createErrorResponse(e.getLocalizedMessage(), null);
      }
    } else {
      cacheResponse = CacheResponse.createErrorResponse(INVALID_COMMAND, cacheRequest.getTransactionId());
    }
    sendCacheResponse(oStream, cacheResponse);

  }

  private void sendCacheResponse(ObjectOutputStream oStream, CacheResponse cacheResponse) {
    try {
      oStream.writeObject(cacheResponse);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private CacheRequest getCacheRequest(ObjectInputStream iStream) {
    CacheRequest cacheRequest = null;
    try {
      assert iStream != null;
      cacheRequest = (CacheRequest) iStream.readObject();
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
    assert cacheRequest != null;
    return cacheRequest;
  }

  private ObjectOutputStream getObjectOutputStream() {
    ObjectOutputStream oStream = null;
    try {
      oStream = new ObjectOutputStream(socket.getOutputStream());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return oStream;
  }

  private ObjectInputStream getObjectInputStream() {
    ObjectInputStream iStream = null;
    try {
      iStream = new ObjectInputStream(socket.getInputStream());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return iStream;
  }
}
