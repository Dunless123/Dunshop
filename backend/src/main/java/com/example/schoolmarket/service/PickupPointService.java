package com.example.schoolmarket.service;

import com.example.schoolmarket.entity.Pickup;

import java.util.List;

public interface PickupPointService {
    List<Pickup> list();
    Pickup getById(Long id);
    boolean save(Pickup pickup);
    boolean update(Pickup pickup);
    boolean delete(Long id);
    int count();
}