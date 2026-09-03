package com.example.schoolmarket.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;
    private Long userId;
    private Long goodsId;
    private Long orderId;
    private Integer rating;
    private String content;
    private String images;
    private String reply;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    private String username;
    private String avatar;
    private String goodsTitle;
    private String goodsImages;
    private String type;
    private String status;
    private String auditComment;
}