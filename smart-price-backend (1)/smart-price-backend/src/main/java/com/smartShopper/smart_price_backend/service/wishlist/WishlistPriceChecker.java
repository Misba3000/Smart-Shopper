package com.smartShopper.smart_price_backend.service.wishlist;

import com.smartShopper.smart_price_backend.entity.Wishlist;
import com.smartShopper.smart_price_backend.repository.WishlistRepository;
import com.smartShopper.smart_price_backend.service.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WishlistPriceChecker {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Check for price drops every 6 hours (at 0:00, 6:00, 12:00, 18:00)
     * For testing: @Scheduled(fixedRate = 60000) // every minute
     */
    @Scheduled(cron = "0 0 */6 * * *")
    @Transactional
    public void checkForPriceDrops() {
        System.out.println("🔍 [" + LocalDateTime.now() + "] Checking wishlist prices for drop alerts...");

        List<Wishlist> alertEnabledWishlists = wishlistRepository.findByAlertEnabledTrue();

        if (alertEnabledWishlists.isEmpty()) {
            System.out.println("ℹ️ No wishlists with alerts enabled found.");
            return;
        }

        System.out.println("📊 Found " + alertEnabledWishlists.size() + " wishlists with alerts enabled");

        int emailsSent = 0;

        for (Wishlist wishlist : alertEnabledWishlists) {
            try {
                // Validate data
                if (wishlist.getProduct() == null) {
                    System.out.println("⚠️ Skipping wishlist " + wishlist.getId() + " - no product");
                    continue;
                }

                if (wishlist.getUser() == null || wishlist.getUser().getEmail() == null) {
                    System.out.println("⚠️ Skipping wishlist " + wishlist.getId() + " - no user/email");
                    continue;
                }

                BigDecimal currentPrice = wishlist.getProduct().getCurrentPrice();
                BigDecimal targetPrice = wishlist.getTargetPrice();

                // Validate prices
                if (currentPrice == null || targetPrice == null) {
                    System.out.println("⚠️ Skipping wishlist " + wishlist.getId() + " - missing price data");
                    continue;
                }

                // Check if price dropped below or equal to target
                if (currentPrice.compareTo(targetPrice) <= 0) {
                    // Check if alert was already sent recently (within 24 hours)
                    if (shouldSendAlert(wishlist)) {
                        emailService.sendPriceDropAlert(wishlist);

                        // Update last alert time
                        wishlist.setLastAlertSentAt(LocalDateTime.now());
                        wishlistRepository.save(wishlist);

                        emailsSent++;

                        System.out.printf("✅ Alert sent for '%s' (₹%s <= ₹%s) to %s%n",
                                wishlist.getProduct().getTitle(),
                                currentPrice,
                                targetPrice,
                                wishlist.getUser().getEmail());
                    } else {
                        System.out.printf("⏭️ Skipping alert for '%s' - already sent recently%n",
                                wishlist.getProduct().getTitle());
                    }
                } else {
                    System.out.printf("ℹ️ No alert for '%s' - Current: ₹%s, Target: ₹%s%n",
                            wishlist.getProduct().getTitle(),
                            currentPrice,
                            targetPrice);
                }
            } catch (Exception e) {
                System.err.println("❌ Error processing wishlist " + wishlist.getId() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("✨ Price check complete. " + emailsSent + " alerts sent.");
    }

    /**
     * Check if we should send an alert for this wishlist
     * Prevents spam by ensuring at least 24 hours between alerts
     */
    private boolean shouldSendAlert(Wishlist wishlist) {
        LocalDateTime lastAlert = wishlist.getLastAlertSentAt();

        // If never sent, send now
        if (lastAlert == null) {
            return true;
        }

        // If last alert was more than 24 hours ago, send again
        return lastAlert.isBefore(LocalDateTime.now().minusHours(24));
    }

    /**
     * Manual trigger for testing
     */
    @Transactional
    public void manualPriceCheck() {
        System.out.println("🔧 Manual price check triggered");
        checkForPriceDrops();
    }
}