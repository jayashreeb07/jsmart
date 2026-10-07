package com.js.jsmart.controller;

import com.js.jsmart.dao.DAOFactory;
import com.js.jsmart.dto.RegisterRequest;
import com.js.jsmart.dto.UserResponseDTO;
import com.js.jsmart.service.AuthService;
import com.js.jsmart.util.JsonUtil;
import com.js.jsmart.dto.ApiResponse;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** POST /api/v1/auth/register : buyer/seller registration. */
@WebServlet("/api/v1/auth/register")
public class RegisterServlet extends BaseServlet {
  private AuthService auth = new AuthService(DAOFactory.users());

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      RegisterRequest r = JsonUtil.fromBody(req, RegisterRequest.class);
      if (r == null) {
        r = new RegisterRequest();
        r.setEmail(req.getParameter("email"));
        r.setPassword(req.getParameter("password"));
        r.setFullName(req.getParameter("fullName"));
        r.setRole(req.getParameter("role"));
      }
      UserResponseDTO out = auth.register(r);
      // form-post flow: redirect to login
      boolean isForm = req.getParameter("email") != null
          && (req.getContentType() == null || !req.getContentType().contains("json"));
      if (isForm) {
        resp.sendRedirect(req.getContextPath() + "/login.jsp?registered=1");
        return;
      }
      JsonUtil.write(resp, 201, ApiResponse.ok(out));
    } catch (Exception e) {
      sendError(resp, e);
    }
  }

  /** Visible for tests. */
  public void setAuthService(AuthService auth) {
    this.auth = auth;
  }
}
