package com.smartShopper.smart_price_backend.repository;

import com.smartShopper.smart_price_backend.entity.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {

    // Get all price history for a product
    List<PriceHistory> findByProductIdOrderByRecordedAtDesc(Long productId);

    // Get price history within date range
    List<PriceHistory> findByProductIdAndRecordedAtBetweenOrderByRecordedAtDesc(
            Long productId, LocalDateTime startDate, LocalDateTime endDate);

    // Get latest price record for a product
    Optional<PriceHistory> findFirstByProductIdOrderByRecordedAtDesc(Long productId);

    // Get lowest price for a product
    @Query("SELECT MIN(ph.price) FROM PriceHistory ph WHERE ph.product.id = :productId")
    Optional<BigDecimal> findLowestPriceByProductId(@Param("productId") Long productId);

    // Get highest price for a product
    @Query("SELECT MAX(ph.price) FROM PriceHistory ph WHERE ph.product.id = :productId")
    Optional<BigDecimal> findHighestPriceByProductId(@Param("productId") Long productId);

    // Get average price for a product
    @Query("SELECT AVG(ph.price) FROM PriceHistory ph WHERE ph.product.id = :productId")
    Optional<BigDecimal> findAveragePriceByProductId(@Param("productId") Long productId);

    // Count price records for a product
    long countByProductId(Long productId);

    // Delete old records (for cleanup)
    void deleteByRecordedAtBefore(LocalDateTime date);
}