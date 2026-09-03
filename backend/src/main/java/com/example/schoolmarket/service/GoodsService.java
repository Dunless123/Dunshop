package com.example.schoolmarket.service;

import com.example.schoolmarket.entity.Goods;

import java.util.List;
import java.util.Map;

public interface GoodsService {
    Goods getById(Long id);
    boolean save(Goods goods);
    boolean update(Goods goods);
    boolean delete(Long id);
    List<Goods> list();
    List<Goods> getList(int offset, int pageSize, Long categoryId, Long campusId, String keyword, String sortBy, String order);
    int getCount(Long categoryId, Long campusId, String keyword);
    List<Goods> getBySellerId(Map<String, Object> params);
    List<Goods> getFavorites(Long userId, int offset, int pageSize);
    int getFavoritesCount(Long userId);
    boolean addFavorite(Long userId, Long goodsId);
    boolean removeFavorite(Long userId, Long goodsId);
    boolean addViewCount(Long goodsId);
    boolean isFavorited(Long userId, Long goodsId);
    int getTotalGoodsBySellerId(Long sellerId);
    int getOnSaleGoodsBySellerId(Long sellerId);
    int getSoldGoodsBySellerId(Long sellerId);
    int getTotalViewsBySellerId(Long sellerId);
    int getTotalFavoritesBySellerId(Long sellerId);
}