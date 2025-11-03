package com.smartShopper.smart_price_backend.repository;

import com.smartShopper.smart_price_backend.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    // Get all wishlist items for a user
    List<Wishlist> findByUserId(Long userId);

    // Check if a product is already in a user's wishlist
    boolean existsByUserIdAndProductId(Long userId, Long productId);

    // Find specific wishlist by user and product
    Optional<Wishlist> findByUserIdAndProductId(Long userId, Long productId);

    // Find wishlists with alerts enabled for a specific user
    List<Wishlist> findByUserIdAndAlertEnabledTrue(Long userId);

    // Find first wishlist by product ID
    Optional<Wishlist> findFirstByProductId(Long productId);

    // Get all wishlists with alerts enabled
    @Query("SELECT w FROM Wishlist w " +
            "JOIN FETCH w.user " +
            "JOIN FETCH w.product " +
            "WHERE w.alertEnabled = true")
    List<Wishlist> findByAlertEnabledTrue();
}