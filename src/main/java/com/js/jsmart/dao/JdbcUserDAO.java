package com.js.jsmart.dao;

import com.js.jsmart.model.Role;
import com.js.jsmart.model.User;
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
 * JDBC implementation of UserDAO. Uses PreparedStatement only.
 */
public class JdbcUserDAO implements UserDAO {

  /** Map row to user. */
  public static User map(ResultSet rs) throws SQLException {
    User u = new User();
    u.setId(rs.getLong("id"));
    u.setEmail(rs.getString("email"));
    u.setPasswordHash(rs.getString("password_hash"));
    u.setFullName(rs.getString("full_name"));
    u.setRole(Role.valueOf(rs.getString("user_role")));
    if (rs.getTimestamp("created_at") != null) {
      u.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
    }
    return u;
  }

  @Override
  public Optional<User> findByEmail(String email) throws SQLException {
    String sql = "SELECT * FROM users WHERE email = ?";
    try (Connection c = DbUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setString(1, email);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return Optional.of(map(rs));
        }
        return Optional.empty();
      }
    }
  }

  @Override
  public Optional<User> findById(long id) throws SQLException {
    String sql = "SELECT * FROM users WHERE id = ?";
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
  public long insert(User user) throws SQLException {
    String sql = "INSERT INTO users (email, password_hash, full_name, user_role) VALUES (?, ?, ?, ?)";
    try (Connection c = DbUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, user.getEmail());
      ps.setString(2, user.getPasswordHash());
      ps.setString(3, user.getFullName());
      ps.setString(4, user.getRole().name());
      ps.executeUpdate();
      try (ResultSet rs = ps.getGeneratedKeys()) {
        rs.next();
        return rs.getLong(1);
      }
    }
  }

  @Override
  public List<User> findAll() throws SQLException {
    String sql = "SELECT * FROM users ORDER BY id";
    try (Connection c = DbUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
      List<User> out = new ArrayList<>();
      while (rs.next()) {
        out.add(map(rs));
      }
      return out;
    }
  }

  @Override
  public long count() throws SQLException {
    try (Connection c = DbUtil.getConnection();
         PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM users");
         ResultSet rs = ps.executeQuery()) {
      rs.next();
      return rs.getLong(1);
    }
  }
}
