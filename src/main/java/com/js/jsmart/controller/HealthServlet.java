package com.js.jsmart.controller;

import com.js.jsmart.dto.ApiResponse;
import com.js.jsmart.util.DbUtil;
import com.js.jsmart.util.JsonUtil;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** GET /api/v1/health : verifies DB connectivity. */
@WebServlet("/api/v1/health")
public class HealthServlet extends BaseServlet {
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Map<String, String> data = new HashMap<>();
    data.put("status", "UP");
    try (Connection c = DbUtil.getConnection();
         PreparedStatement ps = c.prepareStatement("SELECT 1");
         ResultSet rs = ps.executeQuery()) {
      rs.next();
      data.put("db", "UP");
      JsonUtil.write(resp, 200, data);
    } catch (Exception e) {
      log.error("Health check failed", e);
      data.put("db", "DOWN");
      JsonUtil.write(resp, 500, ApiResponse.fail("DB_DOWN", "Database unreachable"));
    }
  }
}
