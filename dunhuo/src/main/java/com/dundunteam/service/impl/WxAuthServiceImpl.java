package com.dundunteam.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dundunteam.service.WxAuthService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WxAuthServiceImpl implements WxAuthService {


    private String appId;


    private String appSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getOpenIdByCode(String code) {
        String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                appId, appSecret, code
        );

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject json = JSON.parseObject(response.getBody());
            return json.getString("openid"); // 提取openId
        }
        return null;
    }
}