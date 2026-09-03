package com.example.schoolmarket.mapper;

import com.example.schoolmarket.entity.Goods;
import java.util.List;
import java.util.Map;

public interface GoodsMapper {
    List<Goods> selectAllGoods();
    List<Goods> selectGoodsList(Map<String, Object> params);
    int countGoods(Map<String, Object> params);
    List<Goods> selectBySellerId(Map<String, Object> params);
    Goods selectById(Long id);
    int insert(Goods goods);
    int update(Goods goods);
    int delete(Long id);
    int sumViewsBySellerId(Long sellerId);
    int sumFavoritesBySellerId(Long sellerId);
    List<Goods> selectFavorites(Map<String, Object> params);
    int countFavorites(Long userId);
    List<Goods> selectPopularGoods(int limit);
}