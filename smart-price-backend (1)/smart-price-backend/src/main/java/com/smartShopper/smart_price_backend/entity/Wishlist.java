package com.smartShopper.smart_price_backend.entity;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "wishlist")
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String title;
    private String brand;
    private String platform;
    private String productUrl;
    private BigDecimal price;
    private String imageUrl;
    private Double rating;
    private Integer reviewCount;

    // Remove @GeneratedValue from platformProductId as it's not a primary key
    @Column(name = "platform_product_id", nullable = false)
    private String platformProductId;  // Changed from Long to String as product IDs are typically strings

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPlatformProductId() {
        return platformProductId;
    }

    public void setPlatformProductId(String platformProductId) {
        this.platformProductId = platformProductId;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Updated constructor to include platformProductId
    public Wishlist(User user, String title, String brand, String platform,
                    String productUrl, BigDecimal price, String imageUrl,
                    Double rating, Integer reviewCount, String platformProductId) {
        this.user = user;
        this.title = title;
        this.brand = brand;
        this.platform = platform;
        this.productUrl = productUrl;
        this.price = price;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.platformProductId = platformProductId;
    }

    public Wishlist() {}
}