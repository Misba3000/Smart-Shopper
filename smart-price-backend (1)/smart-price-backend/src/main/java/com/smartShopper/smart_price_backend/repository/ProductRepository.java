package com.smartShopper.smart_price_backend.repository;

import com.smartShopper.smart_price_backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    Product findByPlatformProductUrl(String url);
}
