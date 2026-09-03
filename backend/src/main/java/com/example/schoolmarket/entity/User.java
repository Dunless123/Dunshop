package com.example.schoolmarket.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String studentId;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String avatar;
    private String openId;
    private Long campusId;
    private String role;
    private String status;
    private String authStatus;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}