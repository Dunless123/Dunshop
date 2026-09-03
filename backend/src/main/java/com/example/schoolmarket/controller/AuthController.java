package com.example.schoolmarket.controller;

import com.example.schoolmarket.entity.User;
import com.example.schoolmarket.service.UserService;
import com.example.schoolmarket.utils.JwtUtil;
import com.example.schoolmarket.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public HashMap<String, Object> login(@RequestBody HashMap<String, String> params) {
        String studentId = params.get("studentId");
        String password = params.get("password");
        User user = userService.login(studentId, password);
        if (user != null) {
            if (!"正常".equals(user.getStatus())) {
                return ResponseUtil.error(401, "用户已被禁用");
            }
            String token = jwtUtil.generateToken(user.getId(), user.getUsername());
            HashMap<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", user);
            return ResponseUtil.success(data, "登录成功");
        }
        return ResponseUtil.error(401, "用户名或密码错误");
    }

    @PostMapping("/register")
    public HashMap<String, Object> register(@RequestBody User user) {
        boolean success = userService.register(user);
        if (success) {
            return ResponseUtil.success(null, "注册成功");
        }
        return ResponseUtil.error(400, "注册失败");
    }

    @PostMapping("/forgot")
    public HashMap<String, Object> forgot(@RequestBody HashMap<String, String> params) {
        String studentId = params.get("studentId");
        String newPassword = params.get("newPassword");
        boolean success = userService.resetPassword(studentId, newPassword);
        if (success) {
            return ResponseUtil.success(null, "密码重置成功");
        }
        return ResponseUtil.error(400, "密码重置失败");
    }

    @PostMapping("/change-password")
    public HashMap<String, Object> changePassword(@RequestBody HashMap<String, String> params) {
        String studentId = params.get("studentId");
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        
        User user = userService.login(studentId, oldPassword);
        if (user != null) {
            boolean success = userService.resetPassword(studentId, newPassword);
            if (success) {
                return ResponseUtil.success(null, "密码修改成功");
            }
            return ResponseUtil.error(400, "密码修改失败");
        }
        return ResponseUtil.error(401, "旧密码不正确");
    }

    @PostMapping("/logout")
    public HashMap<String, Object> logout() {
        return ResponseUtil.success(null, "退出登录成功");
    }
}