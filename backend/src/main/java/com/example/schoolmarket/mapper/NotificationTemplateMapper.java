package com.example.schoolmarket.mapper;

import com.example.schoolmarket.entity.NotificationTemplate;

import java.util.List;

public interface NotificationTemplateMapper {
    int insert(NotificationTemplate template);
    int update(NotificationTemplate template);
    int delete(Long id);
    NotificationTemplate selectById(Long id);
    List<NotificationTemplate> selectAll();
}