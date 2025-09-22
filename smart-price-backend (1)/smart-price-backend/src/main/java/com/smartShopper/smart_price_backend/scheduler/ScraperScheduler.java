package com.smartShopper.smart_price_backend.scheduler;

import com.smartShopper.smart_price_backend.entity.Product;
import com.smartShopper.smart_price_backend.repository.ProductRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScraperScheduler {

    private final ProductRepository productRepository;

    public ScraperScheduler(ProductRepository productRepository
                            ) {
        this.productRepository = productRepository;
    }

//    // 🔄 Run every 6 hours
//    @Scheduled(fixedRate = 21600000)
//    public void updatePrices() {
//        List<Product> products = productRepository.findAll();
//        for (Product p : products) {
//            try {
//                scraperManagerService.scrapeAndSave(p.getPlatformProductUrl());
//                System.out.println("✅ Updated: " + p.getTitle());
//            } catch (Exception e) {
//                System.out.println(" Failed to scrape: " + p.getPlatformProductUrl());
//            }
//        }
//    }
}
