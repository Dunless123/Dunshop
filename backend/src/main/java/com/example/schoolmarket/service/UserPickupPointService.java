package com.example.schoolmarket.service;

import com.example.schoolmarket.entity.Pickup;

import java.util.List;

public interface UserPickupPointService {
    boolean addPickupPoint(Long userId, Long pickupId);
    boolean removePickupPoint(Long userId, Long pickupId);
    List<Pickup> getPickupPointsByUserId(Long userId);
    boolean isPickupPointAdded(Long userId, Long pickupId);
}