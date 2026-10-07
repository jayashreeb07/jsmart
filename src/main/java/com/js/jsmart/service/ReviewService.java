package com.js.jsmart.service;

import com.js.jsmart.dao.OrderDAO;
import com.js.jsmart.dao.ReviewDAO;
import com.js.jsmart.exception.AppException;
import com.js.jsmart.exception.ValidationException;
import com.js.jsmart.model.Review;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Review rules: only delivered purchases, rating 1-5, no duplicates.
 */
public class ReviewService {
  private final ReviewDAO reviewDAO;
  private final OrderDAO orderDAO;

  /** Create service. */
  public ReviewService(ReviewDAO reviewDAO, OrderDAO orderDAO) {
    this.reviewDAO = reviewDAO;
    this.orderDAO = orderDAO;
  }

  /** Add review. */
  public long add(long userId, long productId, int rating, String comment) {
    Map<String, String> errors = new LinkedHashMap<>();
    if (rating < 1 || rating > 5) {
      errors.put("rating", "Rating must be 1-5");
    }
    if (comment != null && comment.length() > 2000) {
      errors.put("comment", "Comment too long");
    }
    if (!errors.isEmpty()) {
      throw new ValidationException("Validation failed", errors);
    }
    try {
      if (!orderDAO.hasDeliveredPurchase(userId, productId)) {
        throw new ValidationException("Not eligible",
            Collections.singletonMap("product", "Review allowed only on delivered orders"));
      }
      if (reviewDAO.findByProductAndUser(productId, userId).isPresent()) {
        throw new AppException(409, "CONFLICT", "You already reviewed this product");
      }
      Review r = new Review();
      r.setProductId(productId);
      r.setUserId(userId);
      r.setRating(rating);
      r.setComment(comment);
      return reviewDAO.insert(r);
    } catch (SQLException e) {
      throw new AppException(500, "DB_ERROR", "Review failed");
    }
  }

  /** List reviews. */
  public List<Review> forProduct(long productId) {
    try {
      return reviewDAO.findByProduct(productId);
    } catch (SQLException e) {
      throw new AppException(500, "DB_ERROR", "Load failed");
    }
  }
}
