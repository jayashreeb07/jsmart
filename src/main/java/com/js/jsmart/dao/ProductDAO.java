package com.js.jsmart.dao;

import com.js.jsmart.model.Product;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** DAO for products. */
public interface ProductDAO {
  /** Find by id. */
  Optional<Product> findById(long id) throws SQLException;
  /** Search with optional category/keyword. */
  List<Product> search(String category, String keyword, int limit, int offset) throws SQLException;
  /** Count search results. */
  long countSearch(String category, String keyword) throws SQLException;
  /** Products of a seller. */
  List<Product> findBySeller(long sellerId) throws SQLException;
  /** Insert product. */
  long insert(Product p) throws SQLException;
  /** Update product. */
  void update(Product p) throws SQLException;
  /** Delete product. */
  void delete(long id) throws SQLException;
  /** Adjust stock by delta (negative allowed, fails if below 0). */
  void adjustStock(long productId, int delta) throws SQLException;
  /** List all (admin moderation). */
  List<Product> findAll(int limit, int offset) throws SQLException;
}
