package com.js.jsmart.dto;

import java.math.BigDecimal;

/** Product request/response DTO. */
public class ProductDTO {
  private Long id;
  private String name;
  private String description;
  private BigDecimal price;
  private Integer stockQty;
  private String category;
  private String imageUrl;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public BigDecimal getPrice() { return price; }
  public void setPrice(BigDecimal price) { this.price = price; }
  public Integer getStockQty() { return stockQty; }
  public void setStockQty(Integer stockQty) { this.stockQty = stockQty; }
  public String getCategory() { return category; }
  public void setCategory(String category) { this.category = category; }
  public String getImageUrl() { return imageUrl; }
  public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
