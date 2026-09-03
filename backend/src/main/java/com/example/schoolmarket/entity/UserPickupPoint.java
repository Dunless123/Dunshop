package com.example.schoolmarket.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserPickupPoint {
    private Long id;
    private Long userId;
    private Long pickupId;
    private LocalDateTime createTime;
}