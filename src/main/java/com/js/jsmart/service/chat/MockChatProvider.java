package com.js.jsmart.service.chat;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Offline FAQ provider. Works without internet/API key.
 */
public class MockChatProvider implements ChatProvider {
  private static final Map<String, String> FAQ = new LinkedHashMap<>();

  static {
    FAQ.put("return", "JS Mart returns: unused items can be returned within 7 days of delivery. Contact support with your order ID.");
    FAQ.put("refund", "Refunds are issued to the original payment method within 5-7 business days after we receive the return.");
    FAQ.put("track", "Track your order under Orders > View Details. Status flows PENDING > CONFIRMED > SHIPPED > DELIVERED.");
    FAQ.put("ship", "Shipping takes 2-5 business days. Orders are confirmed by sellers before dispatch.");
    FAQ.put("payment", "Checkout uses a mock payment confirmation for this capstone - no real money moves.");
    FAQ.put("review", "You can review a product only after its order is DELIVERED, one review per product.");
    FAQ.put("seller", "Sellers manage listings from Seller Dashboard: create, edit stock/price, or delete their own products.");
    FAQ.put("discount", "Current demo promo: use code JSMART10 for 10% off at mock checkout.");
    FAQ.put("account", "Register as BUYER or SELLER. Admin accounts are created by seed data only.");
    FAQ.put("contact", "Reach us at support@jsmart.local. This is a demo storefront.");
  }

  @Override
  public String getReply(String userMessage, String context) {
    if (userMessage == null || userMessage.isBlank()) {
      return "Hi! Ask me about orders, shipping, returns, reviews or selling on JS Mart.";
    }
    String lower = userMessage.toLowerCase(Locale.ROOT);
    for (Map.Entry<String, String> e : FAQ.entrySet()) {
      if (lower.contains(e.getKey())) {
        return e.getValue();
      }
    }
    if (lower.contains("hi") || lower.contains("hello")) {
      return "Hello! Welcome to JS Mart. Ask about tracking, returns, payments or reviews.";
    }
    return "Thanks for asking about JS Mart! I can help with orders, shipping, returns, payments, reviews and selling."
        + (context == null || context.isBlank() ? "" : " Context: " + context);
  }
}
