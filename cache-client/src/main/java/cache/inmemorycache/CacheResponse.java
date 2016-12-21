package cache.inmemorycache;

import java.io.Serializable;

public class CacheResponse implements Serializable {
  public static CacheResponse createErrorResponse(String error, String transactionId) {
    CacheResponse cacheResponse = new CacheResponse();
    cacheResponse.setStatus(false);
    cacheResponse.setErrorResponse(error);
    cacheResponse.setTransactionId(transactionId);
    return cacheResponse;
  }

  private java.lang.String transactionId;
  private java.lang.String errorResponse;

  public boolean isStatus() {
    return status;
  }

  public void setStatus(boolean status) {
    this.status = status;
  }

  public java.lang.String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  private boolean status = false;
  private java.lang.String result;

  public java.lang.String getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(java.lang.String transactionId) {
    this.transactionId = transactionId;
  }

  public java.lang.String getErrorResponse() {
    return errorResponse;
  }

  public void setErrorResponse(java.lang.String errorResponse) {
    this.errorResponse = errorResponse;
  }

  public static CacheResponse createSuccessResponse(String result, String transactionId) {
    CacheResponse cacheResponse = new CacheResponse();
    cacheResponse.setStatus(true);
    cacheResponse.setErrorResponse(result);
    cacheResponse.setTransactionId(transactionId);
    return cacheResponse;
  }
}
