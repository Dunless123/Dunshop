package com.example.schoolmarket.mapper;

import com.example.schoolmarket.entity.Favorite;
import java.util.List;
import java.util.Map;

public interface FavoriteMapper {
    List<Favorite> selectByUserId(Map<String, Object> params);
    Favorite selectByUserIdAndGoodsId(Map<String, Object> params);
    Favorite selectById(Long id);
    int insert(Favorite favorite);
    int update(Favorite favorite);
    int delete(Long id);
    List<Favorite> selectAll();
    List<Favorite> selectByUserId(Long userId);
}