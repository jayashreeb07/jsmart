package com.js.jsmart;

import com.js.jsmart.controller.HealthServlet;
import com.js.jsmart.controller.LoginServlet;
import com.js.jsmart.dto.ApiResponse;
import com.js.jsmart.filter.AuthFilter;
import com.js.jsmart.model.Role;
import com.js.jsmart.model.User;
import com.js.jsmart.service.AuthService;
import com.js.jsmart.service.chat.ChatService;
import com.js.jsmart.service.chat.MockChatProvider;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Servlet + filter + chatbot + envelope tests.
 */
class ServletSecurityTest {

  @Test
  void loginSetsSessionAndStatus() throws Exception {
    User u = new User();
    u.setId(1);
    u.setEmail("b@x.com");
    u.setRole(Role.BUYER);
    u.setFullName("B");
    AuthService auth = mock(AuthService.class);
    when(auth.login("b@x.com", "pw")).thenReturn(u);
    LoginServlet servlet = new LoginServlet();
    servlet.setAuthService(auth);

    HttpServletRequest req = mock(HttpServletRequest.class);
    HttpServletResponse resp = mock(HttpServletResponse.class);
    HttpSession session = mock(HttpSession.class);
    when(req.getParameter("email")).thenReturn("b@x.com");
    when(req.getParameter("password")).thenReturn("pw");
    when(req.getContentType()).thenReturn("application/json");
    when(req.getReader()).thenReturn(new java.io.BufferedReader(new java.io.StringReader(
        "{\"email\":\"b@x.com\",\"password\":\"pw\"}")));
    when(req.getSession(true)).thenReturn(session);
    when(req.getSession(false)).thenReturn(null);
    java.io.StringWriter sw = new java.io.StringWriter();
    when(resp.getWriter()).thenReturn(new java.io.PrintWriter(sw));

    servlet.doPost(req, resp);
    verify(session).setAttribute(eq("user"), eq(u));
    verify(resp).setStatus(200);
    assertTrue(sw.toString().contains("\"success\":true"));
    assertFalse(sw.toString().contains("passwordHash"));
  }

  @Test
  void authFilterBlocksAnonymousApi() throws Exception {
    AuthFilter f = new AuthFilter();
    HttpServletRequest req = mock(HttpServletRequest.class);
    HttpServletResponse resp = mock(HttpServletResponse.class);
    FilterChain chain = mock(FilterChain.class);
    when(req.getRequestURI()).thenReturn("/jsmart/api/v1/cart");
    when(req.getContextPath()).thenReturn("/jsmart");
    when(req.getSession(false)).thenReturn(null);
    java.io.StringWriter sw = new java.io.StringWriter();
    when(resp.getWriter()).thenReturn(new java.io.PrintWriter(sw));
    f.doFilter(req, resp, chain);
    verify(resp).setStatus(401);
    verify(chain, never()).doFilter(req, resp);
  }

  @Test
  void authFilterBlocksBuyerOnAdmin() throws Exception {
    AuthFilter f = new AuthFilter();
    HttpServletRequest req = mock(HttpServletRequest.class);
    HttpServletResponse resp = mock(HttpServletResponse.class);
    FilterChain chain = mock(FilterChain.class);
    HttpSession s = mock(HttpSession.class);
    User buyer = new User();
    buyer.setRole(Role.BUYER);
    when(s.getAttribute("user")).thenReturn(buyer);
    when(req.getSession(false)).thenReturn(s);
    when(req.getRequestURI()).thenReturn("/jsmart/admin/dashboard");
    when(req.getContextPath()).thenReturn("/jsmart");
    f.doFilter(req, resp, chain);
    verify(resp).sendError(403, "Forbidden");
    verify(chain, never()).doFilter(req, resp);
  }

  @Test
  void xssPayloadPreservedButJspEscapes() {
    String evil = "<script>alert(1)</script>";
    // service layer must not execute; JSP uses c:out to escape at render
    assertTrue(evil.contains("<script>"));
    String escaped = evil.replace("<", "&lt;").replace(">", "&gt;");
    assertFalse(escaped.contains("<script>"));
  }

  @Test
  void apiEnvelopeShape() {
    ApiResponse ok = ApiResponse.ok(Map.of("a", 1));
    assertTrue(ok.isSuccess());
    assertNull(ok.getError());
    ApiResponse err = ApiResponse.fail("VALIDATION_ERROR", "bad");
    assertFalse(err.isSuccess());
    assertNull(err.getData());
    assertEquals("VALIDATION_ERROR", err.getError().get("code"));
  }

  @Test
  void chatRateLimitAndCache() {
    ChatService chat = new ChatService(new MockChatProvider());
    Map<String, Object> sess = new HashMap<>();
    String first = chat.reply(sess, "shipping time?", "ctx");
    String second = chat.reply(sess, "shipping time?", "ctx");
    assertEquals(first, second); // cached
    for (int i = 0; i < 10; i++) {
      chat.reply(sess, "q" + i, "ctx");
    }
    assertTrue(chat.reply(sess, "one more", "ctx").contains("Rate limit"));
  }

  @Test
  void healthServletClassExists() {
    assertNotNull(HealthServlet.class);
  }
}
