package com.js.jsmart.dao;

import com.js.jsmart.model.Product;
import com.js.jsmart.util.DbUtil;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of ProductDAO.
 */
public class JdbcProductDAO implements ProductDAO {

  /** Map row. */
  public static Product map(ResultSet rs) throws SQLException {
    Product p = new Product();
    p.setId(rs.getLong("id"));
    p.setSellerId(rs.getLong("seller_id"));
    p.setName(rs.getString("name"));
    p.setDescription(rs.getString("description"));
    BigDecimal price = rs.getBigDecimal("price");
    p.setPrice(price == null ? BigDecimal.ZERO : price);
    p.setStockQty(rs.getInt("stock_qty"));
    p.setCategory(rs.getString("category"));
    p.setImageUrl(rs.getString("image_url"));
    if (rs.getTimestamp("created_at") != null) {
      p.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
    }
    try {
      p.setSellerName(rs.getString("seller_name"));
    } catch (SQLException e) {
      p.setSellerName(null);
    }
    try {
      p.setAvgRating(rs.getDouble("avg_rating"));
      p.setReviewCount(rs.getInt("review_count"));
    } catch (SQLException e) {
      // aggregate columns absent
    }
    return p;
  }

  private String baseSelect() {
    return "SELECT p.*, u.full_name AS seller_name, "
        + "COALESCE((SELECT AVG(CAST(r.rating AS DOUBLE)) FROM reviews r WHERE r.product_id = p.id), 0) AS avg_rating, "
        + "(SELECT COUNT(*) FROM reviews r WHERE r.product_id = p.id) AS review_count "
        + "FROM products p JOIN users u ON u.id = p.seller_id";
  }

  @Override
  public Optional<Product> findById(long id) throws SQLException {
    String sql = baseSelect() + " WHERE p.id = ?";
    try (Connection c = DbUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setLong(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return Optional.of(map(rs));
        }
        return Optional.empty();
      }
    }
  }

  @Override
  public List<Product> search(String category, String keyword, int limit, int offset) throws SQLException {
    StringBuilder sql = new StringBuilder(baseSelect() + " WHERE 1=1");
    List<Object> params = new ArrayList<>();
    if (category != null && !category.isBlank()) {
      sql.append(" AND p.category = ?");
      params.add(category.trim());
    }
    if (keyword != null && !keyword.isBlank()) {
      sql.append(" AND (LOWER(p.name) LIKE ? OR LOWER(p.description) LIKE ?)");
      String like = "%" + keyword.trim().toLowerCase() + "%";
      params.add(like);
      params.add(like);
    }
    sql.append(" ORDER BY p.id DESC LIMIT ? OFFSET ?");
    try (Connection c = DbUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(sql.toString())) {
      int i = 1;
      for (Object p : params) {
        ps.setObject(i++, p);
      }
      ps.setInt(i++, limit);
      ps.setInt(i, offset);
      try (ResultSet rs = ps.executeQuery()) {
        List<Product> out = new ArrayList<>();
        while (rs.next()) {
          out.add(map(rs));
        }
        return out;
      }
    }
  }

  @Override
  public long countSearch(String category, String keyword) throws SQLException {
    StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM products p WHERE 1=1");
    List<Object> params = new ArrayList<>();
    if (category != null && !category.isBlank()) {
      sql.append(" AND p.category = ?");
      params.add(category.trim());
    }
    if (keyword != null && !keyword.isBlank()) {
      sql.append(" AND (LOWER(p.name) LIKE ? OR LOWER(p.description) LIKE ?)");
      String like = "%" + keyword.trim().toLowerCase() + "%";
      params.add(like);
      params.add(like);
    }
    try (Connection c = DbUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(sql.toString())) {
      for (int i = 0; i < params.size(); i++) {
        ps.setObject(i + 1, params.get(i));
      }
      try (ResultSet rs = ps.executeQuery()) {
        rs.next();
        return rs.getLong(1);
      }
    }
  }

  @Override
  public List<Product> findBySeller(long sellerId) throws SQLException {
    String sql = baseSelect() + " WHERE p.seller_id = ? ORDER BY p.id DESC";
    try (Connection c = DbUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setLong(1, sellerId);
      try (ResultSet rs = ps.executeQuery()) {
        List<Product> out = new ArrayList<>();
        while (rs.next()) {
          out.add(map(rs));
        }
        return out;
      }
    }
  }

  @Override
  public List<Product> findAll(int limit, int offset) throws SQLException {
    String sql = baseSelect() + " ORDER BY p.id DESC LIMIT ? OFFSET ?";
    try (Connection c = DbUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, limit);
      ps.setInt(2, offset);
      try (ResultSet rs = ps.executeQuery()) {
        List<Product> out = new ArrayList<>();
        while (rs.next()) {
          out.add(map(rs));
        }
        return out;
      }
    }
  }

  @Override
  public long insert(Product p) throws SQLException {
    String sql = "INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url)"
        + " VALUES (?, ?, ?, ?, ?, ?, ?)";
    try (Connection c = DbUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setLong(1, p.getSellerId());
      ps.setString(2, p.getName());
      ps.setString(3, p.getDescription());
      ps.setBigDecimal(4, p.getPrice());
      ps.setInt(5, p.getStockQty());
      ps.setString(6, p.getCategory());
      ps.setString(7, p.getImageUrl());
      ps.executeUpdate();
      try (ResultSet rs = ps.getGeneratedKeys()) {
        rs.next();
        return rs.getLong(1);
      }
    }
  }

  @Override
  public void update(Product p) throws SQLException {
    String sql = "UPDATE products SET name=?, description=?, price=?, stock_qty=?, category=?, image_url=? WHERE id=?";
    try (Connection c = DbUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setString(1, p.getName());
      ps.setString(2, p.getDescription());
      ps.setBigDecimal(3, p.getPrice());
      ps.setInt(4, p.getStockQty());
      ps.setString(5, p.getCategory());
      ps.setString(6, p.getImageUrl());
      ps.setLong(7, p.getId());
      ps.executeUpdate();
    }
  }

  @Override
  public void delete(long id) throws SQLException {
    try (Connection c = DbUtil.getConnection();
         PreparedStatement ps = c.prepareStatement("DELETE FROM products WHERE id = ?")) {
      ps.setLong(1, id);
      ps.executeUpdate();
    }
  }

  @Override
  public void adjustStock(long productId, int delta) throws SQLException {
    String sql = "UPDATE products SET stock_qty = stock_qty + ? WHERE id = ? AND stock_qty + ? >= 0";
    try (Connection c = DbUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, delta);
      ps.setLong(2, productId);
      ps.setInt(3, delta);
      int n = ps.executeUpdate();
      if (n == 0) {
        throw new SQLException("Insufficient stock for product " + productId);
      }
    }
  }
}
