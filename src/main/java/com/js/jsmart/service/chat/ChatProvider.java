package com.js.jsmart.service.chat;

/**
 * Strategy interface for chat providers.
 */
public interface ChatProvider {
  /**
   * Get reply.
   * @param userMessage user message
   * @param context server-side context (product/listing summary)
   * @return reply text
   */
  String getReply(String userMessage, String context);
}
