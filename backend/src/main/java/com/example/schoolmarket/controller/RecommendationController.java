package com.example.schoolmarket.controller;

import com.example.schoolmarket.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommend")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/user-based")
    public Map<String, Object> getUserBasedRecommendations(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> recommendations = recommendationService.getUserBasedRecommendations(userId, limit);
            result.put("success", true);
            result.put("data", recommendations);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/item-based")
    public Map<String, Object> getItemBasedRecommendations(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> recommendations = recommendationService.getItemBasedRecommendations(userId, limit);
            result.put("success", true);
            result.put("data", recommendations);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/hybrid")
    public Map<String, Object> getHybridRecommendations(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> recommendations = recommendationService.getHybridRecommendations(userId, limit);
            result.put("success", true);
            result.put("data", recommendations);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/cached")
    public Map<String, Object> getCachedRecommendations(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> recommendations = recommendationService.getCachedRecommendations(userId);
            result.put("success", true);
            result.put("data", recommendations);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/refresh/{userId}")
    public Map<String, Object> refreshRecommendations(@PathVariable Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            recommendationService.updateRecommendationsCache(userId);
            result.put("success", true);
            result.put("message", "推荐缓存已刷新");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}