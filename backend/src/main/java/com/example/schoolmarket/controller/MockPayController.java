package com.example.schoolmarket.controller;

import com.example.schoolmarket.service.MockPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/mock/pay")
public class MockPayController {

    @Autowired
    private MockPayService mockPayService;

    @PostMapping("/create")
    public Map<String, Object> createOrder(@RequestBody Map<String, Object> params) {
        Long orderId = ((Number) params.get("orderId")).longValue();
        return mockPayService.createOrder(orderId);
    }

    @PostMapping("/confirm")
    public Map<String, Object> confirmPayment(@RequestBody Map<String, Object> params) {
        Long orderId = ((Number) params.get("orderId")).longValue();
        Long userId = ((Number) params.get("userId")).longValue();
        String payPassword = (String) params.get("payPassword");
        return mockPayService.confirmPayment(orderId, userId, payPassword);
    }

    @PostMapping("/cancel")
    public Map<String, Object> cancelPayment(@RequestBody Map<String, Object> params) {
        Long orderId = ((Number) params.get("orderId")).longValue();
        return mockPayService.cancelPayment(orderId);
    }

    @PostMapping("/refund")
    public Map<String, Object> refundOrder(@RequestBody Map<String, Object> params) {
        Long orderId = ((Number) params.get("orderId")).longValue();
        String reason = (String) params.getOrDefault("reason", "用户申请退款");
        return mockPayService.refundOrder(orderId, reason);
    }

    @GetMapping("/status")
    public Map<String, Object> queryStatus(@RequestParam Long orderId) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("status", mockPayService.queryStatus(orderId));
        return result;
    }

    @GetMapping("/info")
    public Map<String, Object> getInfo() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("mock", true);
        result.put("message", "模拟支付环境已启用");
        result.put("features", new String[]{
            "创建支付订单",
            "确认支付（含交易密码验证）",
            "取消支付",
            "申请退款",
            "查询支付状态"
        });
        return result;
    }
}