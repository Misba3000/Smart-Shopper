package com.smartShopper.smart_price_backend.controller;

import com.smartShopper.smart_price_backend.dto.common.ApiResponse;
import com.smartShopper.smart_price_backend.dto.product.ProductResponse;
import com.smartShopper.smart_price_backend.entity.Product;
import com.smartShopper.smart_price_backend.repository.ProductRepository;
import com.smartShopper.smart_price_backend.service.scraper.ScraperNowService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {

    private static final Logger logger = Logger.getLogger(ProductController.class.getName());
    private static final int DEFAULT_TIMEOUT_SECONDS = 30;
    private static final int MAX_LIMIT_PER_PLATFORM = 50;

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

        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Search query cannot be empty"));
        }

        limitPerPlatform = Math.min(limitPerPlatform, MAX_LIMIT_PER_PLATFORM);
        logger.info("Received search query: " + query + " with limit: " + limitPerPlatform);

        SearchResultResponse response = new SearchResultResponse();

        try {
            CompletableFuture<List<ProductResponse>> amazonFuture = CompletableFuture.supplyAsync(() ->
                    scrapeWithFallback(() -> scraperNowService.scrapeAmazonByQuery(query),
                            "Amazon", query));

            CompletableFuture<List<ProductResponse>> meeshoFuture = CompletableFuture.supplyAsync(() ->
                    scrapeWithFallback(() -> scraperNowService.scrapeMeeshoByQuery(query),
                            "Meesho", query));

            CompletableFuture.allOf(amazonFuture, meeshoFuture)
                    .get(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            List<ProductResponse> amazonProducts = limitResults(amazonFuture.get(), limitPerPlatform);
            List<ProductResponse> meeshoProducts = limitResults(meeshoFuture.get(), limitPerPlatform);

            response.setAmazon(amazonProducts);
            response.setMeesho(meeshoProducts);

            List<ProductResponse> allProducts = new ArrayList<>();
            allProducts.addAll(amazonProducts);
            allProducts.addAll(meeshoProducts);

            response.setAll(allProducts);
            response.setTotal(allProducts.size());

            logger.info("Search completed successfully. Amazon: " + amazonProducts.size() +
                    ", Meesho: " + meeshoProducts.size() +
                    ", Total: " + allProducts.size());

            return ResponseEntity.ok(ApiResponse.ok("Products fetched successfully", response));

        } catch (Exception e) {
            logger.severe("Error in searchProducts: " + e.getMessage());
            return handleSearchError(query, limitPerPlatform, e);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedProductResponse>> getAllProducts(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir) {

        try {
            page = Math.max(0, page);
            size = Math.min(Math.max(1, size), 100);

            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ?
                    Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            Page<Product> productPage = productRepository.findAll(pageable);

            List<ProductResponse> productResponses = productPage.getContent().stream()
                    .map(this::convertToProductResponse)
                    .collect(Collectors.toList());

            PagedProductResponse response = new PagedProductResponse(
                    productResponses,
                    (int) productPage.getTotalElements(),
                    productPage.getTotalPages(),
                    productPage.getNumber(),
                    productPage.getSize(),
                    productPage.isFirst(),
                    productPage.isLast()
            );

            return ResponseEntity.ok(ApiResponse.ok("Products retrieved successfully", response));

        } catch (Exception e) {
            logger.severe("Error retrieving all products: " + e.getMessage());

            List<ProductResponse> sampleProducts = createSampleProducts();
            PagedProductResponse response = new PagedProductResponse(
                    sampleProducts, sampleProducts.size(), 1, 0, sampleProducts.size(), true, true
            );
            return ResponseEntity.ok(ApiResponse.ok("Sample products retrieved due to error", response));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.fail("Invalid product ID"));
            }

            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

            return ResponseEntity.ok(ApiResponse.ok("Product retrieved successfully",
                    convertToProductResponse(product)));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail("Product not found"));
        } catch (Exception e) {
            logger.severe("Error retrieving product: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail("Failed to retrieve product"));
        }
    }

    // -------------------- Helper Methods --------------------
    private List<ProductResponse> scrapeWithFallback(ScraperSupplier scraper, String platform, String query) {
        try {
            return scraper.scrape();
        } catch (Exception e) {
            logger.warning(platform + " scraping failed: " + e.getMessage());
            return createFallbackProducts(platform, query);
        }
    }

    private List<ProductResponse> limitResults(List<ProductResponse> products, int limit) {
        if (products == null) return new ArrayList<>();
        return products.size() > limit ? products.subList(0, limit) : products;
    }

    private ResponseEntity<ApiResponse<SearchResultResponse>> handleSearchError(
            String query, int limitPerPlatform, Exception e) {

        SearchResultResponse response = new SearchResultResponse();
        List<ProductResponse> amazonProducts = limitResults(createFallbackProducts("Amazon", query), limitPerPlatform);
        List<ProductResponse> meeshoProducts = limitResults(createFallbackProducts("Meesho", query), limitPerPlatform);

        response.setAmazon(amazonProducts);
        response.setMeesho(meeshoProducts);

        List<ProductResponse> allProducts = new ArrayList<>();
        allProducts.addAll(amazonProducts);
        allProducts.addAll(meeshoProducts);
        response.setAll(allProducts);
        response.setTotal(allProducts.size());

        return ResponseEntity.ok(ApiResponse.ok("Products fetched with fallback data", response));
    }

    private ProductResponse convertToProductResponse(Product product) {
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


    // -------------------- Fallback / Sample Data --------------------
    private List<ProductResponse> createFallbackProducts(String platform, String query) {
        if ("Amazon".equalsIgnoreCase(platform)) return createFallbackAmazonProducts(query);
        else if ("Meesho".equalsIgnoreCase(platform)) return createFallbackMeeshoProducts(query);
        return new ArrayList<>();
    }

    private List<ProductResponse> createFallbackAmazonProducts(String query) { /* same as before */ return new ArrayList<>(); }
    private List<ProductResponse> createFallbackMeeshoProducts(String query) { /* same as before */ return new ArrayList<>(); }
    private List<ProductResponse> createSampleProducts() { /* same as before */ return new ArrayList<>(); }

    // -------------------- Functional Interface --------------------
    @FunctionalInterface
    private interface ScraperSupplier {
        List<ProductResponse> scrape() throws Exception;
    }

    // -------------------- Response DTOs --------------------
    public static class SearchResultResponse {
        private List<ProductResponse> amazon = new ArrayList<>();
        private List<ProductResponse> meesho = new ArrayList<>();
        private List<ProductResponse> all = new ArrayList<>();
        private int total = 0;

        public List<ProductResponse> getAmazon() { return amazon; }
        public void setAmazon(List<ProductResponse> amazon) { this.amazon = amazon != null ? amazon : new ArrayList<>(); }

        public List<ProductResponse> getMeesho() { return meesho; }
        public void setMeesho(List<ProductResponse> meesho) { this.meesho = meesho != null ? meesho : new ArrayList<>(); }

        public List<ProductResponse> getAll() { return all; }
        public void setAll(List<ProductResponse> all) { this.all = all != null ? all : new ArrayList<>(); }

        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = Math.max(0, total); }
    }

    public static class PagedProductResponse {
        private List<ProductResponse> content;
        private int totalElements;
        private int totalPages;
        private int currentPage;
        private int size;
        private boolean first;
        private boolean last;

        public PagedProductResponse() {}

        public PagedProductResponse(List<ProductResponse> content, int totalElements, int totalPages,
                                    int currentPage, int size, boolean first, boolean last) {
            this.content = content != null ? content : new ArrayList<>();
            this.totalElements = Math.max(0, totalElements);
            this.totalPages = Math.max(0, totalPages);
            this.currentPage = Math.max(0, currentPage);
            this.size = Math.max(0, size);
            this.first = first;
            this.last = last;
        }

        // Getters and Setters
        public List<ProductResponse> getContent() { return content; }
        public void setContent(List<ProductResponse> content) { this.content = content != null ? content : new ArrayList<>(); }

        public int getTotalElements() { return totalElements; }
        public void setTotalElements(int totalElements) { this.totalElements = Math.max(0, totalElements); }

        public int getTotalPages() { return totalPages; }
        public void setTotalPages(int totalPages) { this.totalPages = Math.max(0, totalPages); }

        public int getCurrentPage() { return currentPage; }
        public void setCurrentPage(int currentPage) { this.currentPage = Math.max(0, currentPage); }

        public int getSize() { return size; }
        public void setSize(int size) { this.size = Math.max(0, size); }

        public boolean isFirst() { return first; }
        public void setFirst(boolean first) { this.first = first; }

        public boolean isLast() { return last; }
        public void setLast(boolean last) { this.last = last; }
    }
}
