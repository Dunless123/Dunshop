package com.example.schoolmarket.service;

import com.example.schoolmarket.entity.ChatMessage;

import java.util.List;
import java.util.HashMap;

public interface ChatMessageService {
    ChatMessage sendMessage(Long sessionId, Long senderId, Long receiverId, String content);
    List<ChatMessage> getMessages(Long sessionId, int offset, int pageSize);
    int getMessageCount(Long sessionId);
    void markAsRead(Long sessionId, Long receiverId);
    void updateMessageStatus(Long messageId, Integer status);
    int countAll();
    List<HashMap<String, Object>> getMessageTrend(String startDate, String endDate);
}