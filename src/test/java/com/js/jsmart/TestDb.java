package com.js.jsmart;

import com.js.jsmart.util.DbUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Embedded H2 test database helper.
 */
public final class TestDb {
  private TestDb() { }

  /** Init once per test class. */
  public static HikariDataSource init() throws Exception {
    HikariConfig cfg = new HikariConfig();
    cfg.setJdbcUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=LEGACY");
    cfg.setUsername("sa");
    cfg.setPassword("");
    cfg.setMaximumPoolSize(5);
    HikariDataSource ds = new HikariDataSource(cfg);
    DbUtil.setDataSource(ds);
    try (Connection c = ds.getConnection();
         Statement st = c.createStatement()) {
      java.io.InputStream in = TestDb.class.getClassLoader().getResourceAsStream("schema-test.sql");
      if (in == null) {
        in = TestDb.class.getClassLoader().getResourceAsStream("schema.sql");
      }
      String sql = new String(in.readAllBytes(), StandardCharsets.UTF_8);
      for (String s : sql.split(";")) {
        if (!s.isBlank()) {
          st.execute(s);
        }
      }
    }
    return ds;
  }

  /** Wipe data between tests. */
  public static void clean() throws Exception {
    try (Connection c = DbUtil.getConnection();
         Statement st = c.createStatement()) {
      st.execute("DELETE FROM reviews");
      st.execute("DELETE FROM cart_items");
      st.execute("DELETE FROM order_items");
      st.execute("DELETE FROM orders");
      st.execute("DELETE FROM products");
      st.execute("DELETE FROM users");
    }
  }
}
