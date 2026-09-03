package com.example.schoolmarket.service.impl;

import com.example.schoolmarket.entity.Pickup;
import com.example.schoolmarket.mapper.PickupPointMapper;
import com.example.schoolmarket.service.PickupPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PickupPointServiceImpl implements PickupPointService {
    @Autowired
    private PickupPointMapper pickupPointMapper;

    @Override
    public List<Pickup> list() {
        return pickupPointMapper.selectList();
    }

    @Override
    public Pickup getById(Long id) {
        return pickupPointMapper.selectById(id);
    }

    @Override
    public boolean save(Pickup pickup) {
        return pickupPointMapper.insert(pickup) > 0;
    }

    @Override
    public boolean update(Pickup pickup) {
        return pickupPointMapper.update(pickup) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return pickupPointMapper.delete(id) > 0;
    }

    @Override
    public int count() {
        return pickupPointMapper.count();
    }
}