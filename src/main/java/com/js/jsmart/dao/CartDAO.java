package com.js.jsmart.dao;

import com.js.jsmart.model.CartItem;
import java.sql.SQLException;
import java.util.List;

/** DAO for cart items. */
public interface CartDAO {
  /** List cart lines for user. */
  List<CartItem> findByUser(long userId) throws SQLException;
  /** Upsert quantity. */
  void upsert(long userId, long productId, int quantity) throws SQLException;
  /** Remove one line. */
  void remove(long userId, long productId) throws SQLException;
  /** Clear cart. */
  void clear(long userId) throws SQLException;
}
