package com.js.jsmart.dao;

import com.js.jsmart.model.Review;
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
 * JDBC review DAO.
 */
public class JdbcReviewDAO implements ReviewDAO {

  private Review map(ResultSet rs) throws SQLException {
    Review r = new Review();
    r.setId(rs.getLong("id"));
    r.setProductId(rs.getLong("product_id"));
    r.setUserId(rs.getLong("user_id"));
    r.setRating(rs.getInt("rating"));
    r.setComment(rs.getString("comment"));
    if (rs.getTimestamp("created_at") != null) {
      r.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
    }
    try {
      r.setUserName(rs.getString("user_name"));
    } catch (SQLException e) {
      r.setUserName(null);
    }
    return r;
  }

  @Override
  public List<Review> findByProduct(long productId) throws SQLException {
    String sql = "SELECT r.*, u.full_name AS user_name FROM reviews r JOIN users u ON u.id = r.user_id"
        + " WHERE r.product_id = ? ORDER BY r.id DESC";
    try (Connection c = DbUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setLong(1, productId);
      try (ResultSet rs = ps.executeQuery()) {
        List<Review> out = new ArrayList<>();
        while (rs.next()) {
          out.add(map(rs));
        }
        return out;
      }
    }
  }

  @Override
  public Optional<Review> findByProductAndUser(long productId, long userId) throws SQLException {
    String sql = "SELECT * FROM reviews WHERE product_id = ? AND user_id = ?";
    try (Connection c = DbUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setLong(1, productId);
      ps.setLong(2, userId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return Optional.of(map(rs));
        }
        return Optional.empty();
      }
    }
  }

  @Override
  public long insert(Review r) throws SQLException {
    try (Connection c = DbUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(
             "INSERT INTO reviews (product_id, user_id, rating, comment) VALUES (?, ?, ?, ?)",
             Statement.RETURN_GENERATED_KEYS)) {
      ps.setLong(1, r.getProductId());
      ps.setLong(2, r.getUserId());
      ps.setInt(3, r.getRating());
      ps.setString(4, r.getComment());
      ps.executeUpdate();
      try (ResultSet rs = ps.getGeneratedKeys()) {
        rs.next();
        return rs.getLong(1);
      }
    }
  }

  @Override
  public void delete(long id) throws SQLException {
    try (Connection c = DbUtil.getConnection();
         PreparedStatement ps = c.prepareStatement("DELETE FROM reviews WHERE id = ?")) {
      ps.setLong(1, id);
      ps.executeUpdate();
    }
  }
}
