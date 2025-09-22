package com.smartShopper.smart_price_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "price_history")
public class PriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "platform_product_id", nullable = false)
    private PlatformProduct platformProduct;

    @DecimalMin("0.0")
    private Double price;

    public Double getMrp() {
        return mrp;
    }

    public void setMrp(Double mrp) {
        this.mrp = mrp;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getInStock() {
        return isInStock;
    }

    public void setInStock(Boolean inStock) {
        isInStock = inStock;
    }

    public PlatformProduct getPlatformProduct() {
        return platformProduct;
    }

    public void setPlatformProduct(PlatformProduct platformProduct) {
        this.platformProduct = platformProduct;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public LocalDateTime getScrapedAt() {
        return scrapedAt;
    }

    public void setScrapedAt(LocalDateTime scrapedAt) {
        this.scrapedAt = scrapedAt;
    }

    private Double mrp;

    @Column(length = 10)
    private String currency = "INR";

    private Boolean isInStock = true;

    private LocalDateTime scrapedAt = LocalDateTime.now();

    // Getters & setters
}
