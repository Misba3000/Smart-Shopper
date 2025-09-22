package com.smartShopper.smart_price_backend.repository;

import com.smartShopper.smart_price_backend.entity.Wishlist;
import com.smartShopper.smart_price_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findByUser(User user);
    boolean existsByUserAndProductUrl(User user, String productUrl);
    boolean existsByUserAndPlatformProductId(User user, String platformProductId);
}
