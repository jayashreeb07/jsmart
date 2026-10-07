package com.js.jsmart.controller;

import com.js.jsmart.dao.DAOFactory;
import com.js.jsmart.dto.ApiResponse;
import com.js.jsmart.exception.NotFoundException;
import com.js.jsmart.model.Product;
import com.js.jsmart.model.Review;
import com.js.jsmart.service.ProductService;
import com.js.jsmart.service.ReviewService;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Page + API for product browsing (F3). */
@WebServlet({"/products", "/product", "/api/v1/products", "/api/v1/products/*"})
public class ProductServlet extends BaseServlet {
  private ProductService products = new ProductService(DAOFactory.products());
  private ReviewService reviews =
      new ReviewService(DAOFactory.reviews(), DAOFactory.orders());

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String uri = req.getRequestURI().substring(req.getContextPath().length());
    boolean isApi = uri.startsWith("/api/");
    try {
      if (isApi && uri.matches(".*/products/\\d+")) {
        long id = Long.parseLong(uri.substring(uri.lastIndexOf('/') + 1));
        Product p = products.getOrThrow(id);
        com.js.jsmart.util.JsonUtil.write(resp, 200, ApiResponse.ok(p));
        return;
      }
      if (isApi) {
        String category = req.getParameter("category");
        String q = req.getParameter("q");
        List<Product> list = products.browse(category, q, price(req.getParameter("minPrice")),
            price(req.getParameter("maxPrice")), req.getParameter("sort"), 50, 0);
        com.js.jsmart.util.JsonUtil.write(resp, 200, ApiResponse.ok(list));
        return;
      }
      if ("/product".equals(uri)) {
        String idRaw = req.getParameter("id");
        if (idRaw == null) {
          throw new NotFoundException("Missing product id");
        }
        long id = Long.parseLong(idRaw);
        Product p = products.getOrThrow(id);
        List<Review> rev = reviews.forProduct(id);
        req.setAttribute("product", p);
        req.setAttribute("reviews", rev);
        req.getRequestDispatcher("/WEB-INF/views/product-detail.jsp").forward(req, resp);
        return;
      }
      String category = req.getParameter("category");
      String q = req.getParameter("q");
      String sort = req.getParameter("sort");
      req.setAttribute("products",
          products.browse(category, q, price(req.getParameter("minPrice")),
              price(req.getParameter("maxPrice")), sort, 50, 0));
      req.getRequestDispatcher("/WEB-INF/views/product-list.jsp").forward(req, resp);
    } catch (NumberFormatException e) {
      if (isApi) {
        sendError(resp, new NotFoundException("Invalid product id"));
      } else {
        req.setAttribute("message", "Invalid product id");
        req.getRequestDispatcher("/WEB-INF/views/error/404.jsp").forward(req, resp);
      }
    } catch (Exception e) {
      if (isApi) {
        sendError(resp, e);
      } else {
        pageError(req, resp, e);
      }
    }
  }

  /** Visible for tests. */
  public void setProductService(ProductService s) {
    this.products = s;
  }

  /** Parse optional price, ignoring invalid input. */
  private static java.math.BigDecimal price(String raw) {
    if (raw == null || raw.isBlank()) {
      return null;
    }
    try {
      java.math.BigDecimal v = new java.math.BigDecimal(raw.trim());
      return v.signum() < 0 ? null : v;
    } catch (NumberFormatException e) {
      return null;
    }
  }
}
