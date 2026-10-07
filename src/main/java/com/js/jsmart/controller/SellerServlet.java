package com.js.jsmart.controller;

import com.js.jsmart.dao.DAOFactory;
import com.js.jsmart.dto.ApiResponse;
import com.js.jsmart.dto.ProductDTO;
import com.js.jsmart.model.Product;
import com.js.jsmart.model.User;
import com.js.jsmart.service.ProductService;
import com.js.jsmart.util.JsonUtil;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Seller dashboard + CRUD (F2). Ownership verified server-side. */
@WebServlet({"/seller/dashboard", "/seller/products", "/api/v1/seller/products", "/api/v1/seller/products/*"})
public class SellerServlet extends BaseServlet {
  private ProductService products = new ProductService(DAOFactory.products());

  private ProductDTO bind(HttpServletRequest req) throws IOException {
    if (req.getContentType() != null && req.getContentType().contains("json")) {
      ProductDTO d = JsonUtil.fromBody(req, ProductDTO.class);
      if (d != null) {
        return d;
      }
    }
    ProductDTO d = new ProductDTO();
    d.setName(req.getParameter("name"));
    d.setDescription(req.getParameter("description"));
    try {
      d.setPrice(req.getParameter("price") == null ? null
          : new java.math.BigDecimal(req.getParameter("price")));
    } catch (NumberFormatException e) {
      d.setPrice(null);
    }
    try {
      d.setStockQty(req.getParameter("stockQty") == null ? null
          : Integer.parseInt(req.getParameter("stockQty")));
    } catch (NumberFormatException e) {
      d.setStockQty(null);
    }
    d.setCategory(req.getParameter("category"));
    d.setImageUrl(req.getParameter("imageUrl"));
    return d;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String uri = req.getRequestURI().substring(req.getContextPath().length());
    try {
      User u = requireUser(req);
      if (!"SELLER".equals(u.getRole().name()) && !"ADMIN".equals(u.getRole().name())) {
        resp.sendError(403);
        return;
      }
      List<Product> list = products.sellerProducts(u.getId());
      if (uri.startsWith("/api/")) {
        JsonUtil.write(resp, 200, ApiResponse.ok(list));
        return;
      }
      req.setAttribute("products", list);
      req.getRequestDispatcher("/WEB-INF/views/seller/dashboard.jsp").forward(req, resp);
    } catch (Exception e) {
      pageError(req, resp, e);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String uri = req.getRequestURI().substring(req.getContextPath().length());
    boolean isApi = uri.startsWith("/api/");
    try {
      User u = requireUser(req);
      ProductDTO dto = bind(req);
      long id = products.create(u.getId(), dto);
      if (isApi) {
        JsonUtil.write(resp, 201, ApiResponse.ok(java.util.Collections.singletonMap("id", id)));
      } else {
        resp.sendRedirect(req.getContextPath() + "/seller/dashboard");
      }
    } catch (Exception e) {
      if (isApi) {
        sendError(resp, e);
      } else {
        pageError(req, resp, e);
      }
    }
  }

  @Override
  protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    try {
      User u = requireUser(req);
      String uri = req.getRequestURI();
      long id = Long.parseLong(uri.substring(uri.lastIndexOf('/') + 1));
      products.update(u.getId(), id, bind(req));
      JsonUtil.write(resp, 200, ApiResponse.ok(java.util.Collections.singletonMap("id", id)));
    } catch (Exception e) {
      sendError(resp, e);
    }
  }

  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    try {
      User u = requireUser(req);
      String uri = req.getRequestURI();
      long id = Long.parseLong(uri.substring(uri.lastIndexOf('/') + 1));
      products.delete(u.getId(), id);
      JsonUtil.write(resp, 200, ApiResponse.ok(java.util.Collections.singletonMap("deleted", id)));
    } catch (Exception e) {
      sendError(resp, e);
    }
  }

  /** Visible for tests. */
  public void setProductService(ProductService s) {
    this.products = s;
  }
}
