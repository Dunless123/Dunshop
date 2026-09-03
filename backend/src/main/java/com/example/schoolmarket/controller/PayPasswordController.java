package com.example.schoolmarket.controller;

import com.example.schoolmarket.service.PayPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pay-password")
public class PayPasswordController {

    @Autowired
    private PayPasswordService payPasswordService;

    @PostMapping("/set")
    public Map<String, Object> setPassword(@RequestBody Map<String, Object> params) {
        Long userId = ((Number) params.get("userId")).longValue();
        String password = (String) params.get("password");
        return payPasswordService.setPassword(userId, password);
    }

    @PostMapping("/verify")
    public Map<String, Object> verifyPassword(@RequestBody Map<String, Object> params) {
        Long userId = ((Number) params.get("userId")).longValue();
        String password = (String) params.get("password");
        return payPasswordService.verifyPassword(userId, password);
    }

    @PostMapping("/change")
    public Map<String, Object> changePassword(@RequestBody Map<String, Object> params) {
        Long userId = ((Number) params.get("userId")).longValue();
        String oldPassword = (String) params.get("oldPassword");
        String newPassword = (String) params.get("newPassword");
        return payPasswordService.changePassword(userId, oldPassword, newPassword);
    }

    @PostMapping("/reset")
    public Map<String, Object> resetPassword(@RequestBody Map<String, Object> params) {
        Long userId = ((Number) params.get("userId")).longValue();
        String newPassword = (String) params.get("newPassword");
        return payPasswordService.resetPassword(userId, newPassword);
    }

    @GetMapping("/has")
    public Map<String, Object> hasPassword(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("hasPassword", payPasswordService.hasPassword(userId));
        return result;
    }
}