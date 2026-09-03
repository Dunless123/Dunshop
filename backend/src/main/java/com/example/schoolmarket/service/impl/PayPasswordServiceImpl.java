package com.example.schoolmarket.service.impl;

import com.example.schoolmarket.entity.PayPassword;
import com.example.schoolmarket.mapper.PayPasswordMapper;
import com.example.schoolmarket.service.PayPasswordService;
import com.example.schoolmarket.utils.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class PayPasswordServiceImpl implements PayPasswordService {

    @Autowired
    private PayPasswordMapper payPasswordMapper;

    @Autowired
    private PasswordUtil passwordUtil;

    @Override
    public Map<String, Object> setPassword(Long userId, String password) {
        Map<String, Object> result = new HashMap<>();

        if (password.length() != 6) {
            result.put("success", false);
            result.put("message", "交易密码必须为6位数字");
            return result;
        }

        if (!password.matches("\\d{6}")) {
            result.put("success", false);
            result.put("message", "交易密码只能包含数字");
            return result;
        }

        PayPassword existing = payPasswordMapper.selectByUserId(userId);
        if (existing != null) {
            result.put("success", false);
            result.put("message", "交易密码已设置，请使用修改密码功能");
            return result;
        }

        PayPassword payPassword = new PayPassword();
        payPassword.setUserId(userId);
        payPassword.setPassword(passwordUtil.encode(password));
        payPassword.setCreatedAt(LocalDateTime.now());
        payPassword.setUpdatedAt(LocalDateTime.now());

        if (payPasswordMapper.insert(payPassword) > 0) {
            result.put("success", true);
            result.put("message", "交易密码设置成功");
        } else {
            result.put("success", false);
            result.put("message", "交易密码设置失败");
        }

        return result;
    }

    @Override
    public Map<String, Object> verifyPassword(Long userId, String password) {
        Map<String, Object> result = new HashMap<>();

        PayPassword payPassword = payPasswordMapper.selectByUserId(userId);
        if (payPassword == null) {
            result.put("success", false);
            result.put("message", "未设置交易密码");
            result.put("needSetPassword", true);
            return result;
        }

        if (passwordUtil.matches(password, payPassword.getPassword())) {
            result.put("success", true);
            result.put("message", "密码验证成功");
        } else {
            result.put("success", false);
            result.put("message", "交易密码错误");
        }

        return result;
    }

    @Override
    public Map<String, Object> changePassword(Long userId, String oldPassword, String newPassword) {
        Map<String, Object> result = new HashMap<>();

        if (newPassword.length() != 6) {
            result.put("success", false);
            result.put("message", "新密码必须为6位数字");
            return result;
        }

        if (!newPassword.matches("\\d{6}")) {
            result.put("success", false);
            result.put("message", "新密码只能包含数字");
            return result;
        }

        PayPassword payPassword = payPasswordMapper.selectByUserId(userId);
        if (payPassword == null) {
            result.put("success", false);
            result.put("message", "未设置交易密码");
            return result;
        }

        if (!passwordUtil.matches(oldPassword, payPassword.getPassword())) {
            result.put("success", false);
            result.put("message", "原密码错误");
            return result;
        }

        payPassword.setPassword(passwordUtil.encode(newPassword));
        payPassword.setUpdatedAt(LocalDateTime.now());

        if (payPasswordMapper.update(payPassword) > 0) {
            result.put("success", true);
            result.put("message", "交易密码修改成功");
        } else {
            result.put("success", false);
            result.put("message", "交易密码修改失败");
        }

        return result;
    }

    @Override
    public Map<String, Object> resetPassword(Long userId, String newPassword) {
        Map<String, Object> result = new HashMap<>();

        if (newPassword.length() != 6) {
            result.put("success", false);
            result.put("message", "新密码必须为6位数字");
            return result;
        }

        if (!newPassword.matches("\\d{6}")) {
            result.put("success", false);
            result.put("message", "新密码只能包含数字");
            return result;
        }

        PayPassword payPassword = payPasswordMapper.selectByUserId(userId);
        if (payPassword == null) {
            result.put("success", false);
            result.put("message", "未设置交易密码");
            return result;
        }

        payPassword.setPassword(passwordUtil.encode(newPassword));
        payPassword.setUpdatedAt(LocalDateTime.now());

        if (payPasswordMapper.update(payPassword) > 0) {
            result.put("success", true);
            result.put("message", "交易密码重置成功");
        } else {
            result.put("success", false);
            result.put("message", "交易密码重置失败");
        }

        return result;
    }

    @Override
    public boolean hasPassword(Long userId) {
        return payPasswordMapper.selectByUserId(userId) != null;
    }
}