package com.js.jsmart.dao;

import com.js.jsmart.model.Order;
import com.js.jsmart.model.OrderItem;
import com.js.jsmart.model.OrderStatus;
import com.js.jsmart.util.DbUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC order DAO.
 */
public class JdbcOrderDAO implements OrderDAO {

  private Order mapOrder(ResultSet rs) throws SQLException {
    Order o = new Order();
    o.setId(rs.getLong("id"));
    o.setBuyerId(rs.getLong("buyer_id"));
    o.setStatus(OrderStatus.valueOf(rs.getString("order_status")));
    o.setTotalAmount(rs.getBigDecimal("total_amount"));
    if (rs.getTimestamp("created_at") != null) {
      o.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
    }
    return o;
  }

  private List<OrderItem> itemsFor(Connection con, long orderId) throws SQLException {
    String sql = "SELECT oi.*, p.name AS product_name, p.image_url AS product_image FROM order_items oi"
        + " LEFT JOIN products p ON p.id = oi.product_id WHERE oi.order_id = ?";
    try (PreparedStatement ps = con.prepareStatement(sql)) {
      ps.setLong(1, orderId);
      try (ResultSet rs = ps.executeQuery()) {
        List<OrderItem> out = new ArrayList<>();
        while (rs.next()) {
          OrderItem i = new OrderItem();
          i.setId(rs.getLong("id"));
          i.setOrderId(rs.getLong("order_id"));
          i.setProductId(rs.getLong("product_id"));
          i.setProductName(rs.getString("product_name"));
          try {
            i.setProductImage(rs.getString("product_image"));
          } catch (SQLException e) {
            i.setProductImage(null);
          }
          i.setQuantity(rs.getInt("quantity"));
          i.setUnitPrice(rs.getBigDecimal("unit_price"));
          out.add(i);
        }
        return out;
      }
    }
  }

  @Override
  public long createWithItems(Connection con, Order order) throws SQLException {
    try (PreparedStatement ps = con.prepareStatement(
        "INSERT INTO orders (buyer_id, order_status, total_amount) VALUES (?, ?, ?)",
        Statement.RETURN_GENERATED_KEYS)) {
      ps.setLong(1, order.getBuyerId());
      ps.setString(2, order.getStatus().name());
      ps.setBigDecimal(3, order.getTotalAmount());
      ps.executeUpdate();
      long orderId;
      try (ResultSet rs = ps.getGeneratedKeys()) {
        rs.next();
        orderId = rs.getLong(1);
      }
      for (OrderItem item : order.getItems()) {
        try (PreparedStatement pi = con.prepareStatement(
            "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)")) {
          pi.setLong(1, orderId);
          pi.setLong(2, item.getProductId());
          pi.setInt(3, item.getQuantity());
          pi.setBigDecimal(4, item.getUnitPrice());
          pi.executeUpdate();
        }
        try (PreparedStatement pu = con.prepareStatement(
            "UPDATE products SET stock_qty = stock_qty - ? WHERE id = ? AND stock_qty >= ?")) {
          pu.setInt(1, item.getQuantity());
          pu.setLong(2, item.getProductId());
          pu.setInt(3, item.getQuantity());
          if (pu.executeUpdate() == 0) {
            throw new SQLException("Insufficient stock for product " + item.getProductId());
          }
        }
      }
      try (PreparedStatement pc = con.prepareStatement("DELETE FROM cart_items WHERE user_id = ?")) {
        pc.setLong(1, order.getBuyerId());
        pc.executeUpdate();
      }
      return orderId;
    }
  }

  @Override
  public Optional<Order> findById(long id) throws SQLException {
    try (Connection c = DbUtil.getConnection()) {
      Optional<Order> o = findById(c, id);
      if (o.isPresent()) {
        o.get().setItems(itemsFor(c, id));
      }
      return o;
    }
  }

  @Override
  public Optional<Order> findById(Connection con, long id) throws SQLException {
    try (PreparedStatement ps = con.prepareStatement("SELECT * FROM orders WHERE id = ?")) {
      ps.setLong(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          Order o = mapOrder(rs);
          o.setItems(itemsFor(con, id));
          return Optional.of(o);
        }
        return Optional.empty();
      }
    }
  }

  private List<Order> listBySql(String sql, long param, boolean useParam) throws SQLException {
    try (Connection c = DbUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      if (useParam) {
        ps.setLong(1, param);
      }
      try (ResultSet rs = ps.executeQuery()) {
        List<Order> out = new ArrayList<>();
        while (rs.next()) {
          out.add(mapOrder(rs));
        }
        for (Order o : out) {
          o.setItems(itemsFor(c, o.getId()));
        }
        return out;
      }
    }
  }

  @Override
  public List<Order> findByBuyer(long buyerId) throws SQLException {
    return listBySql("SELECT * FROM orders WHERE buyer_id = ? ORDER BY id DESC", buyerId, true);
  }

  @Override
  public List<Order> findBySeller(long sellerId) throws SQLException {
    String sql = "SELECT DISTINCT o.* FROM orders o JOIN order_items oi ON oi.order_id = o.id"
        + " JOIN products p ON p.id = oi.product_id WHERE p.seller_id = ? ORDER BY o.id DESC";
    return listBySql(sql, sellerId, true);
  }

  @Override
  public List<Order> findAll() throws SQLException {
    return listBySql("SELECT * FROM orders ORDER BY id DESC", 0, false);
  }

  @Override
  public void updateStatus(long orderId, String status) throws SQLException {
    try (Connection c = DbUtil.getConnection();
         PreparedStatement ps = c.prepareStatement("UPDATE orders SET order_status = ? WHERE id = ?")) {
      ps.setString(1, status);
      ps.setLong(2, orderId);
      ps.executeUpdate();
    }
  }

  @Override
  public boolean hasDeliveredPurchase(long userId, long productId) throws SQLException {
    String sql = "SELECT COUNT(*) FROM orders o JOIN order_items oi ON oi.order_id = o.id"
        + " WHERE o.buyer_id = ? AND oi.product_id = ? AND o.order_status = 'DELIVERED'";
    try (Connection c = DbUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setLong(1, userId);
      ps.setLong(2, productId);
      try (ResultSet rs = ps.executeQuery()) {
        rs.next();
        return rs.getLong(1) > 0;
      }
    }
  }
}
