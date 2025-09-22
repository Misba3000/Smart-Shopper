package com.smartShopper.smart_price_backend.service.scraper;

import com.smartShopper.smart_price_backend.entity.Product;

public interface ScraperService {
    Product scrape(String productUrl) throws Exception;
}
