package com.js.jsmart.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Session flash messages for browser flows (Post-Redirect-Get).
 * API/JSON responses are unaffected. Messages are consumed once.
 */
public final class Flash {
  public static final String OK_KEY = "flashOk";
  public static final String ERR_KEY = "flashErr";

  private Flash() { }

  /** Store a success message. */
  public static void success(HttpServletRequest req, String message) {
    HttpSession s = req.getSession(true);
    s.setAttribute(OK_KEY, message);
  }

  /** Store an error message. */
  public static void error(HttpServletRequest req, String message) {
    HttpSession s = req.getSession(true);
    s.setAttribute(ERR_KEY, message);
  }

  /**
   * Store a full-page success screen payload, then redirect to /success.jsp.
   * @param title heading text
   * @param message sub text
   * @param button button label
   * @param link context-relative button target
   */
  public static void successPage(HttpServletRequest req, String title,
      String message, String button, String link) {
    HttpSession s = req.getSession(true);
    s.setAttribute("successTitle", title);
    s.setAttribute("successMsg", message);
    s.setAttribute("successBtn", button);
    s.setAttribute("successLink", link);
  }
}
