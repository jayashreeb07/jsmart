package com.js.jsmart.exception;

/** 404 resource missing. */
public class NotFoundException extends AppException {
  public NotFoundException(String message) { super(404, "NOT_FOUND", message); }
}
