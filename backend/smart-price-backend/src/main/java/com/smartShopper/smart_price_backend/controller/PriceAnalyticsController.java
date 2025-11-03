package com.smartShopper.smart_price_backend.controller;

import com.smartShopper.smart_price_backend.dto.analytics.PriceAnalyticsResponse;
import com.smartShopper.smart_price_backend.service.analytics.PriceTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/analytics")
public class PriceAnalyticsController {

    @Autowired
    private PriceTrackingService priceTrackingService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PriceAnalyticsResponse>> getUserPriceAnalytics(@PathVariable Long userId) {
        List<PriceAnalyticsResponse> analytics = priceTrackingService.getUserPriceAnalytics(userId);
        return ResponseEntity.ok(analytics);
    }

    @PostMapping("/track-prices")
    public ResponseEntity<Map<String, String>> triggerPriceTracking() {
        priceTrackingService.manualTrackPrices();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Price tracking triggered successfully");
        return ResponseEntity.ok(response);
    }
}