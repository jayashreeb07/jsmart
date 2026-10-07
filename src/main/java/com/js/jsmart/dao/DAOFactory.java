package com.js.jsmart.dao;

/**
 * Factory for DAO instances (Factory pattern).
 */
public final class DAOFactory {
  private static final UserDAO USER = new JdbcUserDAO();
  private static final ProductDAO PRODUCT = new JdbcProductDAO();
  private static final CartDAO CART = new JdbcCartDAO();
  private static final OrderDAO ORDER = new JdbcOrderDAO();
  private static final ReviewDAO REVIEW = new JdbcReviewDAO();

  private DAOFactory() { }

  /** User DAO singleton. */
  public static UserDAO users() { return USER; }
  /** Product DAO singleton. */
  public static ProductDAO products() { return PRODUCT; }
  /** Cart DAO singleton. */
  public static CartDAO carts() { return CART; }
  /** Order DAO singleton. */
  public static OrderDAO orders() { return ORDER; }
  /** Review DAO singleton. */
  public static ReviewDAO reviews() { return REVIEW; }
}
