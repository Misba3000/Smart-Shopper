//package com.smartShopper.smart_price_backend.entity;
//
//import jakarta.persistence.*;
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(
//        name = "price_history",
//        indexes = {
//                @Index(name = "idx_price_history_product", columnList = "product_id"),
//                @Index(name = "idx_price_history_date", columnList = "recorded_at")
//        }
//)
//public class PriceHistory {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "product_id", nullable = false)
//    private Product product;
//
//    @Column(nullable = false, precision = 10, scale = 2)
//    private BigDecimal price;
//
//    @Column(name = "recorded_at", nullable = false)
//    private LocalDateTime recordedAt;
//
//    // Constructors
//    public PriceHistory() {
//    }
//
//    public PriceHistory(Product product, BigDecimal price, LocalDateTime recordedAt) {
//        this.product = product;
//        this.price = price;
//        this.recordedAt = recordedAt;
//    }
//
//    // Getters and Setters
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public Product getProduct() {
//        return product;
//    }
//
//    public void setProduct(Product product) {
//        this.product = product;
//    }
//
//    public BigDecimal getPrice() {
//        return price;
//    }
//
//    public void setPrice(BigDecimal price) {
//        this.price = price;
//    }
//
//    public LocalDateTime getRecordedAt() {
//        return recordedAt;
//    }
//
//    public void setRecordedAt(LocalDateTime recordedAt) {
//        this.recordedAt = recordedAt;
//    }
//
//    @Override
//    public String toString() {
//        return "PriceHistory{" +
//                "id=" + id +
//                ", price=" + price +
//                ", recordedAt=" + recordedAt +
//                '}';
//    }
//}
package com.smartShopper.smart_price_backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "price_history",
        indexes = {
                @Index(name = "idx_price_history_product", columnList = "product_id"),
                @Index(name = "idx_price_history_date", columnList = "recorded_at"),
                @Index(name = "idx_price_history_product_date", columnList = "product_id, recorded_at")
        }
)
public class PriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "lowest_price", precision = 10, scale = 2)
    private BigDecimal lowestPrice;

    @Column(name = "highest_price", precision = 10, scale = 2)
    private BigDecimal highestPrice;

    @Column(name = "price_change", precision = 10, scale = 2)
    private BigDecimal priceChange; // Change from previous record

    @Column(name = "price_change_percentage", precision = 5, scale = 2)
    private BigDecimal priceChangePercentage;

    @Column(name = "platform", length = 50)
    private String platform;

    @CreationTimestamp
    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    // Constructors
    public PriceHistory() {
    }

    public PriceHistory(Product product, BigDecimal price, String platform) {
        this.product = product;
        this.price = price;
        this.platform = platform;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }

    @Override
    public String toString() {
        return "PriceHistory{" +
                "id=" + id +
                ", price=" + price +
                ", priceChange=" + priceChange +
                ", platform='" + platform + '\'' +
                ", recordedAt=" + recordedAt +
                '}';
    }
}
