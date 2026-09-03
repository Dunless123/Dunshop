package com.example.schoolmarket.mapper;

import com.example.schoolmarket.entity.Campus;
import java.util.List;

public interface CampusMapper {
    List<Campus> selectCampusList();
    Campus selectById(Long id);
    int insert(Campus campus);
    int update(Campus campus);
    int delete(Long id);
    int count();
}