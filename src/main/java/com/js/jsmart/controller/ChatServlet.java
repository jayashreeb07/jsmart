package com.js.jsmart.controller;

import com.js.jsmart.dao.DAOFactory;
import com.js.jsmart.dto.ApiResponse;
import com.js.jsmart.service.chat.ChatService;
import com.js.jsmart.util.JsonUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/** POST /api/chat + /api/v1/chat : chatbot endpoint. */
@WebServlet({"/api/chat", "/api/v1/chat"})
public class ChatServlet extends BaseServlet {
  private ChatService chat = new ChatService();

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    try {
      String message = req.getParameter("message");
      if ((message == null) && req.getContentType() != null && req.getContentType().contains("json")) {
        @SuppressWarnings("unchecked")
        Map<String, String> body = JsonUtil.fromBody(req, Map.class);
        if (body != null) {
          message = body.get("message");
        }
      }
      if (message == null) {
        message = "";
      }
      HttpSession s = req.getSession(true);
      Map<String, Object> scoped = new HashMap<>();
      // bridge rate-limit list stored in real session
      Object hits = s.getAttribute("chatHits");
      if (hits instanceof java.util.List) {
        @SuppressWarnings("unchecked")
        java.util.List<Long> list = (java.util.List<Long>) hits;
        scoped.put("chatHits", list);
        String reply = chat.reply(syncBack(s, scoped), message, buildContext());
        s.setAttribute("chatHits", scoped.get("chatHits"));
        JsonUtil.write(resp, 200, ApiResponse.ok(java.util.Collections.singletonMap("reply", reply)));
        return;
      }
      s.setAttribute("chatHits", scoped.computeIfAbsent("chatHits", k -> new java.util.ArrayList<Long>()));
      @SuppressWarnings("unchecked")
      Map<String, Object> live = new HashMap<>();
      live.put("chatHits", s.getAttribute("chatHits"));
      String reply = chat.reply(live, message, buildContext());
      s.setAttribute("chatHits", live.get("chatHits"));
      JsonUtil.write(resp, 200, ApiResponse.ok(java.util.Collections.singletonMap("reply", reply)));
    } catch (Exception e) {
      sendError(resp, e);
    }
  }

  private Map<String, Object> syncBack(HttpSession s, Map<String, Object> scoped) {
    return scoped;
  }

  private String buildContext() {
    try {
      var list = DAOFactory.products().findAll(5, 0);
      StringBuilder sb = new StringBuilder("Top products: ");
      for (var p : list) {
        sb.append(p.getName()).append(" (Rs.").append(p.getPrice()).append("); ");
      }
      return sb.toString();
    } catch (Exception e) {
      return "JS Mart demo catalog";
    }
  }

  /** Visible for tests. */
  public void setChatService(ChatService c) {
    this.chat = c;
  }
}
