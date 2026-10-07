package com.js.jsmart.dao;

import com.js.jsmart.model.Review;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** DAO for reviews. */
public interface ReviewDAO {
  /** Reviews for product. */
  List<Review> findByProduct(long productId) throws SQLException;
  /** Existing review by user for product. */
  Optional<Review> findByProductAndUser(long productId, long userId) throws SQLException;
  /** Insert review. */
  long insert(Review r) throws SQLException;
  /** Delete review (moderation). */
  void delete(long id) throws SQLException;
}
