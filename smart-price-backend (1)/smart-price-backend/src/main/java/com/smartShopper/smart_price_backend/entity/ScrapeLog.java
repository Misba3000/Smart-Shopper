package com.smartShopper.smart_price_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "scrape_log")
public class ScrapeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="platform_product_id", nullable=false)
    private PlatformProduct platformProduct;

    @Column(nullable=false, length=50)
    private String status; // SUCCESS / FAILED

    private String errorMessage;

    public Integer getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Integer durationMs) {
        this.durationMs = durationMs;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlatformProduct getPlatformProduct() {
        return platformProduct;
    }

    public void setPlatformProduct(PlatformProduct platformProduct) {
        this.platformProduct = platformProduct;
    }

    public LocalDateTime getScrapedAt() {
        return scrapedAt;
    }

    public void setScrapedAt(LocalDateTime scrapedAt) {
        this.scrapedAt = scrapedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private Integer durationMs;

    private LocalDateTime scrapedAt = LocalDateTime.now();

    // Getters & setters
}
