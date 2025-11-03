package com.smartShopper.smart_price_backend.dto.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductResponse {

    private Long id;
    private String title;
    private String brand;
    private String category;
    private String description;
    private String imageUrl;
    private BigDecimal price;
    private Double rating;
    private Integer reviewCount;
    private String platform;
    private String productUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public ProductResponse() {
    }

    public ProductResponse(String title, String brand, LocalDateTime createdAt, String description,
                           Long id, String imageUrl, String platform, BigDecimal price,
                           String productUrl, Double rating, Integer reviewCount, LocalDateTime updatedAt) {
        this.title = title;
        this.brand = brand;
        this.createdAt = createdAt;
        this.description = description;
        this.id = id;
        this.imageUrl = imageUrl;
        this.platform = platform;
        this.price = price;
        this.productUrl = productUrl;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.updatedAt = updatedAt;
    }

    // Alternative constructor for scraper
    public ProductResponse(String title, String brand, String platform, String productUrl,
                           BigDecimal price, String imageUrl, String category,
                           Double rating, Integer reviewCount, String description) {
        this.title = title;
        this.brand = brand;
        this.platform = platform;
        this.productUrl = productUrl;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "ProductResponse{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", brand='" + brand + '\'' +
                ", platform='" + platform + '\'' +
                ", price=" + price +
                '}';
    }
}