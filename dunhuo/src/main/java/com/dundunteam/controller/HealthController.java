package com.dundunteam.controller;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统健康检查控制器
 * - 检查Redis连接状态
 * - 监控服务健康状态
 */
@RestController
public class HealthController {

    private final RedisTemplate<String, Object> redisTemplate;

    // 通过构造器注入Redis模板
    public HealthController(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 服务健康检查端点
     * @return 包含各组件状态的JSON对象
     */
    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");

        // 检查Redis连接
        try {
            RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
            status.put("redis", connection.ping());
            connection.close();
        } catch (Exception e) {
            status.put("redis", "DOWN");
        }

        return status;
    }
}