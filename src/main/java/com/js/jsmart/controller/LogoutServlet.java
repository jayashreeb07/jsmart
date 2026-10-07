package com.js.jsmart.controller;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/** Logout: invalidate session. */
@WebServlet({"/api/v1/auth/logout", "/logout"})
public class LogoutServlet extends BaseServlet {
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    HttpSession s = req.getSession(false);
    if (s != null) {
      s.invalidate();
    }
    // API callers keep JSON; browsers get flash + redirect.
    if (req.getRequestURI().contains("/api/")) {
      resp.setContentType("application/json;charset=UTF-8");
      resp.getWriter().write("{\"success\":true,\"data\":{\"message\":\"Logged out\"},\"error\":null}");
      return;
    }
    com.js.jsmart.util.Flash.success(req, "Logged out successfully.");
    resp.sendRedirect(req.getContextPath() + "/login.jsp");
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    doPost(req, resp);
  }
}
