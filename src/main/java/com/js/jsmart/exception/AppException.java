package com.js.jsmart.exception;

/** Base application exception carrying an HTTP status + error code. */
public class AppException extends RuntimeException {
  private final int httpStatus;
  private final String code;

  public AppException(int httpStatus, String code, String message) {
    super(message);
    this.httpStatus = httpStatus;
    this.code = code;
  }

  public int getHttpStatus() { return httpStatus; }
  public String getCode() { return code; }
}
