package com.smartShopper.smart_price_backend.service.wishlist;

import com.smartShopper.smart_price_backend.dto.product.ProductResponse;
import com.smartShopper.smart_price_backend.dto.wishlist.WishlistDTO;
import com.smartShopper.smart_price_backend.entity.User;
import com.smartShopper.smart_price_backend.entity.Wishlist;
import com.smartShopper.smart_price_backend.repository.UserRepository;
import com.smartShopper.smart_price_backend.repository.WishlistRepository;
import com.smartShopper.smart_price_backend.service.WishlistService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepo;
    private final UserRepository userRepo;

    public WishlistServiceImpl(WishlistRepository wishlistRepo, UserRepository userRepo) {
        this.wishlistRepo = wishlistRepo;
        this.userRepo = userRepo;
    }

    @Override
    public WishlistDTO addToWishlist(Long userId, ProductResponse product) {
        // 1. Get user
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Validate required fields
        if (product.getProductUrl() == null || product.getProductUrl().isEmpty()) {
            throw new RuntimeException("Product URL is required");
        }
        if (product.getTitle() == null || product.getTitle().isEmpty()) {
            throw new RuntimeException("Product title is required");
        }

        // 3. Generate a platformProductId if not available from the product
        String platformProductId = extractPlatformProductId(product.getProductUrl());
        if (platformProductId == null || platformProductId.isEmpty()) {
            // Fallback: generate a unique ID
            platformProductId = "wishlist-" + UUID.randomUUID().toString();
        }

        // 4. Prevent duplicates
        if (wishlistRepo.existsByUserAndPlatformProductId(user, platformProductId)) {
            throw new RuntimeException("Product already in wishlist");
        }

        // 5. Convert price safely
        BigDecimal price = product.getPrice() != null ? product.getPrice() : BigDecimal.ZERO;

        // 6. Create Wishlist entity with platformProductId
        Wishlist wishlist = new Wishlist(
                user,
                product.getTitle(),
                product.getBrand(),
                product.getPlatform(),
                product.getProductUrl(),
                price,
                product.getImageUrl(),
                product.getRating(),
                product.getReviewCount(),
                platformProductId
        );

        // 7. Save to database
        Wishlist saved = wishlistRepo.save(wishlist);

        // 8. Map to DTO and return
        return mapToDTO(saved);
    }

    @Override
    public List<WishlistDTO> getUserWishlist(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return wishlistRepo.findByUser(user)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void removeFromWishlist(Long userId, Long wishlistId) {
        Wishlist wishlist = wishlistRepo.findById(wishlistId)
                .orElseThrow(() -> new RuntimeException("Wishlist item not found"));

        if (!wishlist.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this wishlist item");
        }

        wishlistRepo.delete(wishlist);
    }

    private WishlistDTO mapToDTO(Wishlist wishlist) {
        WishlistDTO dto = new WishlistDTO();
        dto.setId(wishlist.getId());
        dto.setTitle(wishlist.getTitle());
        dto.setBrand(wishlist.getBrand());
        dto.setPlatform(wishlist.getPlatform());
        dto.setProductUrl(wishlist.getProductUrl());
        dto.setPrice(wishlist.getPrice());
        dto.setImageUrl(wishlist.getImageUrl());
        dto.setRating(wishlist.getRating());
        dto.setReviewCount(wishlist.getReviewCount());
        dto.setPlatformProductId(wishlist.getPlatformProductId());
        return dto;
    }

    // Helper method to extract platform product ID from URL
    private String extractPlatformProductId(String productUrl) {
        if (productUrl == null || productUrl.isEmpty()) {
            return null;
        }

        try {
            // Extract product ID based on platform
            if (productUrl.contains("amazon.in")) {
                // Amazon URL pattern: /dp/[productId] or /gp/product/[productId]
                if (productUrl.contains("/dp/")) {
                    String[] parts = productUrl.split("/dp/");
                    if (parts.length > 1) {
                        String productIdPart = parts[1].split("/")[0];
                        return productIdPart.split("\\?")[0]; // Remove query parameters
                    }
                } else if (productUrl.contains("/gp/product/")) {
                    String[] parts = productUrl.split("/gp/product/");
                    if (parts.length > 1) {
                        String productIdPart = parts[1].split("/")[0];
                        return productIdPart.split("\\?")[0]; // Remove query parameters
                    }
                }
            } else if (productUrl.contains("meesho.com")) {
                // Meesho URL pattern: /p/[productName]/[productId]
                if (productUrl.contains("/p/")) {
                    String[] parts = productUrl.split("/p/");
                    if (parts.length > 1) {
                        String[] pathParts = parts[1].split("/");
                        if (pathParts.length > 1) {
                            return pathParts[pathParts.length - 1]; // Last part is product ID
                        }
                    }
                }
            }

            // Fallback: return the URL itself
            return productUrl;
        } catch (Exception e) {
            return productUrl; // Fallback to URL if extraction fails
        }
    }
}