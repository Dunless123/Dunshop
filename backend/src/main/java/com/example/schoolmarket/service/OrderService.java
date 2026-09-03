package com.example.schoolmarket.service;

import com.example.schoolmarket.entity.Order;

import java.util.List;

public interface OrderService {
    Order getById(Long id);
    boolean save(Order order);
    boolean update(Order order);
    boolean delete(Long id);
    Order createOrder(Order order);
    boolean payOrder(Long orderId);
    boolean cancelOrder(Long orderId);
    boolean confirmOrder(Long orderId);
    boolean shipOrder(Long orderId);
    List<Order> list();
    int getTotalOrdersByUserId(Long userId, String type);
    int getPendingOrdersByUserId(Long userId, String type);
    int getCompletedOrdersByUserId(Long userId, String type);
    double getTotalSalesByUserId(Long userId, String type);
    double getAverageOrderValueByUserId(Long userId, String type);
    int getTodayOrdersByUserId(Long userId, String type);
    double getTodaySalesByUserId(Long userId, String type);
    int getTotalOrders();
    int getPendingOrders();
    double getTotalSales();
    int getTodayOrders();
    double getTodaySales();
    boolean updateStatus(Long orderId, String status);
    boolean refundOrder(Long orderId);
    List<Order> getList(Long userId, int offset, int pageSize, String status, String keyword);
    int getCount(Long userId, String status, String keyword);
    List<Order> getSellerList(Long sellerId, int offset, int pageSize, String status, String keyword);
    int getSellerCount(Long sellerId, String status, String keyword);
    List<Order> getOrdersByGoodsId(Long goodsId, String status);
}