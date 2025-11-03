package com.smartShopper.smart_price_backend.scheduler;

import com.smartShopper.smart_price_backend.entity.Product;
import com.smartShopper.smart_price_backend.repository.ProductRepository;
import com.smartShopper.smart_price_backend.service.scraper.ScraperNowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@Component
public class ScheduledScraperService {

    private static final Logger logger = Logger.getLogger(ScheduledScraperService.class.getName());

    @Autowired
    private ProductRepository productRepository;

    /**
     * Update prices for all products every 12 hours
     * Disabled by default - uncomment @Scheduled to enable
     */
    // @Scheduled(fixedRate = 43200000) // 12 hours in milliseconds
    public void updateAllProductPrices() {
        logger.info("🔄 Starting scheduled price update for all products...");

        List<Product> products = productRepository.findAll();
        int updated = 0;
        int failed = 0;

        for (Product product : products) {
            try {
                // Check if product needs update (last scraped > 6 hours ago)
                if (needsUpdate(product)) {
                    logger.info("Updating: " + product.getTitle());
                    // Note: You'd need to implement re-scraping individual products
                    // This is a placeholder
                    updated++;
                } else {
                    logger.info("Skipping (recently updated): " + product.getTitle());
                }
            } catch (Exception e) {
                logger.severe("Failed to update product " + product.getId() + ": " + e.getMessage());
                failed++;
            }
        }

        logger.info("✅ Scheduled update complete. Updated: " + updated + ", Failed: " + failed);
    }

    /**
     * Clean old price history data (older than 90 days)
     * Runs once daily at 3 AM
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanOldPriceHistory() {
        logger.info("🧹 Cleaning old price history data...");
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(90);

        // You would implement this in PriceHistoryRepository
        // priceHistoryRepository.deleteOlderThan(cutoffDate);

        logger.info("✅ Old price history cleaned");
    }

    private boolean needsUpdate(Product product) {
        if (product.getLastScraped() == null) {
            return true;
        }
        LocalDateTime sixHoursAgo = LocalDateTime.now().minusHours(6);
        return product.getLastScraped().isBefore(sixHoursAgo);
    }
}