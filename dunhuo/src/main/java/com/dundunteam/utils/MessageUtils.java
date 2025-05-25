package com.dundunteam.utils;

import com.alibaba.fastjson.JSON;
import com.dundunteam.pojo.entity.ResultMessage;

public class MessageUtils {
    public  static  String getMessage(boolean isSystemMessage,String fromId,Object message){
        ResultMessage result = new ResultMessage();
        result.setSystem(isSystemMessage);
        result.setMessage(message);
        if(fromId!=null){
            result.setFromId(fromId);
        }
        return  JSON.toJSONString(result);
    }
}
