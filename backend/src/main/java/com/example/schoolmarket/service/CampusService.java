package com.example.schoolmarket.service;

import com.example.schoolmarket.entity.Campus;

import java.util.List;

public interface CampusService {
    Campus getById(Long id);
    boolean save(Campus campus);
    boolean update(Campus campus);
    boolean delete(Long id);
    List<Campus> list();
    int count();
}