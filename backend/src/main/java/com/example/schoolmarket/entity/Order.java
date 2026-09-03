package com.example.schoolmarket.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Order {
    private Long id;
    private String orderNo;
    private Long buyerId;
    private Long sellerId;
    private Long goodsId;
    private String goodsTitle;
    private String goodsImage;
    private BigDecimal price;
    private Long addressId;
    private String status;
    private String tradeMethod;
    private String goodsName;
    private LocalDateTime payTime;
    private String refundReason;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long pickupPointId;
    private String pickupPointName;
    private String pickupTime;
}