package com.example.schoolmarket.controller;

import com.example.schoolmarket.service.GoodsService;
import com.example.schoolmarket.service.OrderService;
import com.example.schoolmarket.service.CommentService;
import com.example.schoolmarket.service.ChatMessageService;
import com.example.schoolmarket.utils.JwtUtil;
import com.example.schoolmarket.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private ChatMessageService chatMessageService;
    @Autowired
    private JwtUtil jwtUtil;

    private Long getUserIdFromToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        String token = authorization.replace("Bearer ", "");
        return jwtUtil.getUserIdFromToken(token);
    }

    @GetMapping("/dashboard")
    public HashMap<String, Object> getDashboard(@RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }

        // 构建统计数据
        HashMap<String, Object> data = new HashMap<>();
        
        // 获取商品统计数据
        int totalGoods = goodsService.getTotalGoodsBySellerId(userId);
        int totalViews = goodsService.getTotalViewsBySellerId(userId);
        
        // 获取订单统计数据
        int totalOrders = orderService.getTotalOrdersByUserId(userId, "seller");
        double totalSales = orderService.getTotalSalesByUserId(userId, "seller");
        int todayOrders = orderService.getTodayOrdersByUserId(userId, "seller");
        double todaySales = orderService.getTodaySalesByUserId(userId, "seller");
        
        data.put("totalGoods", totalGoods); // 总商品数
        data.put("totalOrders", totalOrders); // 总订单数
        data.put("totalSales", totalSales); // 总销售额
        data.put("totalViews", totalViews); // 总浏览量
        data.put("todayOrders", todayOrders); // 今日订单数
        data.put("todaySales", todaySales); // 今日销售额

        return ResponseUtil.success(data, "获取成功");
    }

    @GetMapping("/goods")
    public HashMap<String, Object> getGoodsStatistics(@RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }

        // 构建商品统计数据
        HashMap<String, Object> data = new HashMap<>();
        
        // 获取商品统计数据
        int totalGoods = goodsService.getTotalGoodsBySellerId(userId);
        int onSaleGoods = goodsService.getOnSaleGoodsBySellerId(userId);
        int soldGoods = goodsService.getSoldGoodsBySellerId(userId);
        int totalViews = goodsService.getTotalViewsBySellerId(userId);
        int totalFavorites = goodsService.getTotalFavoritesBySellerId(userId);
        
        data.put("totalGoods", totalGoods); // 商品总数
        data.put("onSaleGoods", onSaleGoods); // 在售商品数
        data.put("soldGoods", soldGoods); // 已售商品数
        data.put("totalViews", totalViews); // 总浏览量
        data.put("totalFavorites", totalFavorites); // 总收藏数

        return ResponseUtil.success(data, "获取成功");
    }

    @GetMapping("/orders")
    public HashMap<String, Object> getOrderStatistics(@RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }

        // 构建订单统计数据
        HashMap<String, Object> data = new HashMap<>();
        
        // 获取订单统计数据
        int totalOrders = orderService.getTotalOrdersByUserId(userId, "seller");
        int pendingOrders = orderService.getPendingOrdersByUserId(userId, "seller");
        int completedOrders = orderService.getCompletedOrdersByUserId(userId, "seller");
        double totalSales = orderService.getTotalSalesByUserId(userId, "seller");
        double averageOrderValue = orderService.getAverageOrderValueByUserId(userId, "seller");
        
        data.put("totalOrders", totalOrders); // 总订单数
        data.put("pendingOrders", pendingOrders); // 待处理订单数
        data.put("completedOrders", completedOrders); // 已完成订单数
        data.put("totalSales", totalSales); // 总销售额
        data.put("averageOrderValue", averageOrderValue); // 平均订单价值

        return ResponseUtil.success(data, "获取成功");
    }

    @GetMapping("/interaction")
    public HashMap<String, Object> getInteractionStatistics(@RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate) {
        HashMap<String, Object> data = new HashMap<>();
        
        int messageCount = chatMessageService.countAll();
        int commentCount = commentService.countAll();
        int reviewCount = commentService.countAll();
        
        HashMap<String, Object> ratingStats = commentService.getRatingStatistics();
        List<HashMap<String, Object>> messageTrend = chatMessageService.getMessageTrend(startDate, endDate);
        
        data.put("messageCount", messageCount); // 消息发送量
        data.put("commentCount", commentCount); // 商品留言数
        data.put("reviewCount", reviewCount); // 评价数
        data.put("ratingStats", ratingStats); // 评价等级统计
        data.put("messageTrend", messageTrend); // 消息发送趋势
        
        return ResponseUtil.success(data, "获取成功");
    }
}