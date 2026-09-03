package com.example.schoolmarket.service;

import com.example.schoolmarket.entity.ChatSession;

import java.util.List;

public interface ChatSessionService {
    ChatSession createOrGetSession(Long userId1, Long userId2);
    ChatSession getSessionById(Long id);
    List<ChatSession> getSessionsByUserId(Long userId);
    void updateLastMessage(Long sessionId, String content);
    void incrementUnreadCount(Long sessionId, Long userId);
    void clearUnreadCount(Long sessionId, Long userId);
}