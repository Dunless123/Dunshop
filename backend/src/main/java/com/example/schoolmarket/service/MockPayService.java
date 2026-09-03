package com.example.schoolmarket.service;

import java.util.Map;

public interface MockPayService {
    Map<String, Object> createOrder(Long orderId);
    Map<String, Object> confirmPayment(Long orderId, Long userId, String payPassword);
    Map<String, Object> cancelPayment(Long orderId);
    Map<String, Object> refundOrder(Long orderId, String reason);
    String queryStatus(Long orderId);
}