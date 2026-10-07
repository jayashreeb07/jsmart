package com.js.jsmart.filter;

import com.js.jsmart.model.User;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Authentication + role authorization filter (server-side, never UI-only).
 */
public class AuthFilter implements Filter {
  @Override
  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest h = (HttpServletRequest) req;
    HttpServletResponse r = (HttpServletResponse) resp;
    String path = h.getRequestURI().substring(h.getContextPath().length());
    HttpSession s = h.getSession(false);
    User u = s == null ? null : (User) s.getAttribute("user");
    if (u == null) {
      if (path.startsWith("/api/")) {
        r.setStatus(401);
        r.setContentType("application/json;charset=UTF-8");
        r.getWriter().write("{\"success\":false,\"data\":null,"
            + "\"error\":{\"code\":\"UNAUTHORIZED\",\"message\":\"Login required\"}}");
      } else {
        r.sendRedirect(h.getContextPath() + "/login.jsp?next=" + h.getRequestURI());
      }
      return;
    }
    String role = u.getRole().name();
    if ((path.startsWith("/seller") || path.startsWith("/api/v1/seller"))
        && !"SELLER".equals(role) && !"ADMIN".equals(role)) {
      deny(h, r, path);
      return;
    }
    if ((path.startsWith("/admin") || path.startsWith("/api/v1/admin")) && !"ADMIN".equals(role)) {
      deny(h, r, path);
      return;
    }
    chain.doFilter(req, resp);
  }

  private void deny(HttpServletRequest h, HttpServletResponse r, String path) throws IOException {
    if (path.startsWith("/api/")) {
      r.setStatus(403);
      r.setContentType("application/json;charset=UTF-8");
      r.getWriter().write("{\"success\":false,\"data\":null,"
          + "\"error\":{\"code\":\"FORBIDDEN\",\"message\":\"Insufficient role\"}}");
    } else {
      r.sendError(403, "Forbidden");
    }
  }
}
