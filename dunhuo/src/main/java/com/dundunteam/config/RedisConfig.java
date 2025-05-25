package com.dundunteam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /**
     * 配置 RedisTemplate，用于操作 Redis 中的键值对。
     * 避免数据多次序列化，统一序列化策略。
     *
     * @param redisConnectionFactory Redis 连接工厂
     * @return 配置好的 RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // 设置 Redis 连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 设置 Key 和 HashKey 的序列化器为字符串序列化器
        redisTemplate.setKeySerializer(stringRedisSerializer());
        redisTemplate.setHashKeySerializer(stringRedisSerializer());

        // 设置 Value 和 HashValue 的序列化器为 JSON 序列化器
        redisTemplate.setValueSerializer(jsonRedisSerializer());
        redisTemplate.setHashValueSerializer(jsonRedisSerializer());

        // 初始化 RedisTemplate
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * 配置 StringRedisTemplate，用于操作 Redis 中的字符串类型数据。
     *
     * @param redisConnectionFactory Redis 连接工厂
     * @return 配置好的 StringRedisTemplate
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();

        // 设置 Redis 连接工厂
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory);

        // 设置 Key 和 Value 的序列化器为字符串序列化器
        stringRedisTemplate.setKeySerializer(stringRedisSerializer());
        stringRedisTemplate.setValueSerializer(stringRedisSerializer());

        return stringRedisTemplate;
    }

    /**
     * 配置字符串序列化器，统一处理 Key 和简单 Value。
     *
     * @return StringRedisSerializer 实例
     */
    private RedisSerializer<String> stringRedisSerializer() {
        return new StringRedisSerializer();
    }

    /**
     * 配置 JSON 序列化器，统一处理复杂对象的 Value。
     *
     * @return GenericJackson2JsonRedisSerializer 实例
     */
    private RedisSerializer<Object> jsonRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }
}
