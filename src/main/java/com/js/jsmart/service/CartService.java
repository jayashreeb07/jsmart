package com.js.jsmart.service;

import com.js.jsmart.dao.CartDAO;
import com.js.jsmart.dao.ProductDAO;
import com.js.jsmart.exception.AppException;
import com.js.jsmart.exception.NotFoundException;
import com.js.jsmart.exception.ValidationException;
import com.js.jsmart.model.CartItem;
import com.js.jsmart.model.Product;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Cart business rules.
 */
public class CartService {
  private final CartDAO cartDAO;
  private final ProductDAO productDAO;

  /** Create service. */
  public CartService(CartDAO cartDAO, ProductDAO productDAO) {
    this.cartDAO = cartDAO;
    this.productDAO = productDAO;
  }

  /** View cart. */
  public List<CartItem> view(long userId) {
    try {
      return cartDAO.findByUser(userId);
    } catch (SQLException e) {
      throw new AppException(500, "DB_ERROR", "Cart load failed");
    }
  }

  /** Add/update item with stock validation. */
  public void addOrUpdate(long userId, long productId, int quantity) {
    if (quantity <= 0) {
      throw new ValidationException("Invalid quantity", Collections.singletonMap("quantity", "Must be positive"));
    }
    try {
      Product p = productDAO.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));
      if (quantity > p.getStockQty()) {
        throw new ValidationException("Insufficient stock",
            Collections.singletonMap("quantity", "Only " + p.getStockQty() + " available"));
      }
      cartDAO.upsert(userId, productId, quantity);
    } catch (SQLException e) {
      throw new AppException(500, "DB_ERROR", "Cart update failed");
    }
  }

  /** Remove line. */
  public void remove(long userId, long productId) {
    try {
      cartDAO.remove(userId, productId);
    } catch (SQLException e) {
      throw new AppException(500, "DB_ERROR", "Cart remove failed");
    }
  }

  /** Compute total. */
  public BigDecimal total(List<CartItem> items) {
    BigDecimal t = BigDecimal.ZERO;
    for (CartItem i : items) {
      t = t.add(i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())));
    }
    return t;
  }
}
