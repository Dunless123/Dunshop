package com.example.schoolmarket.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PayPassword {
    private Long id;
    private Long userId;
    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}