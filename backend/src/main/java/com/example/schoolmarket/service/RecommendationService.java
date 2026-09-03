package com.example.schoolmarket.service;

import java.util.List;
import java.util.Map;

public interface RecommendationService {
    List<Map<String, Object>> getUserBasedRecommendations(Long userId, int limit);
    List<Map<String, Object>> getItemBasedRecommendations(Long userId, int limit);
    List<Map<String, Object>> getHybridRecommendations(Long userId, int limit);
    void updateRecommendationsCache(Long userId);
    List<Map<String, Object>> getCachedRecommendations(Long userId);
}