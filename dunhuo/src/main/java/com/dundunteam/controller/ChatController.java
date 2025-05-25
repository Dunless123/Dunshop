package com.dundunteam.controller;


import com.dundunteam.pojo.entity.BaseMessage;
import com.dundunteam.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * 发送消息（支持离线存储）
     * @param message 消息体
     * @return 消息发送结果
     */
    @PostMapping("/messages")
    public ResponseEntity<String> sendMessage(@RequestBody BaseMessage message) {
        chatService.sendMessage(message);
        return ResponseEntity.ok("消息已接收");
    }

    /**
     * 获取会话列表
     * @param openId 用户唯一标识
     * @return 活跃会话列表
     */
    @GetMapping("/sessions/{openId}")
    public ResponseEntity<List<String>> getSessions(@PathVariable String openId) {
        return ResponseEntity.ok(chatService.getActiveSessions(openId));
    }

    /**
     * 标记消息已读
     * @param messageId 消息ID
     */
    @PutMapping("/messages/{messageId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable String messageId) {
        chatService.markMessageRead(messageId);
        return ResponseEntity.noContent().build();
    }
}