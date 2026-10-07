package com.js.jsmart.controller;

import com.js.jsmart.dto.ApiResponse;
import com.js.jsmart.exception.AppException;
import com.js.jsmart.exception.ValidationException;
import com.js.jsmart.model.User;
import com.js.jsmart.util.JsonUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thin-servlet base: session helpers + centralized error mapping (no stack traces to users).
 */
public abstract class BaseServlet extends HttpServlet {
  protected final Logger log = LoggerFactory.getLogger(getClass());

  /** Current user or null. */
  protected User currentUser(HttpServletRequest req) {
    HttpSession s = req.getSession(false);
    return s == null ? null : (User) s.getAttribute("user");
  }

  /** Require login. */
  protected User requireUser(HttpServletRequest req) {
    User u = currentUser(req);
    if (u == null) {
      throw new com.js.jsmart.exception.UnauthorizedException("Login required");
    }
    return u;
  }

  /** Send JSON error with correct status. */
  protected void sendError(HttpServletResponse resp, Exception e) throws IOException {
    if (e instanceof ValidationException) {
      ValidationException v = (ValidationException) e;
      JsonUtil.write(resp, 400, ApiResponse.fail(v.getCode(), v.getMessage(), v.getFields()));
    } else if (e instanceof AppException) {
      AppException a = (AppException) e;
      JsonUtil.write(resp, a.getHttpStatus(), ApiResponse.fail(a.getCode(), a.getMessage()));
    } else {
      log.error("Unhandled error", e);
      JsonUtil.write(resp, 500, ApiResponse.fail("SERVER_ERROR", "Something went wrong"));
    }
  }

  /** Handle page errors without stack traces. */
  protected void pageError(HttpServletRequest req, HttpServletResponse resp, Exception e)
      throws ServletException, IOException {
    log.error("Page error", e);
    req.setAttribute("message", e.getMessage());
    req.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(req, resp);
  }

  /**
   * Human-readable message for browser form flows. Technical detail stays in logs.
   * @param e failure
   * @return clean message safe to show users
   */
  protected String friendly(Exception e) {
    if (e instanceof ValidationException) {
      return e.getMessage();
    }
    if (e instanceof AppException) {
      AppException a = (AppException) e;
      if ("DB_ERROR".equals(a.getCode()) || "CHECKOUT_FAILED".equals(a.getCode())
          || "SERVER_ERROR".equals(a.getCode())) {
        log.error("Browser flow error", e);
        return "Something went wrong. Please try again.";
      }
      return a.getMessage();
    }
    log.error("Browser flow error", e);
    return "Something went wrong. Please try again.";
  }

  /**
   * Redirect target for browser flows: back to referring page or a fallback.
   * @param req request
   * @param fallback context-relative fallback path
   * @return redirect URL
   */
  protected String back(HttpServletRequest req, String fallback) {
    String ref = req.getHeader("Referer");
    String ctx = req.getContextPath();
    if (ref != null && ref.contains(ctx + "/")) {
      return ref;
    }
    return ctx + fallback;
  }
}
