package com.js.jsmart;

import com.js.jsmart.dao.UserDAO;
import com.js.jsmart.dto.RegisterRequest;
import com.js.jsmart.exception.AppException;
import com.js.jsmart.model.Role;
import com.js.jsmart.model.User;
import com.js.jsmart.service.AuthService;
import com.js.jsmart.util.PasswordUtil;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AuthService tests with mocked DAO.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
  @Mock UserDAO userDAO;

  private RegisterRequest req(String email, String pass, String name, String role) {
    RegisterRequest r = new RegisterRequest();
    r.setEmail(email);
    r.setPassword(pass);
    r.setFullName(name);
    r.setRole(role);
    return r;
  }

  @Test
  void registerHashesPassword() throws Exception {
    when(userDAO.findByEmail("b@x.com")).thenReturn(Optional.empty());
    when(userDAO.insert(any())).thenAnswer(i -> 7L);
    AuthService s = new AuthService(userDAO);
    var out = s.register(req("b@x.com", "secret1", "Bob", "BUYER"));
    assertEquals(7L, out.getId());
    verify(userDAO).insert(argThat(u -> !u.getPasswordHash().equals("secret1")
        && u.getPasswordHash().startsWith("$2a$")));
  }

  @Test
  void adminSignupForbidden() {
    AuthService s = new AuthService(userDAO);
    assertThrows(AppException.class, () -> s.register(req("a@x.com", "secret1", "A", "ADMIN")));
  }

  @Test
  void duplicateEmailConflict() throws Exception {
    User existing = new User();
    existing.setRole(Role.BUYER);
    when(userDAO.findByEmail("b@x.com")).thenReturn(Optional.of(existing));
    AuthService s = new AuthService(userDAO);
    assertThrows(AppException.class, () -> s.register(req("b@x.com", "secret1", "Bob", "BUYER")));
  }

  @Test
  void loginBadCredentials() throws Exception {
    User u = new User();
    u.setEmail("b@x.com");
    u.setPasswordHash(PasswordUtil.hash("rightpass"));
    u.setRole(Role.BUYER);
    when(userDAO.findByEmail("b@x.com")).thenReturn(Optional.of(u));
    AuthService s = new AuthService(userDAO);
    assertThrows(AppException.class, () -> s.login("b@x.com", "wrongpass"));
    assertNotNull(s.login("b@x.com", "rightpass"));
  }

  @Test
  void passwordNeverMd5() {
    String h = PasswordUtil.hash("hello123");
    assertTrue(h.startsWith("$2a$"));
  }
}
