package com.js.jsmart.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Gson JSON helper with fixed envelope support.
 */
public final class JsonUtil {
  private static final Gson GSON = new GsonBuilder().serializeNulls().create();

  private JsonUtil() { }

  /** Shared Gson instance. */
  public static Gson gson() {
    return GSON;
  }

  /** Parse JSON body into class. */
  public static <T> T fromBody(HttpServletRequest req, Class<T> cls) throws java.io.IOException {
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
