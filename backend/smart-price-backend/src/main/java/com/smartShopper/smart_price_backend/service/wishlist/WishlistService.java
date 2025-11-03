package com.smartShopper.smart_price_backend.service.wishlist;

import com.smartShopper.smart_price_backend.dto.Wishlist.AddToWishlistRequest;
import com.smartShopper.smart_price_backend.dto.Wishlist.WishlistResponse;

import java.math.BigDecimal;
import java.util.List;

public interface WishlistService {

    WishlistResponse addToWishlist(Long userId, AddToWishlistRequest request);

    WishlistResponse getWishlistItem(Long wishlistId);

    List<WishlistResponse> getUserWishlist(Long userId);

    WishlistResponse updateWishlist(Long wishlistId, BigDecimal targetPrice, Boolean alertEnabled);

    void deleteWishlist(Long wishlistId);
}
