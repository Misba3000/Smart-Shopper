package com.smartShopper.smart_price_backend.service.analytics;

import com.smartShopper.smart_price_backend.dto.analytics.PriceAnalyticsResponse;
import com.smartShopper.smart_price_backend.entity.PriceHistory;
import com.smartShopper.smart_price_backend.entity.Product;
import com.smartShopper.smart_price_backend.entity.Wishlist;
import com.smartShopper.smart_price_backend.repository.PriceHistoryRepository;
import com.smartShopper.smart_price_backend.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PriceTrackingService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private PriceHistoryRepository priceHistoryRepository;

    /**
     * Track prices every 6 hours for products with alerts enabled
     * For testing: @Scheduled(fixedRate = 300000) // every 5 minutes
     */
    @Scheduled(cron = "0 0 */6 * * *")
    @Transactional
    public void trackPricesForAlertedProducts() {
        System.out.println("📊 [" + LocalDateTime.now() + "] Starting price tracking for alerted products...");

        List<Wishlist> alertedWishlists = wishlistRepository.findByAlertEnabledTrue();

        if (alertedWishlists.isEmpty()) {
            System.out.println("ℹ️ No products with alerts enabled to track");
            return;
        }

        System.out.println("📈 Tracking " + alertedWishlists.size() + " products with alerts enabled");

        int trackedCount = 0;

        for (Wishlist wishlist : alertedWishlists) {
            try {
                Product product = wishlist.getProduct();

                if (product == null || product.getCurrentPrice() == null) {
                    System.out.println("⚠️ Skipping wishlist " + wishlist.getId() + " - missing product or price");
                    continue;
                }

                // Get previous price record
                Optional<PriceHistory> previousRecord = priceHistoryRepository
                        .findFirstByProductIdOrderByRecordedAtDesc(product.getId());

                BigDecimal currentPrice = product.getCurrentPrice();
                BigDecimal priceChange = BigDecimal.ZERO;
                BigDecimal priceChangePercentage = BigDecimal.ZERO;

                // Calculate price change if previous record exists
                if (previousRecord.isPresent()) {
                    BigDecimal previousPrice = previousRecord.get().getPrice();
                    priceChange = currentPrice.subtract(previousPrice);

                    if (previousPrice.compareTo(BigDecimal.ZERO) > 0) {
                        priceChangePercentage = priceChange
                                .divide(previousPrice, 4, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100))
                                .setScale(2, RoundingMode.HALF_UP);
                    }
                }

                // Get historical lowest and highest prices
                BigDecimal lowestPrice = priceHistoryRepository
                        .findLowestPriceByProductId(product.getId())
                        .orElse(currentPrice);

                BigDecimal highestPrice = priceHistoryRepository
                        .findHighestPriceByProductId(product.getId())
                        .orElse(currentPrice);

                // Update lowest/highest if current price is better/worse
                if (currentPrice.compareTo(lowestPrice) < 0) {
                    lowestPrice = currentPrice;
                }
                if (currentPrice.compareTo(highestPrice) > 0) {
                    highestPrice = currentPrice;
                }

                // Create new price history record
                PriceHistory priceHistory = new PriceHistory();
                priceHistory.setProduct(product);
                priceHistory.setPrice(currentPrice);
                priceHistory.setPriceChange(priceChange);
                priceHistory.setPriceChangePercentage(priceChangePercentage);
                priceHistory.setLowestPrice(lowestPrice);
                priceHistory.setHighestPrice(highestPrice);
                priceHistory.setPlatform(product.getPlatform());

                priceHistoryRepository.save(priceHistory);

                trackedCount++;

                System.out.printf("✅ Tracked: %s | Price: ₹%s | Change: %s (%.2f%%)%n",
                        product.getTitle(),
                        currentPrice,
                        priceChange.compareTo(BigDecimal.ZERO) >= 0 ? "+" + priceChange : priceChange,
                        priceChangePercentage);

            } catch (Exception e) {
                System.err.println("❌ Error tracking product for wishlist " + wishlist.getId() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("✨ Price tracking complete. Tracked " + trackedCount + " products.");
    }

    /**
     * Get price analytics for a specific user's wishlist
     */
    public List<PriceAnalyticsResponse> getUserPriceAnalytics(Long userId) {
        List<Wishlist> userWishlists = wishlistRepository.findByUserIdAndAlertEnabledTrue(userId);

        return userWishlists.stream()
                .map(this::buildPriceAnalytics)
                .collect(Collectors.toList());
    }

    /**
     * Build price analytics response from wishlist
     */
    private PriceAnalyticsResponse buildPriceAnalytics(Wishlist wishlist) {
        Product product = wishlist.getProduct();
        PriceAnalyticsResponse response = new PriceAnalyticsResponse();

        response.setProductId(product.getId());
        response.setProductTitle(product.getTitle());
        response.setPlatform(product.getPlatform());
        response.setCurrentPrice(product.getCurrentPrice());
        response.setTargetPrice(wishlist.getTargetPrice());
        response.setAlertEnabled(wishlist.isAlertEnabled());

        // Get price statistics
        BigDecimal lowestPrice = priceHistoryRepository
                .findLowestPriceByProductId(product.getId())
                .orElse(product.getCurrentPrice());

        BigDecimal highestPrice = priceHistoryRepository
                .findHighestPriceByProductId(product.getId())
                .orElse(product.getCurrentPrice());

        BigDecimal averagePrice = priceHistoryRepository
                .findAveragePriceByProductId(product.getId())
                .orElse(product.getCurrentPrice());

        response.setLowestPrice(lowestPrice);
        response.setHighestPrice(highestPrice);
        response.setAveragePrice(averagePrice.setScale(2, RoundingMode.HALF_UP));

        // Get latest price change
        Optional<PriceHistory> latestRecord = priceHistoryRepository
                .findFirstByProductIdOrderByRecordedAtDesc(product.getId());

        if (latestRecord.isPresent()) {
            response.setPriceChange(latestRecord.get().getPriceChange());
            response.setPriceChangePercentage(latestRecord.get().getPriceChangePercentage());
            response.setLastTracked(latestRecord.get().getRecordedAt());

            // Determine trend
            BigDecimal changePercentage = latestRecord.get().getPriceChangePercentage();
            if (changePercentage.compareTo(BigDecimal.ZERO) > 0) {
                response.setTrend("up");
            } else if (changePercentage.compareTo(BigDecimal.ZERO) < 0) {
                response.setTrend("down");
            } else {
                response.setTrend("stable");
            }
        } else {
            response.setTrend("stable");
        }

        // Get price history (last 30 days)
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<PriceHistory> priceHistories = priceHistoryRepository
                .findByProductIdAndRecordedAtBetweenOrderByRecordedAtDesc(
                        product.getId(), thirtyDaysAgo, LocalDateTime.now());

        List<PriceAnalyticsResponse.PriceDataPoint> dataPoints = priceHistories.stream()
                .map(ph -> new PriceAnalyticsResponse.PriceDataPoint(ph.getPrice(), ph.getRecordedAt()))
                .collect(Collectors.toList());

        response.setPriceHistory(dataPoints);

        return response;
    }

    /**
     * Manual trigger for testing
     */
    public void manualTrackPrices() {
        System.out.println("🔧 Manual price tracking triggered");
        trackPricesForAlertedProducts();
    }
}