package com.smartShopper.smart_price_backend.dto.Wishlist;


import java.math.BigDecimal;

public class AddToWishlistRequest {
    private String title;
    private String brand;
    private String platform;
    private String productUrl;
    private BigDecimal price;
    private BigDecimal currentPrice;
    private Double rating;
    private Integer reviewCount;
    private String imageUrl;
    private String description;
    private BigDecimal targetPrice;   // for alerts
    private boolean alertEnabled = true;

    public AddToWishlistRequest() {
    }

    public AddToWishlistRequest(boolean alertEnabled, String brand, BigDecimal currentPrice, String description, String imageUrl, String platform, BigDecimal price, String productUrl, Double rating, Integer reviewCount, BigDecimal targetPrice, String title) {
        this.alertEnabled = alertEnabled;
        this.brand = brand;
        this.currentPrice = currentPrice;
        this.description = description;
        this.imageUrl = imageUrl;
        this.platform = platform;
        this.price = price;
        this.productUrl = productUrl;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.targetPrice = targetPrice;
        this.title = title;
    }

    public boolean isAlertEnabled() {
        return alertEnabled;
    }

    public void setAlertEnabled(boolean alertEnabled) {
        this.alertEnabled = alertEnabled;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
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

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
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

    public BigDecimal getTargetPrice() {
        return targetPrice;
    }

    public void setTargetPrice(BigDecimal targetPrice) {
        this.targetPrice = targetPrice;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
