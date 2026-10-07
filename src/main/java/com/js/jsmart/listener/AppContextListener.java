package com.js.jsmart.listener;

import com.js.jsmart.util.ConfigUtil;
import com.js.jsmart.util.PasswordUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.js.jsmart.util.DbUtil;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Single lifecycle owner for HikariCP pool (Singleton/managed pool pattern).
 * Initializes pool, runs V1 migration, seeds demo data, closes pool on shutdown.
 */
@WebListener
public class AppContextListener implements ServletContextListener {
  private static final Logger LOG = LoggerFactory.getLogger(AppContextListener.class);
  private HikariDataSource ds;

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    ConfigUtil.init(sce.getServletContext());
    String url = ConfigUtil.get("db.url", "jdbc:h2:./data/jsmart;DB_CLOSE_DELAY=-1");
    String user = ConfigUtil.get("db.user", "sa");
    String pass = ConfigUtil.get("db.password", "");
    HikariConfig cfg = new HikariConfig();
    cfg.setDriverClassName("org.h2.Driver");
    cfg.setJdbcUrl(url);
    cfg.setUsername(user);
    cfg.setPassword(pass);
    cfg.setMaximumPoolSize(10);
    cfg.setPoolName("jsmart-pool");
    ds = new HikariDataSource(cfg);
    DbUtil.setDataSource(ds);
    sce.getServletContext().setAttribute("dataSource", ds);
    runMigrationAndSeed();
    LOG.info("JS Mart started with db {}", url);
  }

  private void runMigrationAndSeed() {
    try (Connection c = ds.getConnection();
         Statement st = c.createStatement()) {
      Path mig = Paths.get("db", "migrations", "V1__init_schema.sql");
      if (!Files.exists(mig)) {
        mig = Paths.get(System.getProperty("user.dir"), "db", "migrations", "V1__init_schema.sql");
      }
      URLFallback: {
        if (!Files.exists(mig)) {
          try (java.io.InputStream in = getClass().getClassLoader().getResourceAsStream("schema.sql")) {
            if (in != null) {
              String sql = new String(in.readAllBytes(), StandardCharsets.UTF_8);
              for (String s : sql.split(";")) {
                if (!s.isBlank()) {
                  st.execute(s);
                }
              }
            }
          } catch (Exception e) {
            LOG.warn("No schema resource", e);
          }
          break URLFallback;
        }
        String sql = Files.readString(mig, StandardCharsets.UTF_8);
        for (String s : sql.split(";")) {
          if (!s.isBlank()) {
            st.execute(s);
          }
        }
      }
      // seed demo users (BCrypt) if empty
      try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users")) {
        rs.next();
        if (rs.getLong(1) == 0) {
          seed(c);
        }
      }
      try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM products")) {
        rs.next();
        if (rs.getLong(1) == 0) {
          seedProducts(c);
        }
      }
    } catch (Exception e) {
      LOG.error("Migration failed", e);
    }
  }

  private void seed(Connection c) throws Exception {
    String sql = "INSERT INTO users (email, password_hash, full_name, user_role) VALUES (?, ?, ?, ?)";
    Object[][] users = {
        {"admin@jsmart.local", "Admin@123", "JS Mart Admin", "ADMIN"},
        {"seller1@jsmart.local", "Seller@123", "Asha Seller", "SELLER"},
        {"seller2@jsmart.local", "Seller@123", "Ravi Seller", "SELLER"},
        {"buyer1@jsmart.local", "Buyer@123", "Meera Buyer", "BUYER"},
        {"buyer2@jsmart.local", "Buyer@123", "Arjun Buyer", "BUYER"},
    };
    for (Object[] u : users) {
      try (PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setString(1, (String) u[0]);
        ps.setString(2, PasswordUtil.hash((String) u[1]));
        ps.setString(3, (String) u[2]);
        ps.setString(4, (String) u[3]);
        ps.executeUpdate();
      }
    }
  }

  private void seedProducts(Connection c) throws Exception {
    String sql = "INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url)"
        + " VALUES (?, ?, ?, ?, ?, ?, ?)";
    Object[][] rows = {
        {2L, "JS Bluetooth Speaker", "Portable speaker, 12h battery.", "2499.00", 50, "Electronics", ""},
        {2L, "Cotton T-Shirt", "Premium cotton tee.", "499.00", 200, "Fashion", ""},
        {3L, "Steel Bottle", "1L insulated flask.", "899.00", 120, "Home", ""},
        {3L, "Java Programming Book", "Learn Java 17.", "650.00", 80, "Books", ""},
        {2L, "Wireless Mouse", "Ergonomic mouse.", "799.00", 150, "Electronics", ""},
        {3L, "Running Shoes", "Lightweight shoes.", "1999.00", 60, "Fashion", ""},
    };
    for (Object[] r : rows) {
      try (PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setLong(1, (Long) r[0]);
        ps.setString(2, (String) r[1]);
        ps.setString(3, (String) r[2]);
        ps.setBigDecimal(4, new java.math.BigDecimal((String) r[3]));
        ps.setInt(5, (Integer) r[4]);
        ps.setString(6, (String) r[5]);
        ps.setString(7, (String) r[6]);
        ps.executeUpdate();
      }
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    if (ds != null && !ds.isClosed()) {
      ds.close();
    }
    LOG.info("JS Mart stopped, pool closed");
  }
}
