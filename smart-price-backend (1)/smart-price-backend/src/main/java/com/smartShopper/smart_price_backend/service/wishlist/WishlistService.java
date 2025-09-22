package com.smartShopper.smart_price_backend.service;

import com.smartShopper.smart_price_backend.dto.product.ProductResponse;
import com.smartShopper.smart_price_backend.dto.wishlist.WishlistDTO;
import java.util.List;

public interface WishlistService {
    WishlistDTO addToWishlist(Long userId, ProductResponse product);
    List<WishlistDTO> getUserWishlist(Long userId);
    void removeFromWishlist(Long userId, Long wishlistId);
}
