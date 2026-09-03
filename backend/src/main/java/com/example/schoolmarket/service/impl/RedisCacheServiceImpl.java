package com.example.schoolmarket.service.impl;

import com.example.schoolmarket.service.RedisCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class RedisCacheServiceImpl implements RedisCacheService {

    private static final String HOT_GOODS_KEY = "schoolmarket:hot_goods";
    private static final String CATEGORY_TAGS_KEY = "schoolmarket:category_tags";
    private static final String USER_AUTH_KEY_PREFIX = "schoolmarket:user_auth:";
    private static final String CHAT_MESSAGES_KEY_PREFIX = "schoolmarket:chat_messages:";
    private static final String ORDER_STATUS_KEY_PREFIX = "schoolmarket:order_status:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void cacheHotGoods(List<?> goodsList) {
        redisTemplate.delete(HOT_GOODS_KEY);
        for (int i = 0; i < goodsList.size(); i++) {
            redisTemplate.opsForList().rightPush(HOT_GOODS_KEY, goodsList.get(i));
        }
        redisTemplate.expire(HOT_GOODS_KEY, 30, TimeUnit.MINUTES);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<?> getHotGoods() {
        return (List<?>) redisTemplate.opsForList().range(HOT_GOODS_KEY, 0, -1);
    }

    @Override
    public void cacheCategoryTags(List<?> tags) {
        redisTemplate.delete(CATEGORY_TAGS_KEY);
        for (int i = 0; i < tags.size(); i++) {
            redisTemplate.opsForList().rightPush(CATEGORY_TAGS_KEY, tags.get(i));
        }
        redisTemplate.expire(CATEGORY_TAGS_KEY, 1, TimeUnit.HOURS);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<?> getCategoryTags() {
        return (List<?>) redisTemplate.opsForList().range(CATEGORY_TAGS_KEY, 0, -1);
    }

    @Override
    public void cacheUserAuth(Long userId, String token) {
        String key = USER_AUTH_KEY_PREFIX + userId;
        redisTemplate.opsForValue().set(key, token, 1, TimeUnit.DAYS);
    }

    @Override
    public String getUserAuth(Long userId) {
        String key = USER_AUTH_KEY_PREFIX + userId;
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? value.toString() : null;
    }

    @Override
    public void removeUserAuth(Long userId) {
        String key = USER_AUTH_KEY_PREFIX + userId;
        redisTemplate.delete(key);
    }

    @Override
    public void cacheChatMessage(String sessionId, Object message) {
        String key = CHAT_MESSAGES_KEY_PREFIX + sessionId;
        redisTemplate.opsForList().rightPush(key, message);
        redisTemplate.expire(key, 7, TimeUnit.DAYS);
    }

    @Override
    public List<Object> getChatMessages(String sessionId) {
        String key = CHAT_MESSAGES_KEY_PREFIX + sessionId;
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    @Override
    public void cacheOrderStatus(Long orderId, String status) {
        String key = ORDER_STATUS_KEY_PREFIX + orderId;
        redisTemplate.opsForValue().set(key, status, 24, TimeUnit.HOURS);
    }

    @Override
    public String getOrderStatus(Long orderId) {
        String key = ORDER_STATUS_KEY_PREFIX + orderId;
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? value.toString() : null;
    }

    @Override
    public void removeOrderStatus(Long orderId) {
        String key = ORDER_STATUS_KEY_PREFIX + orderId;
        redisTemplate.delete(key);
    }

    @Override
    public void set(String key, Object value, long expireSeconds) {
        redisTemplate.opsForValue().set(key, value, expireSeconds, TimeUnit.SECONDS);
    }

    @Override
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public void setHash(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    @Override
    public Object getHash(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    @Override
    public Map<Object, Object> getHashAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    @Override
    public void deleteHash(String key, String hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }
}