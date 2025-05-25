package com.dundunteam.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dundunteam.common.context.Result;
import com.dundunteam.controller.userController;
import com.dundunteam.mapper.userMapper;
import com.dundunteam.pojo.dto.userDto;
import com.dundunteam.pojo.entity.WxAuth;
import com.dundunteam.pojo.entity.User;
import com.dundunteam.pojo.entity.wxUserInfo;
import com.dundunteam.service.userService;
import com.dundunteam.service.wxService;
import com.dundunteam.utils.JWTUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.dundunteam.common.constant.userConstant;
import com.dundunteam.mapper.radis.RedisKey;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class userServiceImpl implements userService {
    @Resource
    private userMapper userMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private wxService wxservice;
    private String appid = userConstant.appID;

    private String appSecret = userConstant.appSecret;
    private static final Logger logger = LoggerFactory.getLogger(userController.class);
    private HttpSession session;
    @Override
    public String getSessionId(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid={0}&secret={1}&js_code={2}&grant_type=authorization_code ";
        String replaceUrl = url.replace("{0}", appid).replace("{1}", appSecret).replace("{2}", code);
        String res = HttpUtil.get(replaceUrl);
        String uuid = UUID.randomUUID().toString();
        logger.info("uuid{}", uuid);
        redisTemplate.opsForValue().set(RedisKey.WX_SESSION_ID + uuid, res);
        return uuid;
    }
    public userDto login(userDto userDto) {
        // 登录成功 封装用户信息到token
        userDto.setPassword(null);
        userDto.setUsername(null);
        userDto.setOpenid(null);
        userDto.setWxUnionid(null);
        String token = JWTUtils.sign(userDto.getId());
        userDto.setToken(token);
        //保存到redis内,下次就直接跳过验证
        redisTemplate.opsForValue().set(RedisKey.TOKEN_KEY + token, JSON.toJSONString(userDto), 7, TimeUnit.DAYS);
        return userDto;
    }

    private Result register(userDto userDto) {
        User user = new User();
        BeanUtils.copyProperties(userDto,user);
        User queryUser = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getOpenid, user.getOpenid()));
        logger.info("openid{}",user.getOpenid());
        if (queryUser == null) {
            userMapper.insert(user);
        }
        //已存在直接登录
        return Result.success(login(userDto));
    }



    @Override
    public Result getAuthLogin(WxAuth wxAuth) {
            /*1,wxService对key解密
              2，获取用户信息
              3,openId是否唯一存在，存在则登录，不存在注册
              4，使用jwt返回token,令牌
              5,验证token状态
            * */


        try {
            String wxRes = wxservice.wxDecrypt(wxAuth.getEncryptedData(), wxAuth.getSessionId(), wxAuth.getIv());
            wxUserInfo wxUserInfo = JSON.parseObject(wxRes, wxUserInfo.class);
            User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getOpenid, wxUserInfo.getOpenId()));
            if (user != null) {
                //登录成功
                userDto userDto = new userDto();
                session.setAttribute("openId",user.getOpenid());
                BeanUtils.copyProperties(user, userDto);
                return Result.success(this.login(userDto));
            } else {
                userDto userDto = new userDto();
                userDto.from(wxUserInfo);
                return this.register(userDto);
            }
        } catch (Exception e) {
            logger.info("这里是报错", e);
            throw new RuntimeException(e);

        }

    }

}


