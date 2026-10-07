package com.js.jsmart.controller;

import com.js.jsmart.dao.DAOFactory;
import com.js.jsmart.dto.ApiResponse;
import com.js.jsmart.model.CartItem;
import com.js.jsmart.model.User;
import com.js.jsmart.service.CartService;
import com.js.jsmart.util.JsonUtil;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Cart pages + API (F4). */
@WebServlet({"/cart", "/api/v1/cart", "/api/v1/cart/*"})
public class CartServlet extends BaseServlet {
  private CartService cart = new CartService(DAOFactory.carts(), DAOFactory.products());

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String uri = req.getRequestURI().substring(req.getContextPath().length());
    try {
      User u = requireUser(req);
      List<CartItem> items = cart.view(u.getId());
      if (uri.startsWith("/api/")) {
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("items", items);
        data.put("total", cart.total(items));
        JsonUtil.write(resp, 200, ApiResponse.ok(data));
        return;
      }
      req.setAttribute("items", items);
      req.setAttribute("total", cart.total(items));
      req.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(req, resp);
    } catch (Exception e) {
      if (uri.startsWith("/api/")) {
        sendError(resp, e);
      } else {
        pageError(req, resp, e);
      }
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    try {
      User u = requireUser(req);
      String pid = req.getParameter("productId");
      String qty = req.getParameter("quantity");
      long productId = 0;
      int quantity = 1;
      if (req.getContentType() != null && req.getContentType().contains("json")) {
        @SuppressWarnings("unchecked")
        Map<String, Object> body = JsonUtil.fromBody(req, Map.class);
        if (body != null) {
          if (body.get("productId") != null) {
            productId = JsonUtil.toLong(body.get("productId"));
          }
          if (body.get("quantity") != null) {
            quantity = JsonUtil.toInt(body.get("quantity"));
          }
        }
      } else {
        productId = Long.parseLong(pid);
        quantity = Integer.parseInt(qty);
      }
      cart.addOrUpdate(u.getId(), productId, quantity);
      if (req.getRequestURI().contains("/api/")) {
        JsonUtil.write(resp, 200, ApiResponse.ok(java.util.Collections.singletonMap("ok", true)));
      } else {
        resp.sendRedirect(req.getContextPath() + "/cart");
      }
    } catch (Exception e) {
      try {
        sendError(resp, e);
      } catch (IOException ex) {
        log.error("cart error", ex);
      }
    }
  }

  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    try {
      User u = requireUser(req);
      String uri = req.getRequestURI();
      long productId = Long.parseLong(uri.substring(uri.lastIndexOf('/') + 1));
      cart.remove(u.getId(), productId);
      JsonUtil.write(resp, 200, ApiResponse.ok(java.util.Collections.singletonMap("removed", productId)));
    } catch (Exception e) {
      sendError(resp, e);
    }
  }

  /** Visible for tests. */
  public void setCartService(CartService s) {
    this.cart = s;
  }
}
