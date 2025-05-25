package com.dundunteam.service;

import com.dundunteam.pojo.entity.BaseMessage;

import java.util.List;


    public interface ChatService {
        void sendMessage(BaseMessage message);
        List<String> getActiveSessions(String openId);
        void markMessageRead(String messageId);
    }

