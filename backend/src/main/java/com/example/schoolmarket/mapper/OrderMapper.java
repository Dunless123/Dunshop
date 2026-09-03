package com.example.schoolmarket.mapper;

import com.example.schoolmarket.entity.Order;
import java.util.List;
import java.util.Map;

public interface OrderMapper {
    List<Order> selectByBuyerId(Map<String, Object> params);
    int countByBuyerId(Map<String, Object> params);
    List<Order> selectBySellerId(Map<String, Object> params);
    int countBySellerId(Map<String, Object> params);
    List<Order> selectOrderList();
    int countOrderList();
    Order selectById(Long id);
    int insert(Order order);
    int update(Order order);
    int delete(Long id);
    double sumSalesByUserId(Map<String, Object> params);
    int countTodayOrdersByUserId(Map<String, Object> params);
    double sumTodaySalesByUserId(Map<String, Object> params);
    List<Order> selectAllOrders(Map<String, Object> params);
    int countAllOrders(Map<String, Object> params);
    Order selectByOrderNo(String orderNo);
    List<Order> selectByGoodsId(Map<String, Object> params);
}