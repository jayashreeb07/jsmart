package com.js.jsmart.controller;

import com.js.jsmart.dao.DAOFactory;
import com.js.jsmart.dto.ApiResponse;
import com.js.jsmart.model.Order;
import com.js.jsmart.model.User;
import com.js.jsmart.service.OrderService;
import com.js.jsmart.util.JsonUtil;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Order history + status workflow (F6). */
@WebServlet({"/orders", "/orders/*", "/api/v1/orders", "/api/v1/orders/*"})
public class OrderServlet extends BaseServlet {
  private OrderService orders = new OrderService(DAOFactory.orders(), DAOFactory.carts());

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String uri = req.getRequestURI().substring(req.getContextPath().length());
    boolean isApi = uri.startsWith("/api/");
    try {
      User u = requireUser(req);
      String role = u.getRole().name();
      if (isApi && uri.matches(".*/orders/\\d+")) {
        long id = Long.parseLong(uri.substring(uri.lastIndexOf('/') + 1));
        Order o = orders.detail(u.getId(), role, id);
        if ("BUYER".equals(role) && o.getBuyerId() != u.getId()) {
          resp.sendError(403);
          return;
        }
        JsonUtil.write(resp, 200, ApiResponse.ok(o));
        return;
      }
      List<Order> list;
      if ("SELLER".equals(role)) {
        list = orders.sellerOrders(u.getId());
      } else if ("ADMIN".equals(role)) {
        list = orders.sellerOrders(0);
        if (list.isEmpty()) {
          try {
            list = DAOFactory.orders().findAll();
          } catch (Exception e) {
            list = java.util.Collections.emptyList();
          }
        }
      } else {
        list = orders.buyerOrders(u.getId());
      }
      if (isApi) {
        JsonUtil.write(resp, 200, ApiResponse.ok(list));
        return;
      }
      req.setAttribute("orders", list);
      req.getRequestDispatcher("/WEB-INF/views/orders.jsp").forward(req, resp);
    } catch (Exception e) {
      if (isApi) {
        sendError(resp, e);
      } else {
        pageError(req, resp, e);
      }
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    // status update: POST /api/v1/orders/{id}/status {status}
    try {
      User u = requireUser(req);
      String uri = req.getRequestURI();
      String[] parts = uri.split("/");
      long id = Long.parseLong(parts[parts.length - 2]);
      String status = req.getParameter("status");
      if (req.getContentType() != null && req.getContentType().contains("json")) {
        @SuppressWarnings("unchecked")
        java.util.Map<String, String> body = JsonUtil.fromBody(req, java.util.Map.class);
        if (body != null && body.get("status") != null) {
          status = body.get("status");
        }
      }
      orders.updateStatus(id, status, u.getRole().name());
      JsonUtil.write(resp, 200, ApiResponse.ok(java.util.Collections.singletonMap("status", status)));
    } catch (Exception e) {
      sendError(resp, e);
    }
  }

  /** Visible for tests. */
  public void setOrderService(OrderService s) {
    this.orders = s;
  }
}
