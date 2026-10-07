package com.js.jsmart.controller;

import com.js.jsmart.dao.DAOFactory;
import com.js.jsmart.dto.ApiResponse;
import com.js.jsmart.model.User;
import com.js.jsmart.service.ReviewService;
import com.js.jsmart.util.JsonUtil;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Reviews API (F8). POST /api/v1/reviews */
@WebServlet("/api/v1/reviews")
public class ReviewServlet extends BaseServlet {
  private ReviewService reviews = new ReviewService(DAOFactory.reviews(), DAOFactory.orders());

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    try {
      User u = requireUser(req);
      long productId;
      int rating;
      String comment = req.getParameter("comment");
      if (req.getContentType() != null && req.getContentType().contains("json")) {
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> body = JsonUtil.fromBody(req, java.util.Map.class);
        productId = JsonUtil.toLong(body.get("productId"));
        rating = JsonUtil.toInt(body.get("rating"));
        if (body.get("comment") != null) {
          comment = String.valueOf(body.get("comment"));
        }
      } else {
        productId = Long.parseLong(req.getParameter("productId"));
        rating = Integer.parseInt(req.getParameter("rating"));
      }
      long id = reviews.add(u.getId(), productId, rating, comment);
      // form flow back to product page
      if (req.getContentType() == null || !req.getContentType().contains("json")) {
        com.js.jsmart.util.Flash.success(req, "Review submitted successfully.");
        resp.sendRedirect(req.getContextPath() + "/product?id=" + productId);
        return;
      }
      JsonUtil.write(resp, 201, ApiResponse.ok(java.util.Collections.singletonMap("id", id)));
    } catch (Exception e) {
      if (req.getContentType() == null || !req.getContentType().contains("json")) {
        String pid = req.getParameter("productId");
        com.js.jsmart.util.Flash.error(req, friendly(e));
        try {
          resp.sendRedirect(req.getContextPath() + "/product?id=" + (pid == null ? "" : pid));
        } catch (IOException ex) {
          log.error("review redirect failed", ex);
        }
        return;
      }
      sendError(resp, e);
    }
  }

  /** Visible for tests. */
  public void setReviewService(ReviewService s) {
    this.reviews = s;
  }
}
