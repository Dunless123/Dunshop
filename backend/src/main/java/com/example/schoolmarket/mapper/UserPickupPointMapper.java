package com.example.schoolmarket.mapper;

import com.example.schoolmarket.entity.UserPickupPoint;
import com.example.schoolmarket.entity.Pickup;

import java.util.List;
import java.util.Map;

public interface UserPickupPointMapper {
    int insert(UserPickupPoint userPickupPoint);
    int deleteByUserIdAndPickupId(Map<String, Object> params);
    List<Pickup> selectPickupPointsByUserId(Long userId);
    int countByUserIdAndPickupId(Map<String, Object> params);
}