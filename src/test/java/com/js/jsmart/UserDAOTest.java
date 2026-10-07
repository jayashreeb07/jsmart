package com.js.jsmart;

import com.js.jsmart.dao.JdbcUserDAO;
import com.js.jsmart.dao.UserDAO;
import com.js.jsmart.model.Role;
import com.js.jsmart.model.User;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * UserDAO tests on embedded H2.
 */
class UserDAOTest {
  private static HikariDataSource ds;
  private UserDAO dao = new JdbcUserDAO();

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

  @Test
  void insertAndFindByEmail() throws Exception {
    User u = new User();
    u.setEmail("a@x.com");
    u.setPasswordHash("hash");
    u.setFullName("A");
    u.setRole(Role.BUYER);
    long id = dao.insert(u);
    assertTrue(id > 0);
    assertTrue(dao.findByEmail("a@x.com").isPresent());
  }

  @Test
  void duplicateEmailThrows() throws Exception {
    User u = new User();
    u.setEmail("d@x.com");
    u.setPasswordHash("h");
    u.setFullName("D");
    u.setRole(Role.BUYER);
    dao.insert(u);
    assertThrows(Exception.class, () -> dao.insert(u));
  }

  @Test
  void sqlInjectionStoredLiterally() throws Exception {
    String evil = "' OR '1'='1";
    User u = new User();
    u.setEmail("evil@x.com");
    u.setPasswordHash("h");
    u.setFullName(evil);
    u.setRole(Role.BUYER);
    dao.insert(u);
    // PreparedStatement means table intact; only 1 user
    assertEquals(1, dao.count());
    assertTrue(dao.findByEmail("evil@x.com").isPresent());
  }
}
