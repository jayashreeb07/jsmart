package com.js.jsmart.util;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Shared input validation helpers.
 */
public final class ValidationUtil {
  private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

  private ValidationUtil() { }

  /** Check email format. */
  public static boolean isEmail(String s) {
    return s != null && EMAIL.matcher(s.trim()).matches();
  }

  /** Validate registration; returns field errors. */
  public static Map<String, String> validateRegister(String email, String password, String name) {
    Map<String, String> e = new LinkedHashMap<>();
    if (!isEmail(email)) {
      e.put("email", "Invalid email");
    }
    if (password == null || password.length() < 6) {
      e.put("password", "Password must be at least 6 characters");
    }
    if (name == null || name.trim().isEmpty()) {
      e.put("fullName", "Name is required");
    }
    return e;
  }

  /** Validate product fields; returns field errors. */
  public static Map<String, String> validateProduct(String name, BigDecimal price, Integer stock) {
    Map<String, String> e = new LinkedHashMap<>();
    if (name == null || name.trim().isEmpty()) {
      e.put("name", "Name is required");
    }
    if (price == null || price.signum() < 0) {
      e.put("price", "Price must be >= 0");
    }
    if (stock == null || stock < 0) {
      e.put("stockQty", "Stock must be >= 0");
    }
    return e;
  }
}
