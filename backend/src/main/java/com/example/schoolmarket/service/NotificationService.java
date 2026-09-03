package com.example.schoolmarket.service;

import com.example.schoolmarket.entity.Notification;
import com.example.schoolmarket.entity.NotificationTemplate;

import java.util.List;

public interface NotificationService {
    boolean sendNotification(Long userId, String type, String content);
    boolean resendNotification(Long id);
    Notification getById(Long id);
    List<Notification> getList(int offset, int pageSize, Long userId, String type, String status);
    int getCount(Long userId, String type, String status);
    boolean delete(Long id);
    
    List<NotificationTemplate> getTemplates();
    NotificationTemplate getTemplateById(Long id);
    boolean addTemplate(NotificationTemplate template);
    boolean updateTemplate(NotificationTemplate template);
    boolean deleteTemplate(Long id);
}