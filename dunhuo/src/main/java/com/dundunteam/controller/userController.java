package com.dundunteam.controller;

import com.dundunteam.common.context.Result;
import com.dundunteam.pojo.entity.WxAuth;
import com.dundunteam.service.userService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/user")

public class userController {
    @Autowired
    private userService userService;
    /*微信登录：
        getSessionId():获取微信表示code,用于登录确认用户唯一性
     */
    @GetMapping("/getSessionId")
    public Result getSessionId(String code){
        String sessionId =userService.getSessionId(code);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("sessionId",sessionId);
        return Result.success(hashMap);
    }
    private static final Logger logger = LoggerFactory.getLogger(userController.class);
    public  Result Login(WxAuth wxAuth){
        Result result =new Result();
        result.setData(wxAuth);
        logger.info("这里是res{}",result);
        return  result;
    }


}
