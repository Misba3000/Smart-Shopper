package com.smartShopper.smart_price_backend.dto.product;

import java.math.BigDecimal;

public class ProductResponse {
    private String title;
    private String brand;
    private String platform;
    private String productUrl;
    private BigDecimal price;
    private String imageUrl;
    private String source;
    private Double rating;
    private Integer reviewCount;
    private String description;

    // Default constructor
    public ProductResponse() {}

    // Constructor matching your scraper usage
    public ProductResponse(String title, String brand, String platform, String productUrl,
                           BigDecimal price, String imageUrl, String source, Double rating,
                           Integer reviewCount, String description) {
        this.title = title;
        this.brand = brand;
        this.platform = platform;
        this.productUrl = productUrl;
        this.price = price;
        this.imageUrl = imageUrl;
        this.source = source;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.description = description;
    }

    // All getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public String getProductUrl() { return productUrl; }
    public void setProductUrl(String productUrl) { this.productUrl = productUrl; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    // For frontend compatibility - both getPrice() and getCurrentPrice()
    public BigDecimal getCurrentPrice() { return this.price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "ProductResponse{" +
                "title='" + title + '\'' +
                ", brand='" + brand + '\'' +
                ", platform='" + platform + '\'' +
                ", price=" + price +
                ", rating=" + rating +
                '}';
    }
}