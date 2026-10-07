package com.js.jsmart.util;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * DataSource holder managed by ServletContextListener (Singleton pattern).
 */
public final class DbUtil {
  private static volatile HikariDataSource dataSource;

  private DbUtil() { }

  /** Attach pool (called once by listener). */
  public static void setDataSource(HikariDataSource ds) {
    dataSource = ds;
  }

  /** Get pool. */
  public static HikariDataSource getDataSource() {
    if (dataSource == null) {
      throw new IllegalStateException("DataSource not initialized");
    }
    return dataSource;
  }

  /** Borrow connection (use try-with-resources). */
  public static Connection getConnection() throws SQLException {
    return getDataSource().getConnection();
  }
}
