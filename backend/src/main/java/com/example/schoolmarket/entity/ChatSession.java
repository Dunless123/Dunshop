package com.example.schoolmarket.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatSession {
    private Long id;
    private Long userId1;
    private Long userId2;
    private String lastMessage;
    private Integer unreadCount1;
    private Integer unreadCount2;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}