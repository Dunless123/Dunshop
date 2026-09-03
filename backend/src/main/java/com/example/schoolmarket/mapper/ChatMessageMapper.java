package com.example.schoolmarket.mapper;

import com.example.schoolmarket.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.HashMap;

@Mapper
public interface ChatMessageMapper {
    int insert(ChatMessage message);
    int update(ChatMessage message);
    ChatMessage selectById(Long id);
    List<ChatMessage> selectBySessionId(@Param("sessionId") Long sessionId, @Param("offset") int offset, @Param("pageSize") int pageSize);
    int countBySessionId(Long sessionId);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    int updateStatusBySessionAndReceiver(@Param("sessionId") Long sessionId, @Param("receiverId") Long receiverId, @Param("status") Integer status);
    int countAll();
    List<HashMap<String, Object>> getMessageTrend(@Param("startDate") String startDate, @Param("endDate") String endDate);
}