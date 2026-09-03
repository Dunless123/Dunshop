package com.example.schoolmarket.service.impl;

import com.example.schoolmarket.entity.Favorite;
import com.example.schoolmarket.entity.Goods;
import com.example.schoolmarket.entity.User;
import com.example.schoolmarket.mapper.FavoriteMapper;
import com.example.schoolmarket.mapper.GoodsMapper;
import com.example.schoolmarket.mapper.UserMapper;
import com.example.schoolmarket.service.RedisCacheService;
import com.example.schoolmarket.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisCacheService redisCacheService;

    private static final String RECOMMENDATION_CACHE_KEY_PREFIX = "schoolmarket:recommendations:";

    @Override
    public List<Map<String, Object>> getUserBasedRecommendations(Long userId, int limit) {
        Map<Long, Set<Long>> userFavorites = buildUserFavoritesMap();
        Set<Long> targetUserFavorites = userFavorites.getOrDefault(userId, new HashSet<>());
        
        Map<Long, Double> userSimilarities = new HashMap<>();
        for (Long otherUserId : userFavorites.keySet()) {
            if (!otherUserId.equals(userId)) {
                double similarity = calculateCosineSimilarity(targetUserFavorites, userFavorites.get(otherUserId));
                if (similarity > 0) {
                    userSimilarities.put(otherUserId, similarity);
                }
            }
        }

        List<Long> similarUsers = userSimilarities.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        Map<Long, Double> itemScores = new HashMap<>();
        for (Long similarUserId : similarUsers) {
            double weight = userSimilarities.get(similarUserId);
            for (Long goodsId : userFavorites.getOrDefault(similarUserId, new HashSet<>())) {
                if (!targetUserFavorites.contains(goodsId)) {
                    itemScores.merge(goodsId, weight, Double::sum);
                }
            }
        }

        return getRecommendedGoods(itemScores, limit);
    }

    @Override
    public List<Map<String, Object>> getItemBasedRecommendations(Long userId, int limit) {
        Set<Long> userFavorites = getFavoriteGoodsIds(userId);
        if (userFavorites.isEmpty()) {
            return getPopularGoods(limit);
        }

        Map<Long, Set<Long>> itemCoOccurrence = buildItemCoOccurrenceMap();
        
        Map<Long, Double> itemScores = new HashMap<>();
        for (Long favoriteGoodsId : userFavorites) {
            Set<Long> coOccurringItems = itemCoOccurrence.getOrDefault(favoriteGoodsId, new HashSet<>());
            for (Long coItemId : coOccurringItems) {
                if (!userFavorites.contains(coItemId)) {
                    int coOccurrenceCount = countCoOccurrence(favoriteGoodsId, coItemId, itemCoOccurrence);
                    itemScores.merge(coItemId, coOccurrenceCount * 0.1, Double::sum);
                }
            }
        }

        return getRecommendedGoods(itemScores, limit);
    }

    @Override
    public List<Map<String, Object>> getHybridRecommendations(Long userId, int limit) {
        List<Map<String, Object>> userBasedRecs = getUserBasedRecommendations(userId, limit);
        List<Map<String, Object>> itemBasedRecs = getItemBasedRecommendations(userId, limit);

        Map<Long, Map<String, Object>> merged = new LinkedHashMap<>();
        
        for (Map<String, Object> item : userBasedRecs) {
            Long goodsId = ((Number) item.get("id")).longValue();
            item.put("score", 0.6);
            merged.put(goodsId, item);
        }

        for (Map<String, Object> item : itemBasedRecs) {
            Long goodsId = ((Number) item.get("id")).longValue();
            if (merged.containsKey(goodsId)) {
                double existingScore = (Double) merged.get(goodsId).get("score");
                merged.get(goodsId).put("score", existingScore + 0.4);
            } else {
                item.put("score", 0.4);
                merged.put(goodsId, item);
            }
        }

        return merged.values().stream()
                .sorted((a, b) -> Double.compare((Double) b.get("score"), (Double) a.get("score")))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public void updateRecommendationsCache(Long userId) {
        List<Map<String, Object>> recommendations = getHybridRecommendations(userId, 20);
        String cacheKey = RECOMMENDATION_CACHE_KEY_PREFIX + userId;
        redisCacheService.set(cacheKey, recommendations, 3600);
    }

    @Override
    public List<Map<String, Object>> getCachedRecommendations(Long userId) {
        String cacheKey = RECOMMENDATION_CACHE_KEY_PREFIX + userId;
        Object cached = redisCacheService.get(cacheKey);
        if (cached != null && cached instanceof List) {
            return (List<Map<String, Object>>) cached;
        }
        List<Map<String, Object>> recommendations = getHybridRecommendations(userId, 20);
        redisCacheService.set(cacheKey, recommendations, 3600);
        return recommendations;
    }

    private Map<Long, Set<Long>> buildUserFavoritesMap() {
        List<Favorite> allFavorites = favoriteMapper.selectAll();
        Map<Long, Set<Long>> userFavorites = new HashMap<>();
        for (Favorite favorite : allFavorites) {
            userFavorites.computeIfAbsent(favorite.getUserId(), k -> new HashSet<>()).add(favorite.getGoodsId());
        }
        return userFavorites;
    }

    private Map<Long, Set<Long>> buildItemCoOccurrenceMap() {
        Map<Long, Set<Long>> itemCoOccurrence = new HashMap<>();
        Map<Long, Set<Long>> userFavorites = buildUserFavoritesMap();

        for (Set<Long> favorites : userFavorites.values()) {
            List<Long> favList = new ArrayList<>(favorites);
            for (int i = 0; i < favList.size(); i++) {
                for (int j = i + 1; j < favList.size(); j++) {
                    Long item1 = favList.get(i);
                    Long item2 = favList.get(j);
                    itemCoOccurrence.computeIfAbsent(item1, k -> new HashSet<>()).add(item2);
                    itemCoOccurrence.computeIfAbsent(item2, k -> new HashSet<>()).add(item1);
                }
            }
        }
        return itemCoOccurrence;
    }

    private int countCoOccurrence(Long item1, Long item2, Map<Long, Set<Long>> itemCoOccurrence) {
        Set<Long> coOccurring = itemCoOccurrence.getOrDefault(item1, new HashSet<>());
        return coOccurring.contains(item2) ? 1 : 0;
    }

    private double calculateCosineSimilarity(Set<Long> set1, Set<Long> set2) {
        if (set1.isEmpty() || set2.isEmpty()) {
            return 0;
        }
        int intersection = 0;
        for (Long item : set1) {
            if (set2.contains(item)) {
                intersection++;
            }
        }
        double magnitude1 = Math.sqrt(set1.size());
        double magnitude2 = Math.sqrt(set2.size());
        return intersection / (magnitude1 * magnitude2);
    }

    private Set<Long> getFavoriteGoodsIds(Long userId) {
        List<Favorite> favorites = favoriteMapper.selectByUserId(userId);
        return favorites.stream()
                .map(Favorite::getGoodsId)
                .collect(Collectors.toSet());
    }

    private List<Map<String, Object>> getRecommendedGoods(Map<Long, Double> itemScores, int limit) {
        List<Long> recommendedGoodsIds = itemScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<Map<String, Object>> result = new ArrayList<>();
        for (Long goodsId : recommendedGoodsIds) {
            Goods goods = goodsMapper.selectById(goodsId);
            if (goods != null && "在售".equals(goods.getStatus())) {
                result.add(convertGoodsToMap(goods));
            }
        }
        return result;
    }

    private List<Map<String, Object>> getPopularGoods(int limit) {
        List<Goods> popularGoods = goodsMapper.selectPopularGoods(limit);
        return popularGoods.stream()
                .map(this::convertGoodsToMap)
                .collect(Collectors.toList());
    }

    private Map<String, Object> convertGoodsToMap(Goods goods) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", goods.getId());
        map.put("title", goods.getTitle());
        map.put("price", goods.getPrice());
        map.put("originalPrice", goods.getOriginalPrice());
        map.put("images", goods.getImages());
        map.put("categoryId", goods.getCategoryId());
        map.put("campusId", goods.getCampusId());
        map.put("viewCount", goods.getViewCount());
        map.put("favoriteCount", goods.getFavoriteCount());
        return map;
    }
}