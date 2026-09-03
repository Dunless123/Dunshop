package com.example.schoolmarket.mapper;

import com.example.schoolmarket.entity.Pickup;
import java.util.List;

public interface PickupPointMapper {
    List<Pickup> selectList();
    Pickup selectById(Long id);
    int insert(Pickup pickup);
    int update(Pickup pickup);
    int delete(Long id);
    int count();
}