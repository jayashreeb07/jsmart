package com.js.jsmart.dao;

import com.js.jsmart.model.Order;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** DAO for orders. Transaction-aware methods take a Connection. */
public interface OrderDAO {
  /** Create order + items atomically using provided connection. */
  long createWithItems(Connection con, Order order) throws SQLException;
  /** Find by id. */
  Optional<Order> findById(long id) throws SQLException;
  /** Find by id with connection (inside txn). */
  Optional<Order> findById(Connection con, long id) throws SQLException;
  /** Orders of a buyer. */
  List<Order> findByBuyer(long buyerId) throws SQLException;
  /** Orders containing seller's products. */
  List<Order> findBySeller(long sellerId) throws SQLException;
  /** All orders (admin). */
  List<Order> findAll() throws SQLException;
  /** Update status. */
  void updateStatus(long orderId, String status) throws SQLException;
  /** Check delivered purchase of product by user. */
  boolean hasDeliveredPurchase(long userId, long productId) throws SQLException;
}
