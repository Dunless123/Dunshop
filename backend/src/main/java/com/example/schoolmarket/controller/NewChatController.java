package com.example.schoolmarket.controller;

import com.example.schoolmarket.entity.ChatMessage;
import com.example.schoolmarket.entity.ChatSession;
import com.example.schoolmarket.entity.User;
import com.example.schoolmarket.mapper.UserMapper;
import com.example.schoolmarket.service.ChatMessageService;
import com.example.schoolmarket.service.ChatSessionService;
import com.example.schoolmarket.utils.JwtUtil;
import com.example.schoolmarket.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class NewChatController {

    @Autowired
    private ChatSessionService chatSessionService;

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private Long getUserIdFromToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        String token = authorization.replace("Bearer ", "");
        return jwtUtil.getUserIdFromToken(token);
    }

    @GetMapping("/sessions")
    public Map<String, Object> getSessions(@RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }

        List<ChatSession> sessions = chatSessionService.getSessionsByUserId(userId);
        
        List<Map<String, Object>> sessionList = new java.util.ArrayList<>();
        for (ChatSession session : sessions) {
            Map<String, Object> item = new HashMap<>();
            item.put("sessionId", session.getId());
            
            Long otherUserId = session.getUserId1().equals(userId) ? session.getUserId2() : session.getUserId1();
            User otherUser = userMapper.selectById(otherUserId);
            
            item.put("otherUserId", otherUserId);
            item.put("otherUserName", otherUser != null ? otherUser.getUsername() : "未知");
            item.put("otherUserAvatar", otherUser != null ? otherUser.getAvatar() : "");
            item.put("lastMessage", session.getLastMessage());
            
            int unreadCount = session.getUserId1().equals(userId) ? session.getUnreadCount1() : session.getUnreadCount2();
            item.put("unreadCount", unreadCount);
            item.put("updatedAt", session.getUpdatedAt());
            
            sessionList.add(item);
        }

        return ResponseUtil.success(sessionList, "获取成功");
    }

    @GetMapping("/messages")
    public Map<String, Object> getMessages(
            @RequestParam Long sessionId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }

        int offset = (page - 1) * pageSize;
        List<ChatMessage> messages = chatMessageService.getMessages(sessionId, offset, pageSize);
        int total = chatMessageService.getMessageCount(sessionId);

        chatMessageService.markAsRead(sessionId, userId);

        Map<String, Object> data = new HashMap<>();
        data.put("list", messages);
        data.put("total", total);
        data.put("page", page);
        data.put("pageSize", pageSize);
        data.put("totalPages", (total + pageSize - 1) / pageSize);

        return ResponseUtil.success(data, "获取成功");
    }

    @PostMapping("/send")
    public Map<String, Object> sendMessage(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }

        Long receiverId = ((Number) body.get("receiverId")).longValue();
        String content = (String) body.get("content");

        if (userId.equals(receiverId)) {
            return ResponseUtil.error(400, "不能发送消息给自己");
        }

        ChatSession session = chatSessionService.createOrGetSession(userId, receiverId);
        ChatMessage message = chatMessageService.sendMessage(session.getId(), userId, receiverId, content);

        return ResponseUtil.success(message, "发送成功");
    }

    @GetMapping("/session")
    public Map<String, Object> getSession(
            @RequestParam Long otherUserId,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }

        ChatSession session = chatSessionService.createOrGetSession(userId, otherUserId);

        Map<String, Object> data = new HashMap<>();
        data.put("sessionId", session.getId());
        
        Long targetUserId = session.getUserId1().equals(userId) ? session.getUserId2() : session.getUserId1();
        User otherUser = userMapper.selectById(targetUserId);
        data.put("otherUserId", targetUserId);
        data.put("otherUserName", otherUser != null ? otherUser.getUsername() : "未知");
        data.put("otherUserAvatar", otherUser != null ? otherUser.getAvatar() : "");

        return ResponseUtil.success(data, "获取成功");
    }
}