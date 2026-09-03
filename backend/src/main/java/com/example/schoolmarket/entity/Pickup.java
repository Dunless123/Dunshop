package com.example.schoolmarket.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Pickup {
    private Long id;
    private String name;
    private Long campusId;
    private String campusName;
    private String address;
    private String phone;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}