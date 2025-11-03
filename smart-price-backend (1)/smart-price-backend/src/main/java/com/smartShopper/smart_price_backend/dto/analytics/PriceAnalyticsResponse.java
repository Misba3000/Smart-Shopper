package com.smartShopper.smart_price_backend.dto.analytics;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PriceAnalyticsResponse {
    private Long productId;
    private String productTitle;
    private String platform;
    private BigDecimal currentPrice;
    private BigDecimal targetPrice;
    private BigDecimal lowestPrice;
    private BigDecimal highestPrice;
    private BigDecimal averagePrice;
    private BigDecimal priceChange;
    private BigDecimal priceChangePercentage;
    private String trend; // "up", "down", "stable"
    private boolean alertEnabled;
    private LocalDateTime lastTracked;
    private List<PriceDataPoint> priceHistory;

    // Inner class for price data points
    public static class PriceDataPoint {
        private BigDecimal price;
        private LocalDateTime date;

        public PriceDataPoint(BigDecimal price, LocalDateTime date) {
            this.price = price;
            this.date = date;
        }

        // Getters and Setters
        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public LocalDateTime getDate() {
            return date;
        }

        public void setDate(LocalDateTime date) {
            this.date = date;
        }
    }

    // Constructors
    public PriceAnalyticsResponse() {
    }

    // Getters and Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public BigDecimal getTargetPrice() {
        return targetPrice;
    }

    public void setTargetPrice(BigDecimal targetPrice) {
        this.targetPrice = targetPrice;
    }

    public BigDecimal getLowestPrice() {
        return lowestPrice;
    }

    public void setLowestPrice(BigDecimal lowestPrice) {
        this.lowestPrice = lowestPrice;
    }

    public BigDecimal getHighestPrice() {
        return highestPrice;
    }

    public void setHighestPrice(BigDecimal highestPrice) {
        this.highestPrice = highestPrice;
    }

    public BigDecimal getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(BigDecimal averagePrice) {
        this.averagePrice = averagePrice;
    }

    public BigDecimal getPriceChange() {
        return priceChange;
    }

    public void setPriceChange(BigDecimal priceChange) {
        this.priceChange = priceChange;
    }

    public BigDecimal getPriceChangePercentage() {
        return priceChangePercentage;
    }

    public void setPriceChangePercentage(BigDecimal priceChangePercentage) {
        this.priceChangePercentage = priceChangePercentage;
    }

    public String getTrend() {
        return trend;
    }

    public void setTrend(String trend) {
        this.trend = trend;
    }

    public boolean isAlertEnabled() {
        return alertEnabled;
    }

    public void setAlertEnabled(boolean alertEnabled) {
        this.alertEnabled = alertEnabled;
    }

    public LocalDateTime getLastTracked() {
        return lastTracked;
    }

    public void setLastTracked(LocalDateTime lastTracked) {
        this.lastTracked = lastTracked;
    }

    public List<PriceDataPoint> getPriceHistory() {
        return priceHistory;
    }

    public void setPriceHistory(List<PriceDataPoint> priceHistory) {
        this.priceHistory = priceHistory;
    }
}