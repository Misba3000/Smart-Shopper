package com.smartShopper.smart_price_backend.controller;

import com.smartShopper.smart_price_backend.dto.Wishlist.AddToWishlistRequest;
import com.smartShopper.smart_price_backend.dto.Wishlist.WishlistResponse;
import com.smartShopper.smart_price_backend.service.wishlist.WishlistPriceChecker;
import com.smartShopper.smart_price_backend.service.wishlist.WishlistServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/wishlist")
public class WishlistController {

    @Autowired
    private WishlistServiceImpl wishlistService;

    @Autowired
    private WishlistPriceChecker priceChecker;

    @PostMapping("/{userId}")
    public ResponseEntity<WishlistResponse> addToWishlist(
            @PathVariable Long userId,
            @RequestBody AddToWishlistRequest request
    ) {
        try {
            WishlistResponse response = wishlistService.addToWishlist(userId, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<WishlistResponse>> getUserWishlist(@PathVariable Long userId) {
        List<WishlistResponse> wishlists = wishlistService.getUserWishlist(userId);
        return ResponseEntity.ok(wishlists);
    }

    @GetMapping("/{wishlistId}")
    public ResponseEntity<WishlistResponse> getWishlistItem(@PathVariable Long wishlistId) {
        try {
            WishlistResponse response = wishlistService.getWishlistItem(wishlistId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{wishlistId}")
    public ResponseEntity<WishlistResponse> updateWishlist(
            @PathVariable Long wishlistId,
            @RequestParam(required = false) BigDecimal targetPrice,
            @RequestParam(required = false) Boolean alertEnabled
    ) {
        try {
            System.out.println("🔄 Controller: Updating wishlist " + wishlistId +
                    " - Target: " + targetPrice + ", Alert: " + alertEnabled);

            WishlistResponse response = wishlistService.updateWishlist(wishlistId, targetPrice, alertEnabled);

            System.out.println("✅ Controller: Response - Alert: " + response.isAlertEnabled() +
                    ", Target: " + response.getTargetPrice());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.err.println("❌ Controller: Error updating wishlist: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{wishlistId}")
    public ResponseEntity<Map<String, String>> deleteWishlist(@PathVariable Long wishlistId) {
        try {
            wishlistService.deleteWishlist(wishlistId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Wishlist item deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/check-prices")
    public ResponseEntity<Map<String, String>> triggerPriceCheck() {
        priceChecker.manualPriceCheck();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Price check triggered successfully");
        return ResponseEntity.ok(response);
    }
}