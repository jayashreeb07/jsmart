package com.js.jsmart.dto;

import java.util.LinkedHashMap;
import java.util.Map;

/** Fixed JSON envelope: {success, data, error{code,message,fields}}. */
public class ApiResponse {
  private boolean success;
  private Object data;
  private Map<String, Object> error;

  public static ApiResponse ok(Object data) {
    ApiResponse r = new ApiResponse();
    r.success = true;
    r.data = data;
    r.error = null;
    return r;
  }

  public static ApiResponse fail(String code, String message) {
    return fail(code, message, null);
  }

  public static ApiResponse fail(String code, String message, Map<String, String> fields) {
    ApiResponse r = new ApiResponse();
    r.success = false;
    r.data = null;
    Map<String, Object> e = new LinkedHashMap<>();
    e.put("code", code);
    e.put("message", message);
    if (fields != null) {
      e.put("fields", fields);
    }
    r.error = e;
    return r;
  }

  public boolean isSuccess() { return success; }
  public Object getData() { return data; }
  public Map<String, Object> getError() { return error; }
}
