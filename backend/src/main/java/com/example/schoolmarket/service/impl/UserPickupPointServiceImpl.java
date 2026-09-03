package com.example.schoolmarket.service.impl;

import com.example.schoolmarket.entity.Pickup;
import com.example.schoolmarket.entity.UserPickupPoint;
import com.example.schoolmarket.mapper.UserPickupPointMapper;
import com.example.schoolmarket.service.UserPickupPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserPickupPointServiceImpl implements UserPickupPointService {

    @Autowired
    private UserPickupPointMapper userPickupPointMapper;

    @Override
    public boolean addPickupPoint(Long userId, Long pickupId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("pickupId", pickupId);
        
        if (userPickupPointMapper.countByUserIdAndPickupId(params) > 0) {
            return false;
        }

        UserPickupPoint userPickupPoint = new UserPickupPoint();
        userPickupPoint.setUserId(userId);
        userPickupPoint.setPickupId(pickupId);
        
        return userPickupPointMapper.insert(userPickupPoint) > 0;
    }

    @Override
    public boolean removePickupPoint(Long userId, Long pickupId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("pickupId", pickupId);
        
        return userPickupPointMapper.deleteByUserIdAndPickupId(params) > 0;
    }

    @Override
    public List<Pickup> getPickupPointsByUserId(Long userId) {
        return userPickupPointMapper.selectPickupPointsByUserId(userId);
    }

    @Override
    public boolean isPickupPointAdded(Long userId, Long pickupId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("pickupId", pickupId);
        
        return userPickupPointMapper.countByUserIdAndPickupId(params) > 0;
    }
}