package com.example.schoolmarket.service.impl;

import com.example.schoolmarket.entity.ChatMessage;
import com.example.schoolmarket.mapper.ChatMessageMapper;
import com.example.schoolmarket.service.ChatMessageService;
import com.example.schoolmarket.service.ChatSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.HashMap;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private ChatSessionService chatSessionService;

    @Override
    @Transactional
    public ChatMessage sendMessage(Long sessionId, Long senderId, Long receiverId, String content) {
        ChatMessage message = new ChatMessage();
        message.setSessionId(sessionId);
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setStatus(1);
        message.setMessageType(0);
        
        chatMessageMapper.insert(message);
        
        chatSessionService.updateLastMessage(sessionId, content);
        chatSessionService.incrementUnreadCount(sessionId, receiverId);
        
        return message;
    }

    @Override
    public List<ChatMessage> getMessages(Long sessionId, int offset, int pageSize) {
        return chatMessageMapper.selectBySessionId(sessionId, offset, pageSize);
    }

    @Override
    public int getMessageCount(Long sessionId) {
        return chatMessageMapper.countBySessionId(sessionId);
    }

    @Override
    @Transactional
    public void markAsRead(Long sessionId, Long receiverId) {
        chatMessageMapper.updateStatusBySessionAndReceiver(sessionId, receiverId, 3);
        chatSessionService.clearUnreadCount(sessionId, receiverId);
    }

    @Override
    @Transactional
    public void updateMessageStatus(Long messageId, Integer status) {
        chatMessageMapper.updateStatus(messageId, status);
    }

    @Override
    public int countAll() {
        return chatMessageMapper.countAll();
    }

    @Override
    public List<HashMap<String, Object>> getMessageTrend(String startDate, String endDate) {
        return chatMessageMapper.getMessageTrend(startDate, endDate);
    }
}