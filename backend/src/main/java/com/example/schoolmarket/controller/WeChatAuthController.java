package com.example.schoolmarket.controller;

import com.example.schoolmarket.service.WeChatAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/wechat")
public class WeChatAuthController {

    @Autowired
    private WeChatAuthService weChatAuthService;

    @PostMapping("/login")
    public Map<String, Object> weChatLogin(@RequestBody Map<String, String> params) {
        String code = params.get("code");
        return weChatAuthService.login(code);
    }

    @PostMapping("/register")
    public Map<String, Object> weChatRegister(@RequestBody Map<String, String> params) {
        String openId = params.get("openId");
        String nickname = params.get("nickname");
        String avatar = params.get("avatar");
        return weChatAuthService.registerWithWeChat(openId, nickname, avatar);
    }

    @PostMapping("/bind")
    public Map<String, Object> bindWeChat(@RequestBody Map<String, Object> params) {
        Long userId = ((Number) params.get("userId")).longValue();
        String code = (String) params.get("code");
        return weChatAuthService.bindWeChat(userId, code);
    }
}