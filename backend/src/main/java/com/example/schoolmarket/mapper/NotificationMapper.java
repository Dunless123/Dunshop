package com.example.schoolmarket.mapper;

import com.example.schoolmarket.entity.Notification;

import java.util.List;
import java.util.Map;

public interface NotificationMapper {
    int insert(Notification notification);
    int update(Notification notification);
    int delete(Long id);
    Notification selectById(Long id);
    List<Notification> selectList(Map<String, Object> params);
    int count(Map<String, Object> params);
}