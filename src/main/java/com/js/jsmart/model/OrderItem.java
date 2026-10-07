package com.js.jsmart.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Order line item. */
public class OrderItem {
  private long id;
  private long orderId;
  private long productId;
  private String productName;
  private String productImage;
  private int quantity;
  private BigDecimal unitPrice;
  private LocalDateTime createdAt;

  public long getId() { return id; }
  public void setId(long id) { this.id = id; }
  public long getOrderId() { return orderId; }
  public void setOrderId(long orderId) { this.orderId = orderId; }
  public long getProductId() { return productId; }
  public void setProductId(long productId) { this.productId = productId; }
  public String getProductName() { return productName; }
  public void setProductName(String productName) { this.productName = productName; }
  public String getProductImage() { return productImage; }
  public void setProductImage(String productImage) { this.productImage = productImage; }
  public int getQuantity() { return quantity; }
  public void setQuantity(int quantity) { this.quantity = quantity; }
  public BigDecimal getUnitPrice() { return unitPrice; }
  public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
