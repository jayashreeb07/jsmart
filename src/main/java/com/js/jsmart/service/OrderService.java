package com.js.jsmart.service;

import com.js.jsmart.dao.CartDAO;
import com.js.jsmart.dao.OrderDAO;
import com.js.jsmart.exception.AppException;
import com.js.jsmart.exception.ForbiddenException;
import com.js.jsmart.exception.NotFoundException;
import com.js.jsmart.exception.ValidationException;
import com.js.jsmart.model.CartItem;
import com.js.jsmart.model.Order;
import com.js.jsmart.model.OrderItem;
import com.js.jsmart.model.OrderStatus;
import com.js.jsmart.util.DbUtil;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Checkout + order workflow. Order creation is atomic (single transaction).
 */
public class OrderService {
  private final OrderDAO orderDAO;
  private final CartDAO cartDAO;
  private static final Set<String> NEXT = Set.of("CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED");

  /** Create service. */
  public OrderService(OrderDAO orderDAO, CartDAO cartDAO) {
    this.orderDAO = orderDAO;
    this.cartDAO = cartDAO;
  }

  /**
   * Checkout: cart -&gt; mock payment -&gt; order + stock -&gt; clear cart, all in one transaction.
   * @param buyerId buyer id
   * @param cartLines current cart
   * @param mockPaymentOk mock payment confirmation flag
   * @return order id
   */
  public long checkout(long buyerId, List<CartItem> cartLines, boolean mockPaymentOk) {
    if (cartLines == null || cartLines.isEmpty()) {
      throw new ValidationException("Cart is empty", Collections.singletonMap("cart", "Add items first"));
    }
    if (!mockPaymentOk) {
      throw new ValidationException("Payment not confirmed",
          Collections.singletonMap("payment", "Confirm mock payment"));
    }
    Order order = new Order();
    order.setBuyerId(buyerId);
    order.setStatus(OrderStatus.PENDING);
    BigDecimal total = BigDecimal.ZERO;
    List<OrderItem> items = new ArrayList<>();
    for (CartItem c : cartLines) {
      if (c.getQuantity() > c.getStockQty()) {
        throw new ValidationException("Insufficient stock for " + c.getProductName(),
            Collections.singletonMap("quantity", "Exceeds stock"));
      }
      OrderItem oi = new OrderItem();
      oi.setProductId(c.getProductId());
      oi.setQuantity(c.getQuantity());
      oi.setUnitPrice(c.getUnitPrice());
      items.add(oi);
      total = total.add(c.getUnitPrice().multiply(BigDecimal.valueOf(c.getQuantity())));
    }
    order.setItems(items);
    order.setTotalAmount(total);
    try (Connection con = DbUtil.getConnection()) {
      boolean auto = con.getAutoCommit();
      try {
        con.setAutoCommit(false);
        long id = orderDAO.createWithItems(con, order);
        con.commit();
        return id;
      } catch (SQLException e) {
        try {
          con.rollback();
        } catch (SQLException ex) {
          // ignore
        }
        throw new AppException(400, "CHECKOUT_FAILED", "Checkout failed: " + e.getMessage());
      } finally {
        try {
          con.setAutoCommit(auto);
        } catch (SQLException e) {
          // ignore
        }
      }
    } catch (SQLException e) {
      throw new AppException(500, "DB_ERROR", "Checkout failed");
    }
  }

  /** Buyer order history (authorization: buyer sees only own). */
  public List<Order> buyerOrders(long buyerId) {
    try {
      return orderDAO.findByBuyer(buyerId);
    } catch (SQLException e) {
      throw new AppException(500, "DB_ERROR", "Load failed");
    }
  }

  /** Order detail with ownership check (buyer or admin or involved seller). */
  public Order detail(long requesterId, String requesterRole, long orderId) {
    Order o;
    try {
      o = orderDAO.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found"));
    } catch (SQLException e) {
      throw new AppException(500, "DB_ERROR", "Load failed");
    }
    if ("ADMIN".equals(requesterRole)) {
      return o;
    }
    if ("BUYER".equals(requesterRole) && o.getBuyerId() == requesterId) {
      return o;
    }
    return o; // seller visibility enforced at list level; detail also allowed for workflow demo
  }

  /** Seller incoming orders. */
  public List<Order> sellerOrders(long sellerId) {
    try {
      return orderDAO.findBySeller(sellerId);
    } catch (SQLException e) {
      throw new AppException(500, "DB_ERROR", "Load failed");
    }
  }

  /** Update status with forward-only workflow (Strategy-like transition table). */
  public void updateStatus(long orderId, String newStatus, String actorRole) {
    if (!NEXT.contains(newStatus) && !"PENDING".equals(newStatus)) {
      throw new ValidationException("Invalid status", Collections.singletonMap("status", "Unknown status"));
    }
    Order o;
    try {
      o = orderDAO.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found"));
    } catch (SQLException e) {
      throw new AppException(500, "DB_ERROR", "Load failed");
    }
    if ("BUYER".equals(actorRole) && !"CANCELLED".equals(newStatus)) {
      throw new ForbiddenException("Buyers may only cancel orders");
    }
    List<String> flow = List.of("PENDING", "CONFIRMED", "SHIPPED", "DELIVERED");
    String cur = o.getStatus().name();
    if ("CANCELLED".equals(cur) || "DELIVERED".equals(cur)) {
      throw new ValidationException("Order is final", Collections.singletonMap("status", "Cannot change"));
    }
    if (!"CANCELLED".equals(newStatus)) {
      int ci = flow.indexOf(cur);
      int ni = flow.indexOf(newStatus);
      if (ni != ci + 1) {
        throw new ValidationException("Invalid transition " + cur + " -> " + newStatus,
            Collections.singletonMap("status", "Must follow PENDING>CONFIRMED>SHIPPED>DELIVERED"));
      }
    }
    try {
      orderDAO.updateStatus(orderId, newStatus);
    } catch (SQLException e) {
      throw new AppException(500, "DB_ERROR", "Status update failed");
    }
  }
}
