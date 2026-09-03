package com.example.schoolmarket.service;

import com.example.schoolmarket.entity.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    User login(String studentId, String password);
    boolean register(User user);
    boolean resetPassword(String studentId, String newPassword);
    User getById(Long id);
    boolean save(User user);
    boolean update(User user);
    boolean delete(Long id);
    List<User> list();
    int getTotalUserCount();
    Map<String, Object> getUserStats(Long userId);
}