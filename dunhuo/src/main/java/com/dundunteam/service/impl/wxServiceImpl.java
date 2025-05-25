package com.dundunteam.service.impl;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dundunteam.controller.userController;
import com.dundunteam.mapper.radis.RedisKey;
import com.dundunteam.service.wxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

@Component
public class wxServiceImpl implements wxService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    private static final Logger logger = LoggerFactory.getLogger(userController.class);

    public String wxDecrypt(String encryptedData, String sessionId, String vi) throws Exception {
        logger.info("WX_SESSION_ID: {}", RedisKey.WX_SESSION_ID);
        logger.info("SessionId: {}", sessionId);
        logger.info("Full Key: {}", RedisKey.WX_SESSION_ID + sessionId);

        String json = redisTemplate.opsForValue().get(RedisKey.WX_SESSION_ID+sessionId);
        logger.info("这里呢{}",redisTemplate.opsForValue().get(RedisKey.WX_SESSION_ID+sessionId));
        logger.info("js是什么样子{}", json);
        JSONObject jsonObject = JSON.parseObject(json);
        String sessionKey = (String) jsonObject.get("session_key");
        byte[] encData = cn.hutool.core.codec.Base64.decode(encryptedData);
        byte[] iv = cn.hutool.core.codec.Base64.decode(vi);
        byte[] key = Base64.decode(sessionKey);
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        return new String(cipher.doFinal(encData), "UTF-8");
    }
}