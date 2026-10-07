package com.js.jsmart.service.chat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gemini provider. API key stays server-side; failures fall back to degraded response.
 */
public class GeminiChatProvider implements ChatProvider {
  private static final Logger LOG = LoggerFactory.getLogger(GeminiChatProvider.class);
  private final MockChatProvider fallback = new MockChatProvider();
  private final int timeoutMs;

  /** Create provider. */
  public GeminiChatProvider(int timeoutMs) {
    this.timeoutMs = timeoutMs;
  }

  @Override
  public String getReply(String userMessage, String context) {
    String key = System.getenv("GEMINI_API_KEY");
    if (key == null || key.isBlank()) {
      return fallback.getReply(userMessage, context) + " (offline mode)";
    }
    try {
      String prompt = "You are the JS Mart store assistant. Only answer JS Mart product/listing/order questions."
          + " If unrelated, politely decline. Keep replies under 120 words.\nContext: "
          + (context == null ? "" : context) + "\nUser: " + userMessage;
      URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + key);
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setConnectTimeout(timeoutMs);
      con.setReadTimeout(timeoutMs);
      con.setRequestMethod("POST");
      con.setRequestProperty("Content-Type", "application/json");
      con.setDoOutput(true);
      JsonObject part = new JsonObject();
      part.addProperty("text", prompt);
      JsonObject content = new JsonObject();
      JsonArray parts = new JsonArray();
      parts.add(part);
      content.add("parts", parts);
      JsonObject body = new JsonObject();
      JsonArray contents = new JsonArray();
      contents.add(content);
      body.add("contents", contents);
      byte[] bytes = body.toString().getBytes(StandardCharsets.UTF_8);
      try (OutputStream os = con.getOutputStream()) {
        os.write(bytes);
      }
      int code = con.getResponseCode();
      if (code != 200) {
        throw new RuntimeException("Gemini HTTP " + code);
      }
      String resp;
      try (Scanner s = new Scanner(con.getInputStream(), StandardCharsets.UTF_8).useDelimiter("\\A")) {
        resp = s.hasNext() ? s.next() : "";
      }
      JsonObject json = JsonParser.parseString(resp).getAsJsonObject();
      String text = json.getAsJsonArray("candidates").get(0).getAsJsonObject()
          .getAsJsonObject("content").getAsJsonArray("parts").get(0).getAsJsonObject()
          .get("text").getAsString();
      return text;
    } catch (Exception e) {
      LOG.warn("Gemini call failed, using degraded response", e);
      return "JS Mart assistant is in offline mode right now. " + fallback.getReply(userMessage, context);
    }
  }
}
