package com.example.schoolmarket.service;

import java.util.List;
import java.util.Map;

public interface RedisCacheService {
    void cacheHotGoods(List<?> goodsList);
    List<?> getHotGoods();
    void cacheCategoryTags(List<?> tags);
    List<?> getCategoryTags();
    void cacheUserAuth(Long userId, String token);
    String getUserAuth(Long userId);
    void removeUserAuth(Long userId);
    void cacheChatMessage(String sessionId, Object message);
    List<Object> getChatMessages(String sessionId);
    void cacheOrderStatus(Long orderId, String status);
    String getOrderStatus(Long orderId);
    void removeOrderStatus(Long orderId);
    void set(String key, Object value, long expireSeconds);
    Object get(String key);
    void delete(String key);
    boolean exists(String key);
    void setHash(String key, String hashKey, Object value);
    Object getHash(String key, String hashKey);
    Map<Object, Object> getHashAll(String key);
    void deleteHash(String key, String hashKey);
}