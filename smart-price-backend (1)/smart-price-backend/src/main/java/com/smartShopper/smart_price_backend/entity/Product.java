package com.smartShopper.smart_price_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 200)
    private String name;

    @Size(max = 2000)
    private String description;

    @NotBlank(message = "Brand is required")
    @Size(max = 100)
    private String brand;

    @NotBlank(message = "Category is required")
    @Size(max = 100)
    private String category;

    @NotBlank(message = "Title is required")
    @Size(max = 300)
    private String title;

    @Size(max = 100)
    private String sku; // optional

    @NotBlank(message = "Platform is required")
    @Size(max = 50)
    private String platform; // Amazon, Flipkart, etc.

    @NotBlank(message = "Product URL is required")
    @Size(max = 500)
    @Column(name = "platform_product_url", unique = true)
    private String platformProductUrl;

    @Size(max = 500)
    private String imageUrl;

    @NotNull(message = "Current price is required")
    @PositiveOrZero(message = "Current price must be zero or positive")
    @Column(name = "current_price")
    private BigDecimal currentPrice;

    @PositiveOrZero(message = "MRP must be zero or positive")
    private BigDecimal mrp;

    @DecimalMin(value = "0.0", message = "Rating cannot be negative")
    @DecimalMax(value = "5.0", message = "Rating cannot be more than 5")
    private Double rating;

    @PositiveOrZero(message = "Review count cannot be negative")
    private Integer reviewCount;

    @NotNull
    @Column(name = "last_scraped")
    private LocalDateTime lastScraped = LocalDateTime.now();


    // ===== Constructors =====
    public Product() {}

    public Product(String name, String description, String brand, String category,
                   String title, String sku, String platform, String platformProductUrl,
                   String imageUrl, BigDecimal currentPrice, BigDecimal mrp,
                   Double rating, Integer reviewCount, Instant lastScraped) {
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.category = category;
        this.title = title;
        this.platform = platform;
        this.platformProductUrl = platformProductUrl;
        this.imageUrl = imageUrl;
        this.currentPrice = currentPrice;
        this.mrp = mrp;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.lastScraped = LocalDateTime.now();
    }

    // ===== Getters & Setters =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getPlatformProductUrl() {
        return platformProductUrl;
    }

    public void setPlatformProductUrl(String platformProductUrl) {
        this.platformProductUrl = platformProductUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public BigDecimal getMrp() {
        return mrp;
    }

    public void setMrp(BigDecimal mrp) {
        this.mrp = mrp;
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

    public LocalDateTime  getLastScraped() {
        return lastScraped;
    }

    public void setLastScraped(LocalDateTime  lastScraped) {
        this.lastScraped = lastScraped;
    }
}
