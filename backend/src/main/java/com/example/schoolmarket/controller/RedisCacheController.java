package com.example.schoolmarket.controller;

import com.example.schoolmarket.entity.Category;
import com.example.schoolmarket.entity.Goods;
import com.example.schoolmarket.mapper.CategoryMapper;
import com.example.schoolmarket.mapper.GoodsMapper;
import com.example.schoolmarket.service.RedisCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cache")
public class RedisCacheController {

    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @GetMapping("/hot-goods")
    public Map<String, Object> getHotGoods() {
        Map<String, Object> result = new HashMap<>();
        List<?> hotGoods = redisCacheService.getHotGoods();
        if (hotGoods == null || hotGoods.isEmpty()) {
            List<Goods> goodsList = goodsMapper.selectAllGoods();
            redisCacheService.cacheHotGoods(goodsList);
            result.put("data", goodsList);
            result.put("cached", false);
        } else {
            result.put("data", hotGoods);
            result.put("cached", true);
        }
        result.put("success", true);
        return result;
    }

    @PostMapping("/hot-goods")
    public Map<String, Object> refreshHotGoods() {
        Map<String, Object> result = new HashMap<>();
        List<Goods> goodsList = goodsMapper.selectAllGoods();
        redisCacheService.cacheHotGoods(goodsList);
        result.put("success", true);
        result.put("message", "热点商品缓存已刷新");
        return result;
    }

    @GetMapping("/categories")
    public Map<String, Object> getCategories() {
        Map<String, Object> result = new HashMap<>();
        List<?> categories = redisCacheService.getCategoryTags();
        if (categories == null || categories.isEmpty()) {
            List<Category> categoryList = categoryMapper.selectAll();
            redisCacheService.cacheCategoryTags(categoryList);
            result.put("data", categoryList);
            result.put("cached", false);
        } else {
            result.put("data", categories);
            result.put("cached", true);
        }
        result.put("success", true);
        return result;
    }

    @PostMapping("/categories")
    public Map<String, Object> refreshCategories() {
        Map<String, Object> result = new HashMap<>();
        List<Category> categoryList = categoryMapper.selectAll();
        redisCacheService.cacheCategoryTags(categoryList);
        result.put("success", true);
        result.put("message", "分类标签缓存已刷新");
        return result;
    }

    @GetMapping("/user-auth/{userId}")
    public Map<String, Object> getUserAuth(@PathVariable Long userId) {
        Map<String, Object> result = new HashMap<>();
        String token = redisCacheService.getUserAuth(userId);
        result.put("success", true);
        result.put("token", token);
        return result;
    }

    @PostMapping("/user-auth/{userId}")
    public Map<String, Object> setUserAuth(@PathVariable Long userId, @RequestBody Map<String, String> body) {
        Map<String, Object> result = new HashMap<>();
        String token = body.get("token");
        redisCacheService.cacheUserAuth(userId, token);
        result.put("success", true);
        result.put("message", "用户认证信息已缓存");
        return result;
    }

    @DeleteMapping("/user-auth/{userId}")
    public Map<String, Object> removeUserAuth(@PathVariable Long userId) {
        Map<String, Object> result = new HashMap<>();
        redisCacheService.removeUserAuth(userId);
        result.put("success", true);
        result.put("message", "用户认证信息已清除");
        return result;
    }

    @GetMapping("/order-status/{orderId}")
    public Map<String, Object> getOrderStatus(@PathVariable Long orderId) {
        Map<String, Object> result = new HashMap<>();
        String status = redisCacheService.getOrderStatus(orderId);
        result.put("success", true);
        result.put("status", status);
        return result;
    }

    @PostMapping("/order-status/{orderId}")
    public Map<String, Object> setOrderStatus(@PathVariable Long orderId, @RequestBody Map<String, String> body) {
        Map<String, Object> result = new HashMap<>();
        String status = body.get("status");
        redisCacheService.cacheOrderStatus(orderId, status);
        result.put("success", true);
        result.put("message", "订单状态已缓存");
        return result;
    }

    @DeleteMapping("/order-status/{orderId}")
    public Map<String, Object> removeOrderStatus(@PathVariable Long orderId) {
        Map<String, Object> result = new HashMap<>();
        redisCacheService.removeOrderStatus(orderId);
        result.put("success", true);
        result.put("message", "订单状态已清除");
        return result;
    }

    @DeleteMapping("/clear/{key}")
    public Map<String, Object> clearCache(@PathVariable String key) {
        Map<String, Object> result = new HashMap<>();
        redisCacheService.delete(key);
        result.put("success", true);
        result.put("message", "缓存已清除");
        return result;
    }

    @GetMapping("/exists/{key}")
    public Map<String, Object> exists(@PathVariable String key) {
        Map<String, Object> result = new HashMap<>();
        boolean exists = redisCacheService.exists(key);
        result.put("success", true);
        result.put("exists", exists);
        return result;
    }
}