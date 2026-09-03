package com.example.schoolmarket.service.impl;

import com.example.schoolmarket.entity.Goods;
import com.example.schoolmarket.entity.Order;
import com.example.schoolmarket.mapper.GoodsMapper;
import com.example.schoolmarket.mapper.OrderMapper;
import com.example.schoolmarket.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Order createOrder(Order order) {
        order.setOrderNo(UUID.randomUUID().toString().replace("-", ""));
        order.setStatus("待支付");
        orderMapper.insert(order);
        // 更新商品状态为已售
        Goods goods = goodsMapper.selectById(order.getGoodsId());
        if (goods != null) {
            goods.setStatus("已售");
            goodsMapper.update(goods);
        }
        return order;
    }

    @Override
    public boolean payOrder(Long orderId) {
        Order order = getById(orderId);
        if (order != null) {
            order.setStatus("待发货");
            boolean updated = orderMapper.update(order) > 0;
            if (updated) {
                Goods goods = goodsMapper.selectById(order.getGoodsId());
                if (goods != null) {
                    goods.setStatus("已售出");
                    goodsMapper.update(goods);
                }
            }
            return updated;
        }
        return false;
    }

    @Override
    public boolean cancelOrder(Long orderId) {
        Order order = getById(orderId);
        if (order != null) {
            order.setStatus("已取消");
            boolean updated = orderMapper.update(order) > 0;
            // 恢复商品状态为在售
            Goods goods = goodsMapper.selectById(order.getGoodsId());
            if (goods != null) {
                goods.setStatus("在售");
                goodsMapper.update(goods);
            }
            return updated;
        }
        return false;
    }

    @Override
    public boolean confirmOrder(Long orderId) {
        Order order = getById(orderId);
        if (order != null) {
            order.setStatus("已完成");
            return orderMapper.update(order) > 0;
        }
        return false;
    }

    @Override
    public Order getById(Long id) {
        return orderMapper.selectById(id);
    }

    @Override
    public boolean save(Order order) {
        return orderMapper.insert(order) > 0;
    }

    @Override
    public boolean update(Order order) {
        return orderMapper.update(order) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return orderMapper.delete(id) > 0;
    }

    @Override
    public List<Order> list() {
        return orderMapper.selectOrderList();
    }

    @Override
    public boolean shipOrder(Long orderId) {
        Order order = getById(orderId);
        if (order != null) {
            order.setStatus("待收货");
            return orderMapper.update(order) > 0;
        }
        return false;
    }

    @Override
    public int getTotalOrdersByUserId(Long userId, String type) {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        if (userId != null) {
            if ("buyer".equals(type)) {
                params.put("buyerId", userId);
                return orderMapper.countByBuyerId(params);
            } else if ("seller".equals(type)) {
                params.put("sellerId", userId);
                return orderMapper.countBySellerId(params);
            }
        }
        return orderMapper.countOrderList();
    }

    @Override
    public int getPendingOrdersByUserId(Long userId, String type) {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("status", "待支付");
        if (userId != null) {
            if ("buyer".equals(type)) {
                params.put("buyerId", userId);
                return orderMapper.countByBuyerId(params);
            } else if ("seller".equals(type)) {
                params.put("sellerId", userId);
                return orderMapper.countBySellerId(params);
            }
        }
        return orderMapper.countOrderList();
    }

    @Override
    public int getCompletedOrdersByUserId(Long userId, String type) {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("status", "已完成");
        if (userId != null) {
            if ("buyer".equals(type)) {
                params.put("buyerId", userId);
                return orderMapper.countByBuyerId(params);
            } else if ("seller".equals(type)) {
                params.put("sellerId", userId);
                return orderMapper.countBySellerId(params);
            }
        }
        return orderMapper.countOrderList();
    }

    @Override
    public double getTotalSalesByUserId(Long userId, String type) {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        if (userId != null) {
            if ("buyer".equals(type)) {
                params.put("buyerId", userId);
            } else if ("seller".equals(type)) {
                params.put("sellerId", userId);
            }
        }
        return orderMapper.sumSalesByUserId(params);
    }

    @Override
    public double getAverageOrderValueByUserId(Long userId, String type) {
        double totalSales = getTotalSalesByUserId(userId, type);
        int totalOrders = getTotalOrdersByUserId(userId, type);
        return totalOrders > 0 ? totalSales / totalOrders : 0;
    }

    @Override
    public int getTodayOrdersByUserId(Long userId, String type) {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        if (userId != null) {
            if ("buyer".equals(type)) {
                params.put("buyerId", userId);
            } else if ("seller".equals(type)) {
                params.put("sellerId", userId);
            }
        }
        return orderMapper.countTodayOrdersByUserId(params);
    }

    @Override
    public double getTodaySalesByUserId(Long userId, String type) {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        if (userId != null) {
            if ("buyer".equals(type)) {
                params.put("buyerId", userId);
            } else if ("seller".equals(type)) {
                params.put("sellerId", userId);
            }
        }
        return orderMapper.sumTodaySalesByUserId(params);
    }

    // 添加统计所有订单的方法
    public int getTotalOrders() {
        return orderMapper.countOrderList();
    }

    public int getPendingOrders() {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("status", "待支付");
        return orderMapper.countBySellerId(params);
    }

    public double getTotalSales() {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        return orderMapper.sumSalesByUserId(params);
    }

    public int getTodayOrders() {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        return orderMapper.countTodayOrdersByUserId(params);
    }

    public double getTodaySales() {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        return orderMapper.sumTodaySalesByUserId(params);
    }

    @Override
    public boolean updateStatus(Long orderId, String status) {
        Order order = getById(orderId);
        if (order != null) {
            order.setStatus(status);
            if ("已完成".equals(status)) {
                Goods goods = goodsMapper.selectById(order.getGoodsId());
                if (goods != null) {
                    goods.setStatus("已售");
                    goodsMapper.update(goods);
                }
            }
            return orderMapper.update(order) > 0;
        }
        return false;
    }

    @Override
    public boolean refundOrder(Long orderId) {
        Order order = getById(orderId);
        if (order != null && ("待发货".equals(order.getStatus()) || "待收货".equals(order.getStatus()))) {
            order.setStatus("已退款");
            boolean updated = orderMapper.update(order) > 0;
            Goods goods = goodsMapper.selectById(order.getGoodsId());
            if (goods != null) {
                goods.setStatus("在售");
                goodsMapper.update(goods);
            }
            return updated;
        }
        return false;
    }

    @Override
    public List<Order> getList(Long userId, int offset, int pageSize, String status, String keyword) {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("offset", offset);
        params.put("pageSize", pageSize);
        params.put("status", status);
        params.put("keyword", keyword);
        if (userId != null) {
            params.put("buyerId", userId);
        }
        return orderMapper.selectAllOrders(params);
    }

    @Override
    public int getCount(Long userId, String status, String keyword) {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("status", status);
        params.put("keyword", keyword);
        if (userId != null) {
            params.put("buyerId", userId);
        }
        return orderMapper.countAllOrders(params);
    }

    @Override
    public List<Order> getSellerList(Long sellerId, int offset, int pageSize, String status, String keyword) {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("sellerId", sellerId);
        params.put("offset", offset);
        params.put("pageSize", pageSize);
        params.put("status", status);
        params.put("keyword", keyword);
        return orderMapper.selectBySellerId(params);
    }

    @Override
    public int getSellerCount(Long sellerId, String status, String keyword) {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("sellerId", sellerId);
        params.put("status", status);
        params.put("keyword", keyword);
        return orderMapper.countBySellerId(params);
    }

    @Override
    public List<Order> getOrdersByGoodsId(Long goodsId, String status) {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("goodsId", goodsId);
        params.put("status", status);
        return orderMapper.selectByGoodsId(params);
    }
}