package com.smartShopper.smart_price_backend.repository;

import com.smartShopper.smart_price_backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find product by title + platform (used to avoid duplicates)
    Optional<Product> findByTitleAndPlatform(String title, String platform);

    Optional<Product> findByPlatformProductUrl(String platformProductUrl);
}
