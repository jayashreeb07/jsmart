package com.js.jsmart.exception;

import java.util.Map;

/** Validation failure with field-level detail. */
public class ValidationException extends AppException {
  private final Map<String, String> fields;

  public ValidationException(String message, Map<String, String> fields) {
    super(400, "VALIDATION_ERROR", message);
    this.fields = fields;
  }

  public Map<String, String> getFields() { return fields; }
}
