package com.example.schoolmarket.service.impl;

import com.example.schoolmarket.entity.Goods;
import com.example.schoolmarket.entity.Order;
import com.example.schoolmarket.mapper.GoodsMapper;
import com.example.schoolmarket.mapper.OrderMapper;
import com.example.schoolmarket.service.MockPayService;
import com.example.schoolmarket.service.PayPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class MockPayServiceImpl implements MockPayService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private PayPasswordService payPasswordService;

    @Override
    public Map<String, Object> createOrder(Long orderId) {
        Map<String, Object> result = new HashMap<>();
        
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            result.put("success", false);
            result.put("message", "订单不存在");
            return result;
        }

        if (!"待支付".equals(order.getStatus())) {
            result.put("success", false);
            result.put("message", "订单状态不允许支付");
            return result;
        }

        String mockOrderNo = "MO" + System.currentTimeMillis();
        order.setOrderNo(mockOrderNo);
        orderMapper.update(order);

        result.put("success", true);
        result.put("message", "支付订单已创建");
        result.put("orderNo", mockOrderNo);
        result.put("amount", order.getPrice());
        result.put("mock", true);

        return result;
    }

    @Override
    public Map<String, Object> confirmPayment(Long orderId, Long userId, String payPassword) {
        Map<String, Object> result = new HashMap<>();

        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            result.put("success", false);
            result.put("message", "订单不存在");
            return result;
        }

        if (!"待支付".equals(order.getStatus())) {
            result.put("success", false);
            result.put("message", "订单状态不允许支付");
            return result;
        }

        Map<String, Object> verifyResult = payPasswordService.verifyPassword(userId, payPassword);
        if (!((Boolean) verifyResult.get("success"))) {
            return verifyResult;
        }

        order.setStatus("待发货");
        order.setPayTime(LocalDateTime.now());
        orderMapper.update(order);

        Goods goods = goodsMapper.selectById(order.getGoodsId());
        if (goods != null) {
            goods.setStatus("已售出");
            goodsMapper.update(goods);
        }

        result.put("success", true);
        result.put("message", "支付成功");
        result.put("orderId", orderId);
        result.put("transactionId", "TX" + System.currentTimeMillis());
        result.put("mock", true);

        return result;
    }

    @Override
    public Map<String, Object> cancelPayment(Long orderId) {
        Map<String, Object> result = new HashMap<>();

        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            result.put("success", false);
            result.put("message", "订单不存在");
            return result;
        }

        if (!"待支付".equals(order.getStatus())) {
            result.put("success", false);
            result.put("message", "订单状态不允许取消");
            return result;
        }

        order.setStatus("已取消");
        orderMapper.update(order);

        result.put("success", true);
        result.put("message", "支付已取消");
        result.put("orderId", orderId);
        result.put("mock", true);

        return result;
    }

    @Override
    public Map<String, Object> refundOrder(Long orderId, String reason) {
        Map<String, Object> result = new HashMap<>();

        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            result.put("success", false);
            result.put("message", "订单不存在");
            return result;
        }

        if (!"待发货".equals(order.getStatus()) && !"待收货".equals(order.getStatus())) {
            result.put("success", false);
            result.put("message", "订单状态不支持退款");
            return result;
        }

        order.setStatus("已退款");
        order.setRefundReason(reason);
        orderMapper.update(order);

        result.put("success", true);
        result.put("message", "退款成功");
        result.put("orderId", orderId);
        result.put("refundNo", "RF" + System.currentTimeMillis());
        result.put("amount", order.getPrice());
        result.put("reason", reason);
        result.put("mock", true);

        return result;
    }

    @Override
    public String queryStatus(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        return order != null ? order.getStatus() : "订单不存在";
    }
}