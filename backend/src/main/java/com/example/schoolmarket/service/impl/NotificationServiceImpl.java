package com.example.schoolmarket.service.impl;

import com.example.schoolmarket.entity.Notification;
import com.example.schoolmarket.entity.NotificationTemplate;
import com.example.schoolmarket.entity.User;
import com.example.schoolmarket.mapper.NotificationMapper;
import com.example.schoolmarket.mapper.NotificationTemplateMapper;
import com.example.schoolmarket.mapper.UserMapper;
import com.example.schoolmarket.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private NotificationTemplateMapper templateMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean sendNotification(Long userId, String type, String content) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setContent(content);
        notification.setStatus("已送达");
        notification.setSendTime(new Date());
        notification.setCreateTime(new Date());
        
        User user = userMapper.selectById(userId);
        if (user != null) {
            notification.setUsername(user.getUsername());
        } else {
            notification.setUsername("未知用户");
        }
        
        return notificationMapper.insert(notification) > 0;
    }

    @Override
    public boolean resendNotification(Long id) {
        Notification notification = notificationMapper.selectById(id);
        if (notification != null) {
            notification.setStatus("已送达");
            notification.setSendTime(new Date());
            return notificationMapper.update(notification) > 0;
        }
        return false;
    }

    @Override
    public Notification getById(Long id) {
        return notificationMapper.selectById(id);
    }

    @Override
    public List<Notification> getList(int offset, int pageSize, Long userId, String type, String status) {
        Map<String, Object> params = new HashMap<>();
        params.put("offset", offset);
        params.put("pageSize", pageSize);
        params.put("userId", userId);
        params.put("type", type);
        params.put("status", status);
        return notificationMapper.selectList(params);
    }

    @Override
    public int getCount(Long userId, String type, String status) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("type", type);
        params.put("status", status);
        return notificationMapper.count(params);
    }

    @Override
    public boolean delete(Long id) {
        return notificationMapper.delete(id) > 0;
    }

    @Override
    public List<NotificationTemplate> getTemplates() {
        return templateMapper.selectAll();
    }

    @Override
    public NotificationTemplate getTemplateById(Long id) {
        return templateMapper.selectById(id);
    }

    @Override
    public boolean addTemplate(NotificationTemplate template) {
        return templateMapper.insert(template) > 0;
    }

    @Override
    public boolean updateTemplate(NotificationTemplate template) {
        return templateMapper.update(template) > 0;
    }

    @Override
    public boolean deleteTemplate(Long id) {
        return templateMapper.delete(id) > 0;
    }
}