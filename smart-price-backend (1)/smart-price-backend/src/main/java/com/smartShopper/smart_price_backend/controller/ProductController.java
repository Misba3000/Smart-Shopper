package com.smartShopper.smart_price_backend.controller;

import com.smartShopper.smart_price_backend.dto.common.ApiResponse;
import com.smartShopper.smart_price_backend.dto.product.ProductResponse;
import com.smartShopper.smart_price_backend.entity.Product;
import com.smartShopper.smart_price_backend.repository.ProductRepository;
import com.smartShopper.smart_price_backend.service.scraper.ScraperNowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger logger = Logger.getLogger(ProductController.class.getName());

    private final ProductRepository productRepository;
    private final ScraperNowService scraperNowService;

    public ProductController(ProductRepository productRepository,
                             ScraperNowService scraperNowService) {
        this.productRepository = productRepository;
        this.scraperNowService = scraperNowService;
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<SearchResultResponse>> searchProducts(
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "10") int limitPerPlatform) {

        logger.info("Received search query: " + query + " with limit: " + limitPerPlatform);

        SearchResultResponse response = new SearchResultResponse();
        List<ProductResponse> allProducts = new ArrayList<>();

        try {
            // Use CompletableFuture to run scrapers in parallel with timeout
            CompletableFuture<List<ProductResponse>> amazonFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return scraperNowService.scrapeAmazonByQuery(query);
                } catch (Exception e) {
                    logger.warning("Amazon scraping failed: " + e.getMessage());
                    return createFallbackAmazonProducts(query);
                }
            });

            CompletableFuture<List<ProductResponse>> meeshoFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return scraperNowService.scrapeMeeshoByQuery(query);
                } catch (Exception e) {
                    logger.warning("Meesho scraping failed: " + e.getMessage());
                    return createFallbackMeeshoProducts(query);
                }
            });

            // Wait for both futures to complete with a timeout
            CompletableFuture.allOf(amazonFuture, meeshoFuture)
                    .get(30, TimeUnit.SECONDS); // 30 seconds timeout

            List<ProductResponse> amazonProducts = amazonFuture.get();
            List<ProductResponse> meeshoProducts = meeshoFuture.get();

            // Apply limit per platform
            if (amazonProducts.size() > limitPerPlatform) {
                amazonProducts = amazonProducts.subList(0, limitPerPlatform);
            }

            if (meeshoProducts.size() > limitPerPlatform) {
                meeshoProducts = meeshoProducts.subList(0, limitPerPlatform);
            }

            response.setAmazon(amazonProducts);
            response.setMeesho(meeshoProducts);

            allProducts.addAll(amazonProducts);
            allProducts.addAll(meeshoProducts);
            response.setAll(allProducts);
            response.setTotal(allProducts.size());

            logger.info("Scraping completed. Amazon: " + amazonProducts.size() +
                    ", Meesho: " + meeshoProducts.size() +
                    ", Total: " + allProducts.size());

            return ResponseEntity.ok(ApiResponse.ok("Products fetched successfully", response));

        } catch (Exception e) {
            logger.severe("Error in searchProducts: " + e.getMessage());

            // Return fallback data on error
            List<ProductResponse> amazonProducts = createFallbackAmazonProducts(query);
            List<ProductResponse> meeshoProducts = createFallbackMeeshoProducts(query);

            // Apply limit per platform to fallback data too
            if (amazonProducts.size() > limitPerPlatform) {
                amazonProducts = amazonProducts.subList(0, limitPerPlatform);
            }

            if (meeshoProducts.size() > limitPerPlatform) {
                meeshoProducts = meeshoProducts.subList(0, limitPerPlatform);
            }

            response.setAmazon(amazonProducts);
            response.setMeesho(meeshoProducts);

            allProducts.addAll(amazonProducts);
            allProducts.addAll(meeshoProducts);
            response.setAll(allProducts);
            response.setTotal(allProducts.size());

            return ResponseEntity.ok(ApiResponse.ok("Products fetched with fallback data", response));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
        try {
            List<Product> products = productRepository.findAll();

            if (products.isEmpty()) {
                // Return sample data if database is empty
                List<ProductResponse> sampleProducts = createSampleProducts();
                return ResponseEntity.ok(ApiResponse.ok("Sample products retrieved", sampleProducts));
            }

            List<ProductResponse> productResponses = products.stream()
                    .map(this::convertToProductResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.ok("All products retrieved", productResponses));

        } catch (Exception e) {
            logger.severe("Error retrieving all products: " + e.getMessage());
            e.printStackTrace();

            // Return sample data on error
            List<ProductResponse> sampleProducts = createSampleProducts();
            return ResponseEntity.ok(ApiResponse.ok("Sample products retrieved due to error", sampleProducts));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(@PathVariable Long id) {
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

            ProductResponse response = convertToProductResponse(product);
            return ResponseEntity.ok(ApiResponse.ok("Product retrieved", response));

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.severe("Error retrieving product: " + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.fail("Failed to retrieve product: " + e.getMessage()));
        }
    }

    // Helper method to convert Product entity to ProductResponse
    private ProductResponse convertToProductResponse(Product product) {
        return new ProductResponse(
                product.getTitle(),
                product.getBrand(),
                product.getPlatform(),
                product.getPlatformProductUrl(),
                product.getCurrentPrice(),
                product.getImageUrl(),
                product.getPlatform(),
                product.getRating(),
                product.getReviewCount(),
                product.getDescription()
        );
    }

    // Fallback data methods (only used when scraping fails)
    private List<ProductResponse> createFallbackProducts(String query) {
        List<ProductResponse> products = new ArrayList<>();
        products.addAll(createFallbackAmazonProducts(query));
        products.addAll(createFallbackMeeshoProducts(query));
        return products;
    }

    private List<ProductResponse> createFallbackAmazonProducts(String query) {
        List<ProductResponse> products = new ArrayList<>();

        products.add(new ProductResponse(
                "Amazon " + query + " - Premium Quality",
                "Generic Brand",
                "Amazon",
                "https://amazon.in/dp/sample1",
                new BigDecimal("999.00"),
                "https://via.placeholder.com/300x300?text=Amazon+" + query.replace(" ", "+"),
                "Amazon",
                4.2,
                150,
                "Sample product from Amazon for " + query
        ));

        products.add(new ProductResponse(
                "Amazon " + query + " - Best Seller",
                "Brand X",
                "Amazon",
                "https://amazon.in/dp/sample2",
                new BigDecimal("1499.00"),
                "https://via.placeholder.com/300x300?text=Amazon+" + query.replace(" ", "+") + "+2",
                "Amazon",
                4.5,
                89,
                "Another sample product from Amazon"
        ));

        return products;
    }

    private List<ProductResponse> createFallbackMeeshoProducts(String query) {
        List<ProductResponse> products = new ArrayList<>();

        products.add(new ProductResponse(
                "Meesho " + query + " - Trendy Style",
                "Meesho Brand",
                "Meesho",
                "https://meesho.com/p/sample1",
                new BigDecimal("499.00"),
                "https://via.placeholder.com/300x300?text=Meesho+" + query.replace(" ", "+"),
                "Meesho",
                4.0,
                45,
                "Sample product from Meesho for " + query
        ));

        products.add(new ProductResponse(
                "Meesho " + query + " - Affordable Option",
                "Local Brand",
                "Meesho",
                "https://meesho.com/p/sample2",
                new BigDecimal("299.00"),
                "https://via.placeholder.com/300x300?text=Meesho+" + query.replace(" ", "+") + "+2",
                "Meesho",
                3.8,
                32,
                "Another sample product from Meesho"
        ));

        return products;
    }

    private List<ProductResponse> createSampleProducts() {
        List<ProductResponse> products = new ArrayList<>();

        products.add(new ProductResponse(
                "Sample Smartphone",
                "TechBrand",
                "Amazon",
                "https://amazon.in/dp/sample-phone",
                new BigDecimal("15999.00"),
                "https://via.placeholder.com/300x300?text=Smartphone",
                "Amazon",
                4.3,
                234,
                "Latest smartphone with great features"
        ));

        products.add(new ProductResponse(
                "Cotton T-Shirt",
                "FashionBrand",
                "Meesho",
                "https://meesho.com/p/sample-tshirt",
                new BigDecimal("399.00"),
                "https://via.placeholder.com/300x300?text=T-Shirt",
                "Meesho",
                4.1,
                67,
                "Comfortable cotton t-shirt"
        ));

        products.add(new ProductResponse(
                "Wireless Headphones",
                "AudioBrand",
                "Amazon",
                "https://amazon.in/dp/sample-headphones",
                new BigDecimal("2999.00"),
                "https://via.placeholder.com/300x300?text=Headphones",
                "Amazon",
                4.4,
                156,
                "High quality wireless headphones"
        ));

        return products;
    }

    // Inner class for search response
    public static class SearchResultResponse {
        private List<ProductResponse> amazon;
        private List<ProductResponse> meesho;
        private List<ProductResponse> all;
        private int total;

        public SearchResultResponse() {
            this.amazon = new ArrayList<>();
            this.meesho = new ArrayList<>();
            this.all = new ArrayList<>();
        }

        // Getters and Setters
        public List<ProductResponse> getAmazon() { return amazon; }
        public void setAmazon(List<ProductResponse> amazon) { this.amazon = amazon; }

        public List<ProductResponse> getMeesho() { return meesho; }
        public void setMeesho(List<ProductResponse> meesho) { this.meesho = meesho; }

        public List<ProductResponse> getAll() { return all; }
        public void setAll(List<ProductResponse> all) { this.all = all; }

        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }
    }
}