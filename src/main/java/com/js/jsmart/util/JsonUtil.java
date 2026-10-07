package com.js.jsmart.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import java.io.BufferedReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Gson JSON helper with fixed envelope support.
 */
public final class JsonUtil {
  private static final Gson GSON = new GsonBuilder().serializeNulls()
      .registerTypeAdapter(LocalDateTime.class,
          (JsonSerializer<LocalDateTime>) (v, t, c) -> v == null ? null
              : new JsonPrimitive(v.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
      .registerTypeAdapter(LocalDateTime.class,
          (JsonDeserializer<LocalDateTime>) (j, t, c) -> j == null || j.isJsonNull() ? null
              : LocalDateTime.parse(j.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
      .create();

  private JsonUtil() { }

  /** Shared Gson instance. */
  public static Gson gson() {
    return GSON;
  }

  /** Coerce Gson-parsed JSON value (Number or String) to long. */
  public static long toLong(Object v) {
    if (v instanceof Number) {
      return ((Number) v).longValue();
    }
    return Long.parseLong(String.valueOf(v));
  }

  /** Coerce Gson-parsed JSON value (Number or String) to int. */
  public static int toInt(Object v) {
    if (v instanceof Number) {
      return ((Number) v).intValue();
    }
    return Integer.parseInt(String.valueOf(v));
  }

  /** Parse JSON body into class. Returns null for non-JSON or empty bodies. */
  public static <T> T fromBody(HttpServletRequest req, Class<T> cls) throws java.io.IOException {
    String ct = req.getContentType();
    if (ct == null || !ct.contains("json")) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    try (BufferedReader r = req.getReader()) {
      String line;
      while ((line = r.readLine()) != null) {
        sb.append(line);
      }
    }
    if (sb.length() == 0) {
      return null;
    }
    return GSON.fromJson(sb.toString(), cls);
  }

  /** Write object as JSON with status. */
  public static void write(HttpServletResponse resp, int status, Object body) throws java.io.IOException {
    resp.setStatus(status);
    resp.setContentType("application/json;charset=UTF-8");
    resp.getWriter().write(GSON.toJson(body));
  }
}
