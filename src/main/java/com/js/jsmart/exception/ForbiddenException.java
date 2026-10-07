package com.js.jsmart.exception;

/** 403 forbidden. */
public class ForbiddenException extends AppException {
  public ForbiddenException(String message) { super(403, "FORBIDDEN", message); }
}
