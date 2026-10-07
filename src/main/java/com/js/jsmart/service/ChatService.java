package com.js.jsmart.service.chat;

import com.js.jsmart.util.ConfigUtil;
import java.util.HashMap;
import java.util.Map;

/**
 * Chat service: provider selection (Factory), rate limit, length cap, per-session cache.
 */
public class ChatService {
  private final ChatProvider provider;
  private final Map<String, String> cache = new HashMap<>();

  /** Build from config ai.chatbot.provider. */
  public ChatService() {
    String name = ConfigUtil.get("ai.chatbot.provider", "mock");
    int timeout;
    try {
      timeout = Integer.parseInt(ConfigUtil.get("gemini.api.timeoutMs", "8000"));
    } catch (NumberFormatException e) {
      timeout = 8000;
    }
    if ("gemini".equalsIgnoreCase(name)) {
      this.provider = new GeminiChatProvider(timeout);
    } else {
      this.provider = new MockChatProvider();
    }
  }

  /** Test constructor. */
  public ChatService(ChatProvider provider) {
    this.provider = provider;
  }

  /**
   * Reply with guards.
   * @param session session map for rate-limit + cache
   * @param message user message
   * @param context product context
   * @return reply
   */
  public String reply(Map<String, Object> session, String message, String context) {
    if (message == null) {
      message = "";
    }
    message = message.trim();
    if (message.length() > 500) {
      message = message.substring(0, 500);
    }
    long now = System.currentTimeMillis();
    @SuppressWarnings("unchecked")
    java.util.List<Long> hits = (java.util.List<Long>) session.computeIfAbsent("chatHits",
        k -> new java.util.ArrayList<Long>());
    hits.removeIf(t -> now - t > 60_000);
    if (hits.size() >= 10) {
      return "Rate limit: please wait a moment (10 messages/minute).";
    }
    hits.add(now);
    String key = message.toLowerCase();
    if (cache.containsKey(key)) {
      return cache.get(key);
    }
    String out = provider.getReply(message, context);
    cache.put(key, out);
    return out;
  }
}
