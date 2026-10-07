package com.js.jsmart.service;

import com.js.jsmart.dao.ProductDAO;
import com.js.jsmart.dto.ProductDTO;
import com.js.jsmart.exception.AppException;
import com.js.jsmart.exception.ForbiddenException;
import com.js.jsmart.exception.NotFoundException;
import com.js.jsmart.exception.ValidationException;
import com.js.jsmart.model.Product;
import com.js.jsmart.util.ValidationUtil;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Product business rules incl. seller-ownership checks.
 */
public class ProductService {
  private final ProductDAO productDAO;

  /** Create service. */
  public ProductService(ProductDAO productDAO) {
    this.productDAO = productDAO;
  }

  /** Browse with filters. */
  public List<Product> browse(String category, String keyword, int limit, int offset) {
    return browse(category, keyword, null, null, null, limit, offset);
  }

  /**
   * Browse with optional price range and sort. Filtering/sorting is applied
   * in memory over the DAO result; invalid values are ignored.
   * @param minPrice minimum price or null
   * @param maxPrice maximum price or null
   * @param sort one of price_asc, price_desc, rating, newest
   * @return filtered products
   */
  public List<Product> browse(String category, String keyword, java.math.BigDecimal minPrice,
      java.math.BigDecimal maxPrice, String sort, int limit, int offset) {
    try {
      List<Product> list =
          productDAO.search(category, keyword, limit <= 0 ? 24 : limit, Math.max(0, offset));
      if (minPrice != null) {
        list.removeIf(p -> p.getPrice() == null || p.getPrice().compareTo(minPrice) < 0);
      }
      if (maxPrice != null) {
        list.removeIf(p -> p.getPrice() == null || p.getPrice().compareTo(maxPrice) > 0);
      }
      if ("price_asc".equals(sort)) {
        list.sort(java.util.Comparator.comparing(Product::getPrice));
      } else if ("price_desc".equals(sort)) {
        list.sort(java.util.Comparator.comparing(Product::getPrice).reversed());
      } else if ("rating".equals(sort)) {
        list.sort(java.util.Comparator.comparingDouble(Product::getAvgRating).reversed());
      }
      return list;
    } catch (SQLException e) {
      throw new AppException(500, "DB_ERROR", "Search failed");
    }
  }

  /** Get by id or 404. */
  public Product getOrThrow(long id) {
    try {
      return productDAO.findById(id).orElseThrow(() -> new NotFoundException("Product not found"));
    } catch (SQLException e) {
      throw new AppException(500, "DB_ERROR", "Lookup failed");
    }
  }

  /** Seller creates product (sellerId from session, never browser). */
  public long create(long sellerId, ProductDTO dto) {
    Map<String, String> errors = ValidationUtil.validateProduct(dto.getName(), dto.getPrice(), dto.getStockQty());
    if (!errors.isEmpty()) {
      throw new ValidationException("Validation failed", errors);
    }
    Product p = new Product();
    p.setSellerId(sellerId);
    p.setName(dto.getName().trim());
    p.setDescription(dto.getDescription());
    p.setPrice(dto.getPrice());
    p.setStockQty(dto.getStockQty());
    p.setCategory(dto.getCategory());
    p.setImageUrl(dto.getImageUrl());
    try {
      return productDAO.insert(p);
    } catch (SQLException e) {
      throw new AppException(500, "DB_ERROR", "Create failed");
    }
  }

  /** Seller updates own product only. */
  public void update(long sellerId, long productId, ProductDTO dto) {
    Product existing = getOrThrow(productId);
    if (existing.getSellerId() != sellerId) {
      throw new ForbiddenException("You can modify only your own products");
    }
    Map<String, String> errors = ValidationUtil.validateProduct(dto.getName(), dto.getPrice(), dto.getStockQty());
    if (!errors.isEmpty()) {
      throw new ValidationException("Validation failed", errors);
    }
    existing.setName(dto.getName().trim());
    existing.setDescription(dto.getDescription());
    existing.setPrice(dto.getPrice());
    existing.setStockQty(dto.getStockQty());
    existing.setCategory(dto.getCategory());
    existing.setImageUrl(dto.getImageUrl());
    try {
      productDAO.update(existing);
    } catch (SQLException e) {
      throw new AppException(500, "DB_ERROR", "Update failed");
    }
  }

  /** Seller deletes own product only. */
  public void delete(long sellerId, long productId) {
    Product existing = getOrThrow(productId);
    if (existing.getSellerId() != sellerId) {
      throw new ForbiddenException("You can delete only your own products");
    }
    deleteOrThrow(productId);
  }

  /** Admin removes any listing (moderation). */
  public void adminDelete(long productId) {
    getOrThrow(productId);
    deleteOrThrow(productId);
  }

  /** Delete with clean conflict when orders reference the product. */
  private void deleteOrThrow(long productId) {
    try {
      productDAO.delete(productId);
    } catch (SQLException e) {
      if (e.getSQLState() != null && e.getSQLState().startsWith("23")) {
        throw new AppException(409, "CONFLICT", "Cannot delete: product has existing orders");
      }
      throw new AppException(500, "DB_ERROR", "Delete failed");
    }
  }

  /** Seller listings. */
  public List<Product> sellerProducts(long sellerId) {
    try {
      return productDAO.findBySeller(sellerId);
    } catch (SQLException e) {
      throw new AppException(500, "DB_ERROR", "Load failed");
    }
  }
}
