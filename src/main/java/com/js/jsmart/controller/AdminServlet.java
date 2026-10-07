package com.js.jsmart.controller;

import com.js.jsmart.dao.DAOFactory;
import com.js.jsmart.dto.ApiResponse;
import com.js.jsmart.model.User;
import com.js.jsmart.service.ProductService;
import com.js.jsmart.util.JsonUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Admin: users, orders, moderate listings (F7). */
@WebServlet({"/admin/dashboard", "/api/v1/admin/users", "/api/v1/admin/orders",
    "/api/v1/admin/products/*", "/api/v1/reviews/*"})
public class AdminServlet extends BaseServlet {
  private ProductService products = new ProductService(DAOFactory.products());

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String uri = req.getRequestURI().substring(req.getContextPath().length());
    try {
      User u = requireUser(req);
      if (!"ADMIN".equals(u.getRole().name())) {
        if (uri.startsWith("/api/")) {
          sendError(resp, new com.js.jsmart.exception.ForbiddenException("Admin only"));
        } else {
          resp.sendError(403);
        }
        return;
      }
      if ("/api/v1/admin/users".equals(uri)) {
        JsonUtil.write(resp, 200, ApiResponse.ok(DAOFactory.users().findAll().stream()
            .map(com.js.jsmart.dto.UserResponseDTO::from).toList()));
        return;
      }
      if ("/api/v1/admin/orders".equals(uri)) {
        JsonUtil.write(resp, 200, ApiResponse.ok(DAOFactory.orders().findAll()));
        return;
      }
      req.setAttribute("users", DAOFactory.users().findAll());
      req.setAttribute("orders", DAOFactory.orders().findAll());
      req.setAttribute("products", DAOFactory.products().findAll(50, 0));
      req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
    } catch (Exception e) {
      if (uri.startsWith("/api/")) {
        sendError(resp, e);
      } else {
        pageError(req, resp, e);
      }
    }
  }

  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    try {
      User u = requireUser(req);
      if (!"ADMIN".equals(u.getRole().name())) {
        sendError(resp, new com.js.jsmart.exception.ForbiddenException("Admin only"));
        return;
      }
      String uri = req.getRequestURI();
      if (uri.contains("/admin/products/")) {
        long id = Long.parseLong(uri.substring(uri.lastIndexOf('/') + 1));
        products.adminDelete(id);
        JsonUtil.write(resp, 200, ApiResponse.ok(java.util.Collections.singletonMap("deleted", id)));
      } else if (uri.contains("/reviews/")) {
        long id = Long.parseLong(uri.substring(uri.lastIndexOf('/') + 1));
        DAOFactory.reviews().delete(id);
        JsonUtil.write(resp, 200, ApiResponse.ok(java.util.Collections.singletonMap("deleted", id)));
      } else {
        sendError(resp, new com.js.jsmart.exception.NotFoundException("Unknown resource"));
      }
    } catch (Exception e) {
      sendError(resp, e);
    }
  }
}
