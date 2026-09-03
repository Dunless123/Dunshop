package com.example.schoolmarket.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Goods {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String images;
    private Long categoryId;
    private String tags;
    private Long campusId;
    private Long sellerId;
    private String quality;
    private String tradeMethods;
    private Integer viewCount;
    private Integer favoriteCount;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}