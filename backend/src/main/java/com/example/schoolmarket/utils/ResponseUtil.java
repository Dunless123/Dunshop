package com.example.schoolmarket.utils;

import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class ResponseUtil {
    public static HashMap<String, Object> success(Object data, String message) {
        HashMap<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", data);
        response.put("message", message);
        return response;
    }

    public static HashMap<String, Object> success(Object data) {
        return success(data, "操作成功");
    }

    public static HashMap<String, Object> success() {
        return success(null, "操作成功");
    }

    public static HashMap<String, Object> error(int code, String message) {
        HashMap<String, Object> response = new HashMap<>();
        response.put("code", code);
        response.put("data", null);
        response.put("message", message);
        return response;
    }

    public static HashMap<String, Object> error(String message) {
        return error(500, message);
    }
}