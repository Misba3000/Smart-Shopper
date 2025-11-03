package com.smartShopper.smart_price_backend.service.wishlist;

import com.smartShopper.smart_price_backend.dto.Wishlist.AddToWishlistRequest;
import com.smartShopper.smart_price_backend.dto.Wishlist.WishlistResponse;
import com.smartShopper.smart_price_backend.entity.Product;
import com.smartShopper.smart_price_backend.entity.User;
import com.smartShopper.smart_price_backend.entity.Wishlist;
import com.smartShopper.smart_price_backend.repository.WishlistRepository;
import com.smartShopper.smart_price_backend.service.product.ProductService;
import com.smartShopper.smart_price_backend.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishlistServiceImpl {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Transactional
    public WishlistResponse addToWishlist(Long userId, AddToWishlistRequest request) {
        User user = userService.getUserById(userId);

        // Check if product already exists by its unique URL
        Product product = productService.findByPlatformProductUrl(request.getProductUrl())
                .orElseGet(() -> {
                    Product newProduct = new Product();
                    newProduct.setTitle(request.getTitle());
                    newProduct.setBrand(request.getBrand());
                    newProduct.setPlatform(request.getPlatform());
                    newProduct.setPlatformProductUrl(request.getProductUrl());
                    newProduct.setCurrentPrice(
                            request.getCurrentPrice() != null ? request.getCurrentPrice() : request.getPrice());
                    newProduct.setRating(request.getRating());
                    newProduct.setReviewCount(request.getReviewCount());
                    newProduct.setImageUrl(request.getImageUrl());
                    newProduct.setDescription(request.getDescription());
                    return productService.saveProduct(newProduct);
                });

        // Check if the wishlist already contains this product for the user
        if (wishlistRepository.existsByUserIdAndProductId(userId, product.getId())) {
            throw new RuntimeException("Product already in wishlist");
        }

        // Create wishlist entry
        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setProduct(product);
        wishlist.setTargetPrice(request.getTargetPrice());
        wishlist.setAlertEnabled(request.isAlertEnabled());

        Wishlist savedWishlist = wishlistRepository.save(wishlist);

        System.out.println("✅ Wishlist created - ID: " + savedWishlist.getId() +
                ", Alert: " + savedWishlist.isAlertEnabled() +
                ", Target: " + savedWishlist.getTargetPrice());

        return convertToResponse(savedWishlist);
    }

    public WishlistResponse getWishlistItem(Long wishlistId) {
        Wishlist wishlist = wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new RuntimeException("Wishlist item not found"));
        return convertToResponse(wishlist);
    }

    public List<WishlistResponse> getUserWishlist(Long userId) {
        return wishlistRepository.findByUserId(userId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public WishlistResponse updateWishlist(Long wishlistId, BigDecimal targetPrice, Boolean alertEnabled) {
        Wishlist wishlist = wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new RuntimeException("Wishlist item not found"));

        // Update fields
        if (targetPrice != null) {
            wishlist.setTargetPrice(targetPrice);
        }
        if (alertEnabled != null) {
            wishlist.setAlertEnabled(alertEnabled);
        }

        // Save to database - this ensures persistence
        Wishlist updatedWishlist = wishlistRepository.save(wishlist);

        // Force flush to ensure database update
        wishlistRepository.flush();

        System.out.println("✅ Wishlist updated in DB - ID: " + updatedWishlist.getId() +
                ", Alert: " + updatedWishlist.isAlertEnabled() +
                ", Target: " + updatedWishlist.getTargetPrice());

        return convertToResponse(updatedWishlist);
    }

    @Transactional
    public void deleteWishlist(Long wishlistId) {
        Wishlist wishlist = wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new RuntimeException("Wishlist item not found"));
        wishlistRepository.delete(wishlist);
    }

    // Helper method to convert entity to response
    private WishlistResponse convertToResponse(Wishlist wishlist) {
        WishlistResponse response = new WishlistResponse();
        response.setId(wishlist.getId());
        response.setProduct(productService.convertToResponse(wishlist.getProduct()));
        response.setTargetPrice(wishlist.getTargetPrice());
        response.setAlertEnabled(wishlist.isAlertEnabled());
        response.setAddedAt(wishlist.getCreatedAt());
        response.setLastAlertSentAt(wishlist.getLastAlertSentAt());
        return response;
    }
}