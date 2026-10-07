package com.js.jsmart.dao;

import com.js.jsmart.model.CartItem;
import com.js.jsmart.util.DbUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC cart DAO.
 */
public class JdbcCartDAO implements CartDAO {

  @Override
  public List<CartItem> findByUser(long userId) throws SQLException {
    String sql = "SELECT ci.*, p.name AS product_name, p.price AS unit_price, p.stock_qty, p.image_url"
        + " FROM cart_items ci JOIN products p ON p.id = ci.product_id WHERE ci.user_id = ? ORDER BY ci.id";
    try (Connection c = DbUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setLong(1, userId);
      try (ResultSet rs = ps.executeQuery()) {
        List<CartItem> out = new ArrayList<>();
        while (rs.next()) {
          CartItem i = new CartItem();
          i.setId(rs.getLong("id"));
          i.setUserId(rs.getLong("user_id"));
          i.setProductId(rs.getLong("product_id"));
          i.setQuantity(rs.getInt("quantity"));
          i.setProductName(rs.getString("product_name"));
          i.setUnitPrice(rs.getBigDecimal("unit_price"));
          i.setStockQty(rs.getInt("stock_qty"));
          i.setImageUrl(rs.getString("image_url"));
          out.add(i);
        }
        return out;
      }
    }
  }

  @Override
  public void upsert(long userId, long productId, int quantity) throws SQLException {
    String update = "UPDATE cart_items SET quantity = ? WHERE user_id = ? AND product_id = ?";
    try (Connection c = DbUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(update)) {
      ps.setInt(1, quantity);
      ps.setLong(2, userId);
      ps.setLong(3, productId);
      if (ps.executeUpdate() == 0) {
        try (PreparedStatement ins = c.prepareStatement(
            "INSERT INTO cart_items (user_id, product_id, quantity) VALUES (?, ?, ?)")) {
          ins.setLong(1, userId);
          ins.setLong(2, productId);
          ins.setInt(3, quantity);
          ins.executeUpdate();
        }
      }
    }
  }

  @Override
  public void remove(long userId, long productId) throws SQLException {
    try (Connection c = DbUtil.getConnection();
         PreparedStatement ps = c.prepareStatement("DELETE FROM cart_items WHERE user_id = ? AND product_id = ?")) {
      ps.setLong(1, userId);
      ps.setLong(2, productId);
      ps.executeUpdate();
    }
  }

  @Override
  public void clear(long userId) throws SQLException {
    try (Connection c = DbUtil.getConnection();
         PreparedStatement ps = c.prepareStatement("DELETE FROM cart_items WHERE user_id = ?")) {
      ps.setLong(1, userId);
      ps.executeUpdate();
    }
  }
}
