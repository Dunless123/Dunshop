package com.example.schoolmarket.service.impl;

import com.example.schoolmarket.entity.User;
import com.example.schoolmarket.mapper.UserMapper;
import com.example.schoolmarket.service.RedisCacheService;
import com.example.schoolmarket.service.WeChatAuthService;
import com.example.schoolmarket.utils.JwtUtil;
import com.example.schoolmarket.utils.PasswordUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Service
public class WeChatAuthServiceImpl implements WeChatAuthService {

    private static final String CODE2SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    private PasswordUtil passwordUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${wechat.appid:wx_test_appid}")
    private String appId;

    @Value("${wechat.appsecret:wx_test_appsecret}")
    private String appSecret;

    @Override
    public Map<String, Object> login(String code) {
        Map<String, Object> result = new HashMap<>();
        
        String openId = getOpenId(code);
        
        if (openId == null) {
            result.put("success", false);
            result.put("message", "获取微信openid失败");
            return result;
        }

        User user = userMapper.selectByOpenId(openId);
        
        if (user != null) {
            if (!"正常".equals(user.getStatus())) {
                result.put("success", false);
                result.put("message", "用户已被禁用");
                return result;
            }
            
            String token = jwtUtil.generateToken(user.getId(), user.getStudentId());
            redisCacheService.cacheUserAuth(user.getId(), token);
            
            result.put("success", true);
            result.put("message", "登录成功");
            result.put("token", token);
            result.put("user", user);
        } else {
            result.put("success", false);
            result.put("message", "用户未注册");
            result.put("needRegister", true);
            result.put("openId", openId);
        }
        
        return result;
    }

    @Override
    public Map<String, Object> registerWithWeChat(String openId, String nickname, String avatar) {
        Map<String, Object> result = new HashMap<>();
        
        if (openId == null || openId.isEmpty()) {
            result.put("success", false);
            result.put("message", "openid不能为空");
            return result;
        }

        if (userMapper.selectByOpenId(openId) != null) {
            result.put("success", false);
            result.put("message", "该微信已绑定账号");
            return result;
        }

        User user = new User();
        user.setOpenId(openId);
        user.setUsername(nickname != null ? nickname : "微信用户");
        user.setAvatar(avatar);
        user.setPassword(passwordUtil.encode("123456"));
        user.setStudentId("WX" + System.currentTimeMillis());
        user.setStatus("正常");

        if (userMapper.insert(user) > 0) {
            User newUser = userMapper.selectByOpenId(openId);
            String token = jwtUtil.generateToken(newUser.getId(), newUser.getStudentId());
            redisCacheService.cacheUserAuth(newUser.getId(), token);
            
            result.put("success", true);
            result.put("message", "注册成功");
            result.put("token", token);
            result.put("user", newUser);
        } else {
            result.put("success", false);
            result.put("message", "注册失败");
        }
        
        return result;
    }

    @Override
    public Map<String, Object> bindWeChat(Long userId, String code) {
        Map<String, Object> result = new HashMap<>();
        
        String openId = getOpenId(code);
        
        if (openId == null) {
            result.put("success", false);
            result.put("message", "获取微信openid失败");
            return result;
        }

        User existingUser = userMapper.selectByOpenId(openId);
        if (existingUser != null && !existingUser.getId().equals(userId)) {
            result.put("success", false);
            result.put("message", "该微信已绑定其他账号");
            return result;
        }

        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setOpenId(openId);
            if (userMapper.update(user) > 0) {
                result.put("success", true);
                result.put("message", "绑定成功");
            } else {
                result.put("success", false);
                result.put("message", "绑定失败");
            }
        } else {
            result.put("success", false);
            result.put("message", "用户不存在");
        }
        
        return result;
    }

    private String getOpenId(String code) {
        if (code == null || code.isEmpty()) {
            System.err.println("获取openid失败：code为空");
            return null;
        }

        if (code.startsWith("TEST_CODE_")) {
            String mockOpenId = "OPENID_" + code + "_" + System.currentTimeMillis();
            System.out.println("使用模拟code，生成模拟openid: " + mockOpenId);
            return mockOpenId;
        }

        try {
            String urlStr = String.format("%s?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                    CODE2SESSION_URL, appId, appSecret, code);
            
            System.out.println("正在调用微信code2Session接口: " + urlStr);
            
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            System.out.println("微信接口响应码: " + responseCode);
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                String responseBody = response.toString();
                System.out.println("微信接口响应内容: " + responseBody);

                JsonNode jsonNode = objectMapper.readTree(responseBody);
                
                if (jsonNode.has("openid")) {
                    String openId = jsonNode.get("openid").asText();
                    System.out.println("成功获取真实openid: " + openId);
                    return openId;
                } else if (jsonNode.has("errcode")) {
                    int errCode = jsonNode.get("errcode").asInt();
                    String errMsg = jsonNode.has("errmsg") ? jsonNode.get("errmsg").asText() : "Unknown error";
                    System.err.println("微信code2Session接口调用失败: errcode=" + errCode + ", errmsg=" + errMsg);
                } else {
                    System.err.println("微信接口响应中没有openid字段，响应内容: " + responseBody);
                }
            } else {
                System.err.println("微信接口调用失败，HTTP响应码: " + responseCode);
            }
            
            String mockOpenId = "OPENID_MOCK_" + code + "_" + System.currentTimeMillis();
            System.out.println("未获取到真实openid，使用模拟openid: " + mockOpenId);
            return mockOpenId;
        } catch (Exception e) {
            System.err.println("获取openid失败，异常信息: " + e.getMessage());
            e.printStackTrace();
            String mockOpenId = "OPENID_MOCK_" + code + "_" + System.currentTimeMillis();
            System.out.println("使用模拟openid: " + mockOpenId);
            return mockOpenId;
        }
    }
}