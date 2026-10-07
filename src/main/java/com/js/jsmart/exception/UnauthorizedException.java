package com.js.jsmart.exception;

/** 401 unauthenticated. */
public class UnauthorizedException extends AppException {
  public UnauthorizedException(String message) { super(401, "UNAUTHORIZED", message); }
}
