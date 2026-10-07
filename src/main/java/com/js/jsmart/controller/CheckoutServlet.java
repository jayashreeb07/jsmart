package com.js.jsmart.controller;

import com.js.jsmart.dao.DAOFactory;
import com.js.jsmart.dto.ApiResponse;
import com.js.jsmart.model.CartItem;
import com.js.jsmart.model.User;
import com.js.jsmart.service.CartService;
import com.js.jsmart.service.OrderService;
import com.js.jsmart.util.JsonUtil;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Checkout with mock payment (F5). */
@WebServlet({"/checkout", "/api/v1/checkout"})
public class CheckoutServlet extends BaseServlet {
  private CartService cart = new CartService(DAOFactory.carts(), DAOFactory.products());
  private OrderService orders = new OrderService(DAOFactory.orders(), DAOFactory.carts());

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      User u = requireUser(req);
      List<CartItem> items = cart.view(u.getId());
      req.setAttribute("items", items);
      req.setAttribute("total", cart.total(items));
      req.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(req, resp);
    } catch (Exception e) {
      pageError(req, resp, e);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String uri = req.getRequestURI();
    boolean isApi = uri.contains("/api/");
    try {
      User u = requireUser(req);
      boolean confirm = "true".equalsIgnoreCase(req.getParameter("confirm"))
          || "true".equalsIgnoreCase(req.getParameter("mockPayment"))
          || isApi;
      if (req.getContentType() != null && req.getContentType().contains("json")) {
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> body = JsonUtil.fromBody(req, java.util.Map.class);
        if (body != null && body.get("confirm") != null) {
          confirm = Boolean.parseBoolean(String.valueOf(body.get("confirm")));
        } else if (body != null && body.get("mockPayment") != null) {
          confirm = Boolean.parseBoolean(String.valueOf(body.get("mockPayment")));
        }
      }
      List<CartItem> items = cart.view(u.getId());
      long orderId = orders.checkout(u.getId(), items, confirm);
      if (isApi) {
        JsonUtil.write(resp, 201, ApiResponse.ok(java.util.Collections.singletonMap("orderId", orderId)));
      } else {
        com.js.jsmart.util.Flash.successPage(req, "Order placed successfully! \uD83C\uDF89",
            "Order #" + orderId + " has been placed.",
            "Track Order", "/orders/" + orderId);
        resp.sendRedirect(req.getContextPath() + "/success.jsp");
      }
    } catch (Exception e) {
      if (isApi) {
        sendError(resp, e);
      } else {
        com.js.jsmart.util.Flash.error(req, friendly(e));
        try {
          resp.sendRedirect(back(req, "/checkout"));
        } catch (IOException ex) {
          log.error("checkout redirect failed", ex);
        }
      }
    }
  }

  /** Visible for tests. */
  public void setServices(CartService c, OrderService o) {
    this.cart = c;
    this.orders = o;
  }
}
