package com.smartShopper.smart_price_backend.dto.Wishlist;

import com.smartShopper.smart_price_backend.dto.product.ProductResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WishlistResponse {
    private Long id;
    private ProductResponse product;
    private BigDecimal targetPrice;
    private boolean alertEnabled;
    private LocalDateTime addedAt;
    private LocalDateTime lastAlertSentAt;

    // Constructors
    public WishlistResponse() {
    }

    public WishlistResponse(LocalDateTime addedAt, boolean alertEnabled, Long id, LocalDateTime lastAlertSentAt, ProductResponse product, BigDecimal targetPrice) {
        this.addedAt = addedAt;
        this.alertEnabled = alertEnabled;
        this.id = id;
        this.lastAlertSentAt = lastAlertSentAt;
        this.product = product;
        this.targetPrice = targetPrice;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }

    public boolean isAlertEnabled() {
        return alertEnabled;
    }

    public void setAlertEnabled(boolean alertEnabled) {
        this.alertEnabled = alertEnabled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getLastAlertSentAt() {
        return lastAlertSentAt;
    }

    public void setLastAlertSentAt(LocalDateTime lastAlertSentAt) {
        this.lastAlertSentAt = lastAlertSentAt;
    }

    public ProductResponse getProduct() {
        return product;
    }

    public void setProduct(ProductResponse product) {
        this.product = product;
    }

    public BigDecimal getTargetPrice() {
        return targetPrice;
    }

    public void setTargetPrice(BigDecimal targetPrice) {
        this.targetPrice = targetPrice;
    }
}