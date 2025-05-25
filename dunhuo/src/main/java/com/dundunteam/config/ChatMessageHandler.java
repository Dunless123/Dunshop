package com.dundunteam.config;

import com.dundunteam.common.context.MessageStatus;
import com.dundunteam.pojo.entity.BaseMessage;
import com.dundunteam.service.MessageService;
import com.dundunteam.service.SessionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket消息处理器
 * - 处理实时消息收发
 * - 管理用户在线状态
 * - 集成离线消息存储
 */
@Component
public class ChatMessageHandler extends TextWebSocketHandler {

    // 在线用户会话存储（Key: openId）
    private static final Map<String, WebSocketSession> onlineSessions = new ConcurrentHashMap<>();

    // 依赖服务
    private final SessionService sessionService;
    private final MessageService messageService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    // 通过构造器注入依赖
    public ChatMessageHandler(SessionService sessionService,
                              MessageService messageService,
                              RedisTemplate<String, Object> redisTemplate,
                              ObjectMapper objectMapper) {
        this.sessionService = sessionService;
        this.messageService = messageService;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 连接建立成功回调
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 从会话属性中获取openId（由AuthInterceptor绑定）
        String openId = getOpenIdFromSession(session);

        // 1. 添加到在线列表
        onlineSessions.put(openId, session);

        // 2. 标记用户在线状态
        redisTemplate.opsForValue().set("online:" + openId, "1");

        // 3. 处理离线消息
        processOfflineMessages(openId);

        System.out.println("用户连接成功: " + openId + ", 当前在线数: " + onlineSessions.size());
    }

    /**
     * 处理接收到的文本消息
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            // 1. 解析消息
            BaseMessage msg = parseMessage(message.getPayload());
            msg.setSenderOpenId(getOpenIdFromSession(session));

            // 2. 消息去重校验
            if (messageService.isDuplicateMessage(msg.getMessageId())) {
                System.out.println("检测到重复消息: " + msg.getMessageId());
                return;
            }

            // 3. 创建/更新会话
            String sessionId = sessionService.createOrGetSession(
                    msg.getSenderOpenId(),
                    msg.getReceiverOpenId()
            );
            sessionService.updateSessionActivity(sessionId);

            // 4. 持久化消息
            messageService.saveMessage(msg, sessionId);

            // 5. 实时投递或离线存储
            deliverOrStoreMessage(msg);

        } catch (Exception e) {
            System.err.println("消息处理异常: " + e.getMessage());
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    /**
     * 连接关闭回调
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String openId = getOpenIdFromSession(session);

        // 1. 移除在线记录
        onlineSessions.remove(openId);

        // 2. 清除在线标记
        redisTemplate.delete("online:" + openId);

        System.out.println("用户断开连接: " + openId + ", 原因: " + status.getReason());
    }

    /**
     * 消息投递逻辑
     */
    private void processOfflineMessages(String openId) {
        List<BaseMessage> messages = messageService.getOfflineMessages(openId);
        WebSocketSession session = onlineSessions.get(openId);
        if (session != null && !messages.isEmpty()) {
            messages.forEach(msg -> {
                try {
                    // 重新发送离线消息
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(msg)));
                    // 更新消息状态为已送达
                    messageService.updateMessageStatus(msg.getMessageId(), MessageStatus.DELIVERED);
                } catch (IOException e) {
                    System.err.println("离线消息发送失败: " + e.getMessage());
                }
            });
        }
    }

    /**
     * 消息投递逻辑
     */
    private void deliverOrStoreMessage(BaseMessage msg) throws IOException {
        WebSocketSession targetSession = onlineSessions.get(msg.getReceiverOpenId());
        if (targetSession != null && targetSession.isOpen()) {
            // 在线：直接发送
            targetSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(msg)));
            // 标记已送达
            messageService.updateMessageStatus(msg.getMessageId(), MessageStatus.DELIVERED);
        } else {
            // 离线：存储消息并保持SENT状态
            messageService.storeOfflineMessage(msg.getReceiverOpenId(), msg);
        }
    }

    /**
     * 从会话属性获取openId
     */
    private String getOpenIdFromSession(WebSocketSession session) {
        return (String) session.getAttributes().get("openId"); // AuthInterceptor中设置
    }

    /**
     * 反序列化消息
     */
    private BaseMessage parseMessage(String payload) throws IOException {
        BaseMessage msg = objectMapper.readValue(payload, BaseMessage.class);
        if (msg.getReceiverOpenId() == null || msg.getContent() == null) {
            throw new IllegalArgumentException("消息格式错误");
        }
        return msg;
    }
}