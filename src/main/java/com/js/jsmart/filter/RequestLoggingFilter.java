package com.js.jsmart.filter;

import java.io.IOException;
import java.util.UUID;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Request logging filter: generates request ID, attaches to MDC, logs, cleans MDC.
 */
public class RequestLoggingFilter implements Filter {
  private static final Logger LOG = LoggerFactory.getLogger(RequestLoggingFilter.class);

  @Override
  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
      throws IOException, ServletException {
    String id = UUID.randomUUID().toString().substring(0, 8);
    MDC.put("requestId", id);
    try {
      if (req instanceof HttpServletRequest) {
        HttpServletRequest h = (HttpServletRequest) req;
        LOG.info("{} {}", h.getMethod(), h.getRequestURI());
      }
      chain.doFilter(req, resp);
    } finally {
      MDC.clear();
    }
  }
}
