package com.dundunteam.service.impl;

import com.dundunteam.common.context.MessageStatus;
import com.dundunteam.pojo.entity.BaseMessage;
import com.dundunteam.service.MessageService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 消息服务实现类
 * - 基于Redis实现消息存储
 * - 提供历史消息分页查询
 */
@Service
public class MessageServiceImpl implements MessageService {

    private static final String HISTORY_KEY_PREFIX = "history:"; //历史消息Key前缀
    private static final String OFFLINE_KEY_PREFIX = "offline:"; //离线消息Key前缀
    private static final int HISTORY_EXPIRE_DAYS = 30; //历史消息保留天数

    private final RedisTemplate<String, Object> redisTemplate;

    // 通过构造器注入Redis模板
    public MessageServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void saveMessage(BaseMessage message, String sessionId) {
        String key = HISTORY_KEY_PREFIX + sessionId;
        // 使用List结构存储消息（天然时序性）
        redisTemplate.opsForList().rightPush(key, message);
        // 设置过期时间（自动清理旧数据）
        redisTemplate.expire(key, HISTORY_EXPIRE_DAYS, TimeUnit.DAYS);
    }

    @Override
    public void storeOfflineMessage(String receiverOpenId, BaseMessage message) {
        String key = "offline:" + receiverOpenId;
        // 使用List结构存储消息（保留时序）
        redisTemplate.opsForList().rightPush(key, message);
        // 设置7过期时间

        redisTemplate.expire(key, 7, TimeUnit.DAYS);


}

    @Override
    public List<BaseMessage> getOfflineMessages(String receiverOpenId) {
        String key = "offline:" + receiverOpenId;
        // 获取所有离线消息（原子操作）
        List<Object> messages = redisTemplate.opsForList().range(key, 0, -1);
        // 清空队列
        redisTemplate.delete(key);
        // 类型转换
        return messages.stream()
                .map(obj -> (BaseMessage) obj)
                .collect(Collectors.toList());
    }

    @Override
    public List<BaseMessage> getHistoryMessages(String sessionId, int page, int size) {
        String key = HISTORY_KEY_PREFIX + sessionId;
        // 计算分页范围（Redis List索引从0开始）
        long start = (page - 1) * size;
        long end = start + size - 1;
        // 获取区间数据并转换类型
        return redisTemplate.opsForList().range(key, start, end)
                .stream()
                .map(obj -> (BaseMessage) obj)
                .collect(Collectors.toList());
    }

    @Override
    public void updateMessageStatus(String messageId, MessageStatus status) {
        // 更新 Redis 中消息状态（假设消息存储在 Hash 结构）
        String key = "message:" + messageId;
        redisTemplate.opsForHash().put(key, "status", status.name());

        // 如果使用数据库，添加类似逻辑：
        // messageRepository.updateStatus(messageId, status);
    }

    /**
     * 获取消息状态（扩展方法）
     */
    public MessageStatus getMessageStatus(String messageId) {
        String key = "message:" + messageId;
        String status = (String) redisTemplate.opsForHash().get(key, "status");
        return status != null ? MessageStatus.valueOf(status) : MessageStatus.SENT;
    }

    @Override
    public boolean isDuplicateMessage(String messageId) {
        String key = "msg:" + messageId;
        // 使用setIfAbsent实现分布式锁（5分钟有效期）
        return Boolean.FALSE.equals(
                redisTemplate.opsForValue().setIfAbsent(key, "1", 5, TimeUnit.MINUTES)
        );
    }

    @Override
    public int processOfflineMessages(String openId) {
        String key = OFFLINE_KEY_PREFIX + openId;
        // 获取并清空离线消息
        Long count = redisTemplate.opsForList().size(key);
        if (count == null || count == 0) return 0;
        redisTemplate.delete(key);
        return count.intValue();
    }
}