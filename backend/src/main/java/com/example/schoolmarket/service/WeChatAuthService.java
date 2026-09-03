package com.example.schoolmarket.service;

import java.util.Map;

public interface WeChatAuthService {
    Map<String, Object> login(String code);
    Map<String, Object> registerWithWeChat(String openId, String nickname, String avatar);
    Map<String, Object> bindWeChat(Long userId, String code);
}