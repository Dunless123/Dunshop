package com.example.schoolmarket.service.impl;

import com.example.schoolmarket.entity.ChatSession;
import com.example.schoolmarket.mapper.ChatSessionMapper;
import com.example.schoolmarket.service.ChatSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class ChatSessionServiceImpl implements ChatSessionService {

    @Autowired
    private ChatSessionMapper chatSessionMapper;

    @Override
    @Transactional
    public ChatSession createOrGetSession(Long userId1, Long userId2) {
        ChatSession session = chatSessionMapper.selectByUserIds(userId1, userId2);
        if (session != null) {
            return session;
        }
        
        session = new ChatSession();
        if (userId1 < userId2) {
            session.setUserId1(userId1);
            session.setUserId2(userId2);
        } else {
            session.setUserId1(userId2);
            session.setUserId2(userId1);
        }
        session.setUnreadCount1(0);
        session.setUnreadCount2(0);
        chatSessionMapper.insert(session);
        
        return chatSessionMapper.selectByUserIds(userId1, userId2);
    }

    @Override
    public ChatSession getSessionById(Long id) {
        return chatSessionMapper.selectById(id);
    }

    @Override
    public List<ChatSession> getSessionsByUserId(Long userId) {
        return chatSessionMapper.selectByUserId(userId);
    }

    @Override
    @Transactional
    public void updateLastMessage(Long sessionId, String content) {
        ChatSession session = chatSessionMapper.selectById(sessionId);
        if (session != null) {
            session.setLastMessage(content);
            chatSessionMapper.update(session);
        }
    }

    @Override
    @Transactional
    public void incrementUnreadCount(Long sessionId, Long userId) {
        ChatSession session = chatSessionMapper.selectById(sessionId);
        if (session != null) {
            if (session.getUserId1().equals(userId)) {
                session.setUnreadCount1(session.getUnreadCount1() + 1);
            } else {
                session.setUnreadCount2(session.getUnreadCount2() + 1);
            }
            chatSessionMapper.update(session);
        }
    }

    @Override
    @Transactional
    public void clearUnreadCount(Long sessionId, Long userId) {
        ChatSession session = chatSessionMapper.selectById(sessionId);
        if (session != null) {
            if (session.getUserId1().equals(userId)) {
                session.setUnreadCount1(0);
            } else {
                session.setUnreadCount2(0);
            }
            chatSessionMapper.update(session);
        }
    }
}