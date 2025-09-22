package com.smartShopper.smart_price_backend.controller;

import com.smartShopper.smart_price_backend.dto.product.ProductResponse;
import com.smartShopper.smart_price_backend.dto.wishlist.WishlistDTO;
import com.smartShopper.smart_price_backend.service.WishlistService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    // Add product to wishlist
    @PostMapping("/{userId}/add")
    public WishlistDTO addToWishlist(@PathVariable Long userId, @RequestBody ProductResponse product) {
        return wishlistService.addToWishlist(userId, product);
    }

    // Get all wishlist items for user
    @GetMapping("/{userId}")
    public List<WishlistDTO> getWishlist(@PathVariable Long userId) {
        return wishlistService.getUserWishlist(userId);
    }

    // Remove wishlist item
    @DeleteMapping("/{userId}/remove/{wishlistId}")
    public String removeFromWishlist(@PathVariable Long userId, @PathVariable Long wishlistId) {
        wishlistService.removeFromWishlist(userId, wishlistId);
        return "Wishlist item removed successfully";
    }
}
