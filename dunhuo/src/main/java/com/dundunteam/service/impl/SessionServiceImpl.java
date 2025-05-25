package com.dundunteam.service.impl;



import com.dundunteam.service.SessionService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 会话服务实现类
 * - 使用Redis存储会话数据
 * - 每个会话默认保留7天
 */
@Service
public class SessionServiceImpl implements SessionService {

    private static final String SESSION_PREFIX = "session:"; // Redis键前缀
    private static final long SESSION_TTL_HOURS = 7 * 24;    // 会话有效期（7天）

    private final RedisTemplate<String, String> redisTemplate;

    public SessionServiceImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String createOrGetSession(String user1, String user2) {
        // 1. 生成有序的会话键（保证user1和user2顺序不影响结果）
        String[] users = {user1, user2};
        Arrays.sort(users); // 排序用户ID
        String sessionKey = String.join("_", users);

        // 2. 生成MD5作为唯一会话ID
        String sessionId = DigestUtils.md5DigestAsHex(sessionKey.getBytes());
        String redisKey = SESSION_PREFIX + sessionId;

        // 3. 如果会话不存在，初始化会话数据
        if (Boolean.FALSE.equals(redisTemplate.hasKey(redisKey))) {
            Map<String, String> sessionData = new HashMap<>();
            sessionData.put("user1", users[0]);
            sessionData.put("user2", users[1]);
            sessionData.put("createdAt", String.valueOf(System.currentTimeMillis()));

            // 存储到Redis并设置过期时间
            redisTemplate.opsForHash().putAll(redisKey, sessionData);
            redisTemplate.expire(redisKey, SESSION_TTL_HOURS, TimeUnit.HOURS);
        }

        return sessionId;
    }

    @Override
    public void updateSessionActivity(String sessionId) {
        String redisKey = SESSION_PREFIX + sessionId;
        // 更新过期时间（每次操作后重置TTL）
        redisTemplate.expire(redisKey, SESSION_TTL_HOURS, TimeUnit.HOURS);
    }

    @Override
    public List<String> getActiveSessions(String openId) {
        List<String> sessions = new ArrayList<>();
        // 扫描所有以 session: 开头的键
        Set<String> keys = redisTemplate.keys(SESSION_PREFIX + "*");

        if (keys != null) {
            for (String key : keys) {
                // 检查会话是否包含当前用户
                Map<Object, Object> session = redisTemplate.opsForHash().entries(key);
                if (session.get("user1").equals(openId) || session.get("user2").equals(openId)) {
                    sessions.add(key.replace(SESSION_PREFIX, ""));
                }
            }
        }
        return sessions;
    }

    @Override
    public void deleteSession(String sessionId) {
        String redisKey = SESSION_PREFIX + sessionId;
        redisTemplate.delete(redisKey);
    }
}