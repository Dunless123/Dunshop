package com.dundunteam.service.impl;

import com.dundunteam.common.context.MessageStatus;
import com.dundunteam.pojo.entity.BaseMessage;
import com.dundunteam.service.ChatService;
import com.dundunteam.service.MessageService;
import com.dundunteam.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    private final MessageService messageService;
    private final SessionService sessionService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public ChatServiceImpl(MessageService messageService,
                           SessionService sessionService,
                           RedisTemplate<String, Object> redisTemplate) {
        this.messageService = messageService;
        this.sessionService = sessionService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void sendMessage(BaseMessage message) {
        // 生成会话ID
        String sessionId = sessionService.createOrGetSession(
                message.getSenderOpenId(),
                message.getReceiverOpenId()
        );

        // 存储消息
        messageService.saveMessage(message, sessionId);

        // 实时推送或离线存储
        String receiverKey = "online:" + message.getReceiverOpenId();
        if (redisTemplate.hasKey(receiverKey)) {
            // 通过WebSocket推送
            redisTemplate.convertAndSend("chat_channel", message);
        } else {
            messageService.processOfflineMessages(message.getReceiverOpenId());
        }
    }

    @Override
    public List<String> getActiveSessions(String openId) {
        return sessionService.getActiveSessions(openId);
    }

    @Override
    public void markMessageRead(String messageId) {
        messageService.updateMessageStatus(messageId, MessageStatus.READ);
    }
}