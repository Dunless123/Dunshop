package com.example.schoolmarket.service;

import java.util.Map;

public interface PayPasswordService {
    Map<String, Object> setPassword(Long userId, String password);
    Map<String, Object> verifyPassword(Long userId, String password);
    Map<String, Object> changePassword(Long userId, String oldPassword, String newPassword);
    Map<String, Object> resetPassword(Long userId, String newPassword);
    boolean hasPassword(Long userId);
}