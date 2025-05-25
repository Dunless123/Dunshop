package com.dundunteam.service;

import com.dundunteam.common.context.Result;
import com.dundunteam.pojo.entity.WxAuth;

public interface userService {
    String getSessionId(String code);


    Result getAuthLogin(WxAuth wxAuth);
}
