package com.js.jsmart.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Product entity. Money uses BigDecimal, never float. */
public class Product {
  private long id;
  private long sellerId;
  private String sellerName;
  private String name;
  private String description;
  private BigDecimal price;
  private int stockQty;
  private String category;
  private String imageUrl;
  private LocalDateTime createdAt;
  private double avgRating;
  private int reviewCount;

  public long getId() { return id; }
  public void setId(long id) { this.id = id; }
  public long getSellerId() { return sellerId; }
  public void setSellerId(long sellerId) { this.sellerId = sellerId; }
  public String getSellerName() { return sellerName; }
  public void setSellerName(String sellerName) { this.sellerName = sellerName; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public BigDecimal getPrice() { return price; }
  public void setPrice(BigDecimal price) { this.price = price; }
  public int getStockQty() { return stockQty; }
  public void setStockQty(int stockQty) { this.stockQty = stockQty; }
  public String getCategory() { return category; }
  public void setCategory(String category) { this.category = category; }
  public String getImageUrl() { return imageUrl; }
  public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public double getAvgRating() { return avgRating; }
  public void setAvgRating(double avgRating) { this.avgRating = avgRating; }
  public int getReviewCount() { return reviewCount; }
  public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }
}
