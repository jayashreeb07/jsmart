package com.js.jsmart.service;

import com.js.jsmart.dao.OrderDAO;
import com.js.jsmart.dao.ProductDAO;
import com.js.jsmart.dao.UserDAO;
import com.js.jsmart.dto.RegisterRequest;
import com.js.jsmart.dto.UserResponseDTO;
import com.js.jsmart.exception.AppException;
import com.js.jsmart.exception.ValidationException;
import com.js.jsmart.model.Role;
import com.js.jsmart.model.User;
import com.js.jsmart.util.PasswordUtil;
import com.js.jsmart.util.ValidationUtil;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

/**
 * Authentication business rules: registration, login validation.
 */
public class AuthService {
  private final UserDAO userDAO;

  /** Create with DAO (services depend on interfaces). */
  public AuthService(UserDAO userDAO) {
    this.userDAO = userDAO;
  }

  /**
   * Register buyer or seller. Admin signup is forbidden.
   * @param req registration request
   * @return safe user DTO
   */
  public UserResponseDTO register(RegisterRequest req) {
    String email = req.getEmail() == null ? null : req.getEmail().trim().toLowerCase();
    String roleRaw = req.getRole() == null ? "BUYER" : req.getRole().trim().toUpperCase();
    if ("ADMIN".equals(roleRaw)) {
      throw new AppException(403, "FORBIDDEN", "Admin signup is not allowed");
    }
    Role role;
    try {
      role = Role.valueOf(roleRaw);
    } catch (IllegalArgumentException e) {
      role = Role.BUYER;
    }
    if (role != Role.BUYER && role != Role.SELLER) {
      role = Role.BUYER;
    }
    Map<String, String> errors = ValidationUtil.validateRegister(email, req.getPassword(), req.getFullName());
    if (!errors.isEmpty()) {
      throw new ValidationException("Validation failed", errors);
    }
    try {
      Optional<User> existing = userDAO.findByEmail(email);
      if (existing.isPresent()) {
        throw new AppException(409, "CONFLICT", "Email already registered");
      }
      User u = new User();
      u.setEmail(email);
      u.setPasswordHash(PasswordUtil.hash(req.getPassword()));
      u.setFullName(req.getFullName().trim());
      u.setRole(role);
      long id = userDAO.insert(u);
      u.setId(id);
      return UserResponseDTO.from(u);
    } catch (SQLException e) {
      throw new AppException(500, "DB_ERROR", "Registration failed");
    }
  }

  /**
   * Verify credentials and return user.
   * @param email email
   * @param password raw password
   * @return user entity
   */
  public User login(String email, String password) {
    if (email == null || password == null) {
      throw new AppException(401, "UNAUTHORIZED", "Invalid credentials");
    }
    try {
      Optional<User> opt = userDAO.findByEmail(email.trim().toLowerCase());
      if (opt.isEmpty() || !PasswordUtil.verify(password, opt.get().getPasswordHash())) {
        throw new AppException(401, "UNAUTHORIZED", "Invalid credentials");
      }
      return opt.get();
    } catch (SQLException e) {
      throw new AppException(500, "DB_ERROR", "Login failed");
    }
  }

  /**
   * Lookup product DAO accessor (kept for wiring convenience).
   */
  public UserDAO getUserDAO() {
    return userDAO;
  }
}
