package com.example.schoolmarket.mapper;

import com.example.schoolmarket.entity.ChatSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatSessionMapper {
    int insert(ChatSession session);
    int update(ChatSession session);
    ChatSession selectById(Long id);
    ChatSession selectByUserIds(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    List<ChatSession> selectByUserId(Long userId);
    int deleteById(Long id);
    int incrementUnreadCount(@Param("sessionId") Long sessionId, @Param("userId") Long userId);
    int clearUnreadCount(@Param("sessionId") Long sessionId, @Param("userId") Long userId);
}