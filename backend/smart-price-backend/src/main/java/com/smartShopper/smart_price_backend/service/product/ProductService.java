//package com.smartShopper.smart_price_backend.service.product;
//
//
////import com.smartShopper.smart_price_backend.dto.product.ProductRequest;
//import com.smartShopper.smart_price_backend.dto.product.ProductResponse;
//import com.smartShopper.smart_price_backend.entity.PriceHistory;
//import com.smartShopper.smart_price_backend.entity.Product;
//import com.smartShopper.smart_price_backend.repository.PriceHistoryRepository;
//import com.smartShopper.smart_price_backend.repository.ProductRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//@Service
//public class ProductService {
//
//    @Autowired
//    private ProductRepository productRepository;
//
////    @Autowired
////    private PriceHistoryRepository priceHistoryRepository;
////
////    // Save or update product
////    public Product saveProduct(Product product) {
////
////        // Check for existing product by title + platform
////        Optional<Product> existingOpt = productRepository.findByTitleAndPlatform(
////                product.getTitle(), product.getPlatform()
////        );
////
////        Product savedProduct;
////        if (existingOpt.isPresent()) {
////            Product existing = existingOpt.get();
////
////            // Update price and other fields
////            existing.setCurrentPrice(product.getCurrentPrice());
////            existing.setImageUrl(product.getImageUrl());
////            existing.setDescription(product.getDescription());
////            existing.setRating(product.getRating());
////            existing.setReviewCount(product.getReviewCount());
////
////            savedProduct = productRepository.save(existing);
////        } else {
////            savedProduct = productRepository.save(product);
////        }
////
////        // Save price history
////        PriceHistory history = new PriceHistory();
////        history.setProduct(savedProduct);
////        history.setPrice(savedProduct.getCurrentPrice());
////        history.setRecordedAt(LocalDateTime.now());
////        priceHistoryRepository.save(history);
////
////        return savedProduct;
////    }
//@Autowired
//private PriceHistoryRepository priceHistoryRepository;
//
//    public Product saveProduct(Product product) {
//        // Validate unique field first
////        if (product.getPlatformProductUrl() == null || product.getPlatformProductUrl().trim().isEmpty()) {
////            throw new IllegalArgumentException("Product URL cannot be null or empty");
////        }
//
//        // Check if product already exists (based on URL — your unique column)
//        Optional<Product> existingOpt = productRepository.findByPlatformProductUrl(
//                product.getPlatformProductUrl()
//        );
//
//        Product savedProduct;
//
//        if (existingOpt.isPresent()) {
//            Product existing = existingOpt.get();
//
//            // Update product details
//            existing.setTitle(product.getTitle());
//            existing.setBrand(product.getBrand());
//            existing.setPlatform(product.getPlatform());
//            existing.setCurrentPrice(product.getCurrentPrice());
//            existing.setImageUrl(product.getImageUrl());
//            existing.setDescription(product.getDescription());
//            existing.setRating(product.getRating());
//            existing.setReviewCount(product.getReviewCount());
//
//            savedProduct = productRepository.save(existing);
//        } else {
//            // New product → save directly
//            savedProduct = productRepository.save(product);
//        }
//
//        // Always record price history
//        PriceHistory history = new PriceHistory();
//        history.setProduct(savedProduct);
//        history.setPrice(savedProduct.getCurrentPrice());
//        history.setRecordedAt(LocalDateTime.now());
//        priceHistoryRepository.save(history);
//
//        return savedProduct;
//    }
//
//    // Get product by ID
//    public ProductResponse getProductById(Long productId) {
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new RuntimeException("Product not found"));
//        return convertToResponse(product);
//    }
//
//    // Convert entity to DTO
//    public ProductResponse convertToResponse(Product product) {
//        ProductResponse response = new ProductResponse();
//        response.setId(product.getId());
//        response.setTitle(product.getTitle());
//        response.setBrand(product.getBrand());
//        response.setPlatform(product.getPlatform());
//        response.setProductUrl(product.getPlatformProductUrl());
//        response.setPrice(product.getCurrentPrice());
//        response.setRating(product.getRating());
//        response.setReviewCount(product.getReviewCount());
//        response.setImageUrl(product.getImageUrl());
//        response.setDescription(product.getDescription());
//        response.setCreatedAt(product.getCreatedAt());
//        response.setUpdatedAt(product.getUpdatedAt());
//        return response;
//    }
//
//    public Optional<Product> findByPlatformProductUrl(String url) {
//        return productRepository.findByPlatformProductUrl(url);
//    }
//
//}

package com.smartShopper.smart_price_backend.service.product;

import com.smartShopper.smart_price_backend.dto.product.ProductResponse;
import com.smartShopper.smart_price_backend.entity.PriceHistory;
import com.smartShopper.smart_price_backend.entity.Product;
import com.smartShopper.smart_price_backend.repository.PriceHistoryRepository;
import com.smartShopper.smart_price_backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PriceHistoryRepository priceHistoryRepository;

    @Transactional
    public Product saveProduct(Product product) {
        try {
            // Validate required fields
            if (product.getPlatformProductUrl() == null || product.getPlatformProductUrl().trim().isEmpty()) {
                throw new IllegalArgumentException("Product URL cannot be null or empty");
            }

            if (product.getTitle() == null || product.getTitle().trim().isEmpty()) {
                throw new IllegalArgumentException("Product title cannot be null or empty");
            }

            if (product.getCurrentPrice() == null || product.getCurrentPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Product price must be positive");
            }

            // Check if product already exists by URL
            Optional<Product> existingOpt = productRepository.findByPlatformProductUrl(
                    product.getPlatformProductUrl()
            );

            Product savedProduct;

            if (existingOpt.isPresent()) {
                Product existing = existingOpt.get();

                // Only save price history if price changed
                boolean priceChanged = existing.getCurrentPrice() == null ||
                        existing.getCurrentPrice().compareTo(product.getCurrentPrice()) != 0;

                // Update product details
                existing.setTitle(product.getTitle());
                existing.setBrand(product.getBrand());
                existing.setPlatform(product.getPlatform());
                existing.setCurrentPrice(product.getCurrentPrice());
                existing.setImageUrl(product.getImageUrl());
                existing.setDescription(product.getDescription());
                existing.setRating(product.getRating());
                existing.setReviewCount(product.getReviewCount());
                existing.setLastScraped(LocalDateTime.now());
                existing.setCategory(product.getCategory());

                savedProduct = productRepository.save(existing);

                // Record price history only if price changed
                if (priceChanged) {
                    recordPriceHistory(savedProduct);
                }

                System.out.println("✅ Updated existing product: " + savedProduct.getTitle());
            } else {
                // New product - save directly
                product.setLastScraped(LocalDateTime.now());
                savedProduct = productRepository.save(product);

                // Record initial price
                recordPriceHistory(savedProduct);

                System.out.println("✅ Saved new product: " + savedProduct.getTitle());
            }

            return savedProduct;

        } catch (Exception e) {
            System.err.println("❌ Error saving product: " + e.getMessage());
            throw e;
        }
    }

    private void recordPriceHistory(Product product) {
        try {
            PriceHistory history = new PriceHistory();
            history.setProduct(product);
            history.setPrice(product.getCurrentPrice());
            history.setRecordedAt(LocalDateTime.now());
            priceHistoryRepository.save(history);
        } catch (Exception e) {
            System.err.println("⚠️ Failed to record price history: " + e.getMessage());
            // Don't throw - price history is optional
        }
    }

    public ProductResponse getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        return convertToResponse(product);
    }

    public ProductResponse convertToResponse(Product product) {
        return new ProductResponse(
                product.getTitle(),
                product.getBrand(),
                product.getCreatedAt(),
                product.getDescription(),
                product.getId(),
                product.getImageUrl(),
                product.getPlatform(),
                product.getCurrentPrice(),
                product.getPlatformProductUrl(),
                product.getRating(),
                product.getReviewCount(),
                product.getUpdatedAt()
        );
    }

    public Optional<Product> findByPlatformProductUrl(String url) {
        return productRepository.findByPlatformProductUrl(url);
    }

    public void updateProductPrice(Long productId, BigDecimal newPrice) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getCurrentPrice().compareTo(newPrice) != 0) {
            product.setCurrentPrice(newPrice);
            product.setLastScraped(LocalDateTime.now());
            productRepository.save(product);

            // Record price change
            recordPriceHistory(product);
        }
    }
}