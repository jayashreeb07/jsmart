package com.js.jsmart.controller;

import com.js.jsmart.dao.DAOFactory;
import com.js.jsmart.dto.ApiResponse;
import com.js.jsmart.dto.UserResponseDTO;
import com.js.jsmart.model.User;
import com.js.jsmart.service.AuthService;
import com.js.jsmart.util.ConfigUtil;
import com.js.jsmart.util.JsonUtil;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/** POST /api/v1/auth/login : session login with ID regeneration + timeout. */
@WebServlet("/api/v1/auth/login")
public class LoginServlet extends BaseServlet {
  private AuthService auth = new AuthService(DAOFactory.users());

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      String email = req.getParameter("email");
      String password = req.getParameter("password");
      if (email == null && req.getContentType() != null && req.getContentType().contains("json")) {
        @SuppressWarnings("unchecked")
        Map<String, String> body = JsonUtil.fromBody(req, Map.class);
        if (body != null) {
          email = body.get("email");
          password = body.get("password");
        }
      }
      User u = auth.login(email, password);
      HttpSession old = req.getSession(false);
      if (old != null) {
        old.invalidate();
      }
      HttpSession s = req.getSession(true);
      s.setAttribute("user", u);
      int timeoutMin = 30;
      try {
        timeoutMin = Integer.parseInt(ConfigUtil.get("session.timeoutMinutes", "30"));
      } catch (NumberFormatException e) {
        timeoutMin = 30;
      }
      s.setMaxInactiveInterval(timeoutMin * 60);
      boolean isForm = req.getParameter("email") != null
          && (req.getContentType() == null || !req.getContentType().contains("json"));
      if (isForm) {
        String role = u.getRole().name();
        if ("ADMIN".equals(role)) {
          resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
        } else if ("SELLER".equals(role)) {
          resp.sendRedirect(req.getContextPath() + "/seller/dashboard");
        } else {
          resp.sendRedirect(req.getContextPath() + "/index.jsp");
        }
        return;
      }
      JsonUtil.write(resp, 200, ApiResponse.ok(UserResponseDTO.from(u)));
    } catch (Exception e) {
      sendError(resp, e);
    }
  }

  /** Visible for tests. */
  public void setAuthService(AuthService auth) {
    this.auth = auth;
  }
}
