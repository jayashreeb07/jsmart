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
        {2L, "Nova X5 Smartphone", "6.5-inch display, 128GB storage, 5000mAh battery with fast charging.", "14999.00", 40,
            "Mobiles & Electronics", "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=640&q=70"},
        {3L, "Pixel Pro 12 Smartphone", "Triple rear camera, 256GB storage, all-day battery life.", "32999.00", 25,
            "Mobiles & Electronics", "https://images.unsplash.com/photo-1592750475338-74b7b21085ab?auto=format&fit=crop&w=640&q=70"},
        {2L, "Sonic Bass Headphones", "Over-ear headphones with deep bass and 30-hour battery.", "2499.00", 60,
            "Mobiles & Electronics", "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=640&q=70"},
        {3L, "Aero True Wireless Earbuds", "Compact earbuds with charging case and clear calling.", "1999.00", 80,
            "Mobiles & Electronics", "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?auto=format&fit=crop&w=640&q=70"},
        {2L, "Boom Mini Bluetooth Speaker", "Portable speaker with rich sound and 12-hour playtime.", "1999.00", 70,
            "Mobiles & Electronics", "https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?auto=format&fit=crop&w=640&q=70"},
        {3L, "Pulse Fitness Smartwatch", "Heart-rate tracking, step counter and 7-day battery.", "4999.00", 50,
            "Mobiles & Electronics", "https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=640&q=70"},
        {2L, "Volt 20000mAh Power Bank", "High-capacity power bank with dual USB fast output.", "1499.00", 90,
            "Mobiles & Electronics", "https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5?auto=format&fit=crop&w=640&q=70"},
        {3L, "FastCharge 65W Adapter", "65W wall charger for phones, tablets and laptops.", "999.00", 120,
            "Mobiles & Electronics", "https://images.unsplash.com/photo-1583863788434-e58a36330cf0?auto=format&fit=crop&w=640&q=70"},
        {2L, "Men Cotton T-Shirt", "Soft breathable cotton round-neck T-shirt for daily wear.", "499.00", 200,
            "Fashion", "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=640&q=70"},
        {3L, "Men Casual Shirt", "Slim-fit cotton casual shirt for office and outings.", "899.00", 150,
            "Fashion", "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?auto=format&fit=crop&w=640&q=70"},
        {2L, "Men Slim-Fit Jeans", "Stretchable denim jeans with a modern tapered fit.", "1299.00", 120,
            "Fashion", "https://images.unsplash.com/photo-1542272604-787c3835535d?auto=format&fit=crop&w=640&q=70"},
        {3L, "Women Summer Top", "Lightweight printed top, comfortable for warm days.", "699.00", 140,
            "Fashion", "https://images.unsplash.com/photo-1434389677669-e08b4cac3105?auto=format&fit=crop&w=640&q=70"},
        {2L, "Women Floral Dress", "Knee-length floral dress in soft flowy fabric.", "1499.00", 80,
            "Fashion", "https://images.unsplash.com/photo-1595777457583-95e059d581b8?auto=format&fit=crop&w=640&q=70"},
        {3L, "Women Cotton Kurta", "Straight-cut cotton kurta for everyday ethnic wear.", "999.00", 110,
            "Fashion", "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?auto=format&fit=crop&w=640&q=70"},
        {2L, "Unisex Fleece Hoodie", "Warm hooded sweatshirt with kangaroo pocket.", "1199.00", 100,
            "Fashion", "https://images.unsplash.com/photo-1556821840-3a63f95609a7?auto=format&fit=crop&w=640&q=70"},
        {3L, "Men Denim Jacket", "Classic button-front denim jacket for layering.", "1999.00", 60,
            "Fashion", "https://images.unsplash.com/photo-1551537482-f2075a1d41f2?auto=format&fit=crop&w=640&q=70"},
        {2L, "Street Runner Shoes", "Lightweight running shoes with cushioned sole.", "1999.00", 75,
            "Footwear", "https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=640&q=70"},
        {3L, "Urban Sneakers", "Stylish sneakers for college and street wear.", "2499.00", 65,
            "Footwear", "https://images.unsplash.com/photo-1549298916-b41d501d3772?auto=format&fit=crop&w=640&q=70"},
        {2L, "Casual Canvas Shoes", "Easy-wear canvas shoes for daily commuting.", "1299.00", 90,
            "Footwear", "https://images.unsplash.com/photo-1560343090-f0409e92791a?auto=format&fit=crop&w=640&q=70"},
        {3L, "Classic White Sneakers", "All-white sneakers that match every outfit.", "1799.00", 85,
            "Footwear", "https://images.unsplash.com/photo-1525966222134-fcfa99b8ae77?auto=format&fit=crop&w=640&q=70"},
        {2L, "Comfort-Strap Sandals", "Soft-sole sandals for all-day comfort.", "799.00", 110,
            "Footwear", "https://images.unsplash.com/photo-1603487742131-4160ec999306?auto=format&fit=crop&w=640&q=70"},
        {3L, "Daily-Wear Slippers", "Light anti-skid slippers for home and outdoors.", "399.00", 150,
            "Footwear", "https://images.unsplash.com/photo-1602293589930-45aad59ba3ab?auto=format&fit=crop&w=640&q=70"},
        {2L, "TurboMix 750W Mixer Grinder", "3-jar mixer grinder for chutneys, batter and spices.", "3499.00", 35,
            "Home & Kitchen", "https://images.unsplash.com/photo-1570222094114-d054a817e56b?auto=format&fit=crop&w=640&q=70"},
        {3L, "QuickBoil Electric Kettle 1.8L", "Fast-boiling kettle with auto cut-off for safety.", "1299.00", 55,
            "Home & Kitchen", "https://images.unsplash.com/photo-1565452344518-47faca79dc69?auto=format&fit=crop&w=640&q=70"},
        {2L, "Non-Stick Cookware Set", "3-piece cookware set for low-oil everyday cooking.", "2499.00", 40,
            "Home & Kitchen", "https://images.unsplash.com/photo-1556911220-bff31c812dba?auto=format&fit=crop&w=640&q=70"},
        {3L, "Airtight Storage Containers Set of 6", "Stackable containers to keep grains fresh.", "899.00", 95,
            "Home & Kitchen", "https://images.unsplash.com/photo-1584589167171-541ce45f1eea?auto=format&fit=crop&w=640&q=70"},
        {2L, "Steel Flask Bottle 1L", "Vacuum flask keeps drinks hot or cold for hours.", "599.00", 130,
            "Home & Kitchen", "https://images.unsplash.com/photo-1602143407151-7111542de6e8?auto=format&fit=crop&w=640&q=70"},
        {3L, "Cotton King Bedsheet", "Soft king-size bedsheet with two pillow covers.", "999.00", 70,
            "Home & Kitchen", "https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?auto=format&fit=crop&w=640&q=70"},
        {2L, "Study Desk Lamp", "Adjustable LED lamp for reading and work desks.", "799.00", 80,
            "Home & Kitchen", "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?auto=format&fit=crop&w=640&q=70"},
        {3L, "Neem Face Wash 150ml", "Gentle daily face wash for clear fresh skin.", "249.00", 160,
            "Beauty & Personal Care", "https://images.unsplash.com/photo-1556228720-195a672e8a03?auto=format&fit=crop&w=640&q=70"},
        {2L, "Daily Moisturizer Cream", "Lightweight cream for soft hydrated skin.", "399.00", 140,
            "Beauty & Personal Care", "https://images.unsplash.com/photo-1620916566398-39f1143ab7be?auto=format&fit=crop&w=640&q=70"},
        {3L, "Herbal Shampoo 340ml", "Mild shampoo for strong smooth hair.", "349.00", 150,
            "Beauty & Personal Care", "https://images.unsplash.com/photo-1535585209827-a15fcdbc4c2d?auto=format&fit=crop&w=640&q=70"},
        {2L, "Hair Care Combo Pack", "Oil and serum combo for hair fall control.", "299.00", 120,
            "Beauty & Personal Care", "https://images.unsplash.com/photo-1526947425960-945c6e72858f?auto=format&fit=crop&w=640&q=70"},
        {3L, "Men Grooming Kit", "Trimmer, razor and grooming essentials gift set.", "899.00", 75,
            "Beauty & Personal Care", "https://images.unsplash.com/photo-1621605815971-fbc98d665033?auto=format&fit=crop&w=640&q=70"},
        {2L, "Travel Backpack 32L", "Water-resistant backpack with laptop sleeve.", "1499.00", 85,
            "Bags, Watches & Accessories", "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=640&q=70"},
        {3L, "Laptop Messenger Bag", "Padded messenger bag for 15-inch laptops.", "1299.00", 65,
            "Bags, Watches & Accessories", "https://images.unsplash.com/photo-1491637639811-60e2756cc1c7?auto=format&fit=crop&w=640&q=70"},
        {2L, "Leather Wallet", "Slim bifold wallet with card slots.", "599.00", 180,
            "Bags, Watches & Accessories", "https://images.unsplash.com/photo-1627123424574-724758594e93?auto=format&fit=crop&w=640&q=70"},
        {3L, "Chrono Steel Watch", "Analog watch with steel strap and date window.", "2999.00", 45,
            "Bags, Watches & Accessories", "https://images.unsplash.com/photo-1524805444758-089113d48a6d?auto=format&fit=crop&w=640&q=70"},
        {2L, "Aviator Sunglasses", "UV-protected classic aviator sunglasses.", "1299.00", 95,
            "Bags, Watches & Accessories", "https://images.unsplash.com/photo-1572635196237-14b3f281503f?auto=format&fit=crop&w=640&q=70"},
        {3L, "Java Programming Guide", "Learn Java step by step with practice programs.", "650.00", 80,
            "Books & Stationery", "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=640&q=70"},
        {2L, "Exam Prep Solved Papers", "Previous years solved papers with explanations.", "450.00", 100,
            "Books & Stationery", "https://images.unsplash.com/photo-1434030216411-0b793f4b4173?auto=format&fit=crop&w=640&q=70"},
        {3L, "Ruled Notebook Set of 4", "200-page notebooks for school and office.", "299.00", 200,
            "Books & Stationery", "https://images.unsplash.com/photo-1456735190827-d1262f71b8a3?auto=format&fit=crop&w=640&q=70"},
        {2L, "School Stationery Set", "Pens, pencils, eraser and scale combo box.", "499.00", 170,
            "Books & Stationery", "https://images.unsplash.com/photo-1452860606245-08befc0ff44b?auto=format&fit=crop&w=640&q=70"},
        {3L, "Building Blocks 100pc", "Colorful blocks set for creative play.", "899.00", 90,
            "Toys & Kids", "https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4?auto=format&fit=crop&w=640&q=70"},
        {2L, "Kids Learning Tablet", "Educational games and rhymes tablet for kids.", "1499.00", 55,
            "Toys & Kids", "https://images.unsplash.com/photo-1587654780291-39c9404d746b?auto=format&fit=crop&w=640&q=70"},
        {3L, "Classic Chess Board Game", "Wooden chess set for family game nights.", "799.00", 70,
            "Toys & Kids", "https://images.unsplash.com/photo-1529699211952-734e80c4d42b?auto=format&fit=crop&w=640&q=70"},
        {2L, "Cricket Bat English Willow", "Lightweight bat for tennis and leather ball.", "1799.00", 50,
            "Sports & Fitness", "https://images.unsplash.com/photo-1531415074968-036ba1b575da?auto=format&fit=crop&w=640&q=70"},
        {3L, "Football Size 5", "Durable stitched football for ground matches.", "899.00", 100,
            "Sports & Fitness", "https://images.unsplash.com/photo-1575361204480-aadea25e6e68?auto=format&fit=crop&w=640&q=70"},
        {2L, "Speed Skipping Rope", "Adjustable rope for cardio and weight loss.", "349.00", 140,
            "Sports & Fitness", "https://images.unsplash.com/photo-1601422407692-ec4eeec1d9b3?auto=format&fit=crop&w=640&q=70"},
        {3L, "Non-Slip Yoga Mat 6mm", "Comfortable mat for yoga and exercise.", "799.00", 110,
            "Sports & Fitness", "https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?auto=format&fit=crop&w=640&q=70"},
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
