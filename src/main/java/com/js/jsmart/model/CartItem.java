package com.js.jsmart.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Cart line. */
public class CartItem {
  private long id;
  private long userId;
  private long productId;
  private String productName;
  private BigDecimal unitPrice;
  private int quantity;
  private int stockQty;
  private String imageUrl;
  private LocalDateTime createdAt;

  public long getId() { return id; }
  public void setId(long id) { this.id = id; }
  public long getUserId() { return userId; }
  public void setUserId(long userId) { this.userId = userId; }
  public long getProductId() { return productId; }
  public void setProductId(long productId) { this.productId = productId; }
  public String getProductName() { return productName; }
  public void setProductName(String productName) { this.productName = productName; }
  public BigDecimal getUnitPrice() { return unitPrice; }
  public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
  public int getQuantity() { return quantity; }
  public void setQuantity(int quantity) { this.quantity = quantity; }
  public int getStockQty() { return stockQty; }
  public void setStockQty(int stockQty) { this.stockQty = stockQty; }
  public String getImageUrl() { return imageUrl; }
  public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
