package com.js.jsmart;

import com.js.jsmart.dao.JdbcProductDAO;
import com.js.jsmart.dao.JdbcUserDAO;
import com.js.jsmart.dao.ProductDAO;
import com.js.jsmart.dao.UserDAO;
import com.js.jsmart.model.Product;
import com.js.jsmart.model.Role;
import com.js.jsmart.model.User;
import com.zaxxer.hikari.HikariDataSource;
import java.math.BigDecimal;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ProductDAO tests incl. search injection safety.
 */
class ProductDAOTest {
  private static HikariDataSource ds;
  private ProductDAO products = new JdbcProductDAO();
  private UserDAO users = new JdbcUserDAO();

  @org.junit.jupiter.api.BeforeAll
  static void setup() throws Exception {
    ds = TestDb.init();
  }

  @AfterAll
  static void down() {
    if (ds != null) {
      ds.close();
    }
  }

  @BeforeEach
  void clean() throws Exception {
    TestDb.clean();
  }

  private long seller() throws Exception {
    User u = new User();
    u.setEmail("s@x.com");
    u.setPasswordHash("h");
    u.setFullName("S");
    u.setRole(Role.SELLER);
    return users.insert(u);
  }

  @Test
  void crud() throws Exception {
    long sid = seller();
    Product p = new Product();
    p.setSellerId(sid);
    p.setName("Mouse");
    p.setPrice(new BigDecimal("799.00"));
    p.setStockQty(10);
    p.setCategory("Electronics");
    long id = products.insert(p);
    assertTrue(products.findById(id).isPresent());
    assertEquals(1, products.search(null, "mou", 10, 0).size());
  }

  @Test
  void searchInjectionIsHarmless() throws Exception {
    long sid = seller();
    Product p = new Product();
    p.setSellerId(sid);
    p.setName("Keyboard");
    p.setPrice(new BigDecimal("999.00"));
    p.setStockQty(5);
    products.insert(p);
    // injection attempt in keyword must not dump schema or throw SQL error
    var r = products.search(null, "' OR '1'='1", 10, 0);
    assertEquals(0, r.size());
    assertEquals(1, products.countSearch(null, null));
  }
}
