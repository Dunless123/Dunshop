package com.example.schoolmarket.service.impl;

import com.example.schoolmarket.entity.Favorite;
import com.example.schoolmarket.entity.Goods;
import com.example.schoolmarket.mapper.FavoriteMapper;
import com.example.schoolmarket.mapper.GoodsMapper;
import com.example.schoolmarket.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    private FavoriteMapper favoriteMapper;
    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public boolean addViewCount(Long goodsId) {
        Goods goods = getById(goodsId);
        if (goods != null) {
            goods.setViewCount(goods.getViewCount() + 1);
            return update(goods);
        }
        return false;
    }

    @Override
    public boolean addFavorite(Long userId, Long goodsId) {
        // 检查是否已经收藏
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("goodsId", goodsId);
        Favorite existing = favoriteMapper.selectByUserIdAndGoodsId(params);
        if (existing != null) {
            return false; // 已经收藏过了
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setGoodsId(goodsId);
        boolean saved = favoriteMapper.insert(favorite) > 0;
        if (saved) {
            Goods goods = getById(goodsId);
            if (goods != null) {
                goods.setFavoriteCount(goods.getFavoriteCount() + 1);
                update(goods);
            }
        }
        return saved;
    }

    @Override
    public boolean removeFavorite(Long userId, Long goodsId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("goodsId", goodsId);
        Favorite favorite = favoriteMapper.selectByUserIdAndGoodsId(params);
        if (favorite == null) {
            return false; // 没有收藏
        }

        boolean removed = favoriteMapper.delete(favorite.getId()) > 0;
        if (removed) {
            Goods goods = getById(goodsId);
            if (goods != null) {
                goods.setFavoriteCount(Math.max(0, goods.getFavoriteCount() - 1));
                update(goods);
            }
        }
        return removed;
    }

    @Override
    public boolean isFavorited(Long userId, Long goodsId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("goodsId", goodsId);
        Favorite favorite = favoriteMapper.selectByUserIdAndGoodsId(params);
        return favorite != null;
    }

    @Override
    public Goods getById(Long id) {
        return goodsMapper.selectById(id);
    }

    @Override
    public boolean save(Goods goods) {
        return goodsMapper.insert(goods) > 0;
    }

    @Override
    public boolean update(Goods goods) {
        return goodsMapper.update(goods) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return goodsMapper.delete(id) > 0;
    }

    @Override
    public List<Goods> list() {
        return goodsMapper.selectAllGoods();
    }

    @Override
    public List<Goods> getList(int offset, int pageSize, Long categoryId, Long campusId, String keyword, String sortBy, String order) {
        Map<String, Object> params = new HashMap<>();
        params.put("offset", offset);
        params.put("pageSize", pageSize);
        params.put("categoryId", categoryId);
        params.put("campusId", campusId);
        params.put("keyword", keyword);
        params.put("status", "在售");
        
        String dbSortBy = convertToDbColumnName(sortBy);
        params.put("sortBy", dbSortBy);
        params.put("order", order);
        return goodsMapper.selectGoodsList(params);
    }
    
    private String convertToDbColumnName(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return null;
        }
        Map<String, String> mapping = new HashMap<>();
        mapping.put("createTime", "create_time");
        mapping.put("updateTime", "update_time");
        mapping.put("viewCount", "view_count");
        mapping.put("favoriteCount", "favorite_count");
        mapping.put("categoryId", "category_id");
        mapping.put("campusId", "campus_id");
        mapping.put("sellerId", "seller_id");
        
        return mapping.getOrDefault(camelCase, camelCase);
    }

    @Override
    public int getCount(Long categoryId, Long campusId, String keyword) {
        Map<String, Object> params = new HashMap<>();
        params.put("categoryId", categoryId);
        params.put("campusId", campusId);
        params.put("keyword", keyword);
        return goodsMapper.countGoods(params);
    }

    @Override
    public List<Goods> getBySellerId(Map<String, Object> params) {
        return goodsMapper.selectBySellerId(params);
    }

    @Override
    public List<Goods> getFavorites(Long userId, int offset, int pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("offset", offset);
        params.put("pageSize", pageSize);
        return goodsMapper.selectFavorites(params);
    }

    @Override
    public int getFavoritesCount(Long userId) {
        return goodsMapper.countFavorites(userId);
    }

    @Override
    public int getTotalGoodsBySellerId(Long sellerId) {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("sellerId", sellerId);
        return goodsMapper.countGoods(params);
    }

    @Override
    public int getOnSaleGoodsBySellerId(Long sellerId) {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("sellerId", sellerId);
        params.put("status", "在售");
        return goodsMapper.countGoods(params);
    }

    @Override
    public int getSoldGoodsBySellerId(Long sellerId) {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("sellerId", sellerId);
        params.put("status", "已售");
        return goodsMapper.countGoods(params);
    }

    @Override
    public int getTotalViewsBySellerId(Long sellerId) {
        return goodsMapper.sumViewsBySellerId(sellerId);
    }

    @Override
    public int getTotalFavoritesBySellerId(Long sellerId) {
        return goodsMapper.sumFavoritesBySellerId(sellerId);
    }
}