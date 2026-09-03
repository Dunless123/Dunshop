package com.example.schoolmarket.service.impl;

import com.example.schoolmarket.entity.Campus;
import com.example.schoolmarket.mapper.CampusMapper;
import com.example.schoolmarket.service.CampusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CampusServiceImpl implements CampusService {
    @Autowired
    private CampusMapper campusMapper;

    @Override
    public List<Campus> list() {
        return campusMapper.selectCampusList();
    }

    @Override
    public Campus getById(Long id) {
        return campusMapper.selectById(id);
    }

    @Override
    public boolean save(Campus campus) {
        return campusMapper.insert(campus) > 0;
    }

    @Override
    public boolean update(Campus campus) {
        return campusMapper.update(campus) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return campusMapper.delete(id) > 0;
    }

    @Override
    public int count() {
        return campusMapper.count();
    }
}