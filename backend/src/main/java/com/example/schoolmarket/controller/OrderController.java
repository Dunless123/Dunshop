package com.example.schoolmarket.controller;

import com.example.schoolmarket.entity.Order;
import com.example.schoolmarket.entity.User;
import com.example.schoolmarket.service.CommentService;
import com.example.schoolmarket.service.OrderService;
import com.example.schoolmarket.service.UserService;
import com.example.schoolmarket.utils.JwtUtil;
import com.example.schoolmarket.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private JwtUtil jwtUtil;

    private Long getUserIdFromToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        String token = authorization.replace("Bearer ", "");
        return jwtUtil.getUserIdFromToken(token);
    }

    @GetMapping("/list")
    public HashMap<String, Object> getList(@RequestParam(required = false, defaultValue = "1") Integer page, 
                                           @RequestParam(required = false, defaultValue = "10") Integer pageSize, 
                                           @RequestParam(required = false) String status,
                                           @RequestParam(required = false) String keyword,
                                           @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }
        
        int offset = (page - 1) * pageSize;
        
        List<Order> orderList = orderService.getList(userId, offset, pageSize, status, keyword);
        int total = orderService.getCount(userId, status, keyword);
        
        List<HashMap<String, Object>> resultList = orderList.stream()
            .map(order -> {
                HashMap<String, Object> map = new HashMap<>();
                map.put("id", order.getId());
                map.put("orderNo", order.getOrderNo());
                map.put("buyerId", order.getBuyerId());
                map.put("sellerId", order.getSellerId());
                map.put("goodsId", order.getGoodsId());
                map.put("goodsTitle", order.getGoodsTitle());
                map.put("goodsImage", order.getGoodsImage());
                map.put("price", order.getPrice());
                map.put("addressId", order.getAddressId());
                map.put("status", order.getStatus());
                map.put("tradeMethod", order.getTradeMethod());
                map.put("createTime", order.getCreateTime());
                map.put("updateTime", order.getUpdateTime());
                
                User buyer = userService.getById(order.getBuyerId());
                map.put("username", buyer != null ? buyer.getUsername() : "未知用户");
                
                User seller = userService.getById(order.getSellerId());
                map.put("sellerUsername", seller != null ? seller.getUsername() : "未知卖家");
                map.put("sellerAvatar", seller != null ? seller.getAvatar() : "");
                
                int commentCount = commentService.getCountByOrderId(order.getId());
                map.put("hasComment", commentCount > 0);
                
                return map;
            })
            .toList();
        
        HashMap<String, Object> data = new HashMap<>();
        data.put("list", resultList);
        data.put("total", total);
        data.put("page", page);
        data.put("pageSize", pageSize);
        data.put("totalPages", (total + pageSize - 1) / pageSize);
        
        return ResponseUtil.success(data, "获取成功");
    }

    @PostMapping("/{id}/ship")
    public HashMap<String, Object> ship(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }
        Order order = orderService.getById(id);
        if (order != null && order.getSellerId().equals(userId)) {
            boolean success = orderService.shipOrder(id);
            if (success) {
                return ResponseUtil.success(null, "发货成功");
            }
        }
        return ResponseUtil.error(400, "发货失败");
    }

    @GetMapping("/{id}")
    public HashMap<String, Object> getDetail(@PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order != null) {
            return ResponseUtil.success(order, "获取成功");
        }
        return ResponseUtil.error(404, "订单不存在");
    }

    @PostMapping("/create")
    public HashMap<String, Object> create(@RequestBody Map<String, Object> params, @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }
        
        Order order = new Order();
        order.setBuyerId(userId);
        
        if (!params.containsKey("sellerId") || params.get("sellerId") == null) {
            return ResponseUtil.error(400, "卖家ID不能为空");
        }
        if (!params.containsKey("goodsId") || params.get("goodsId") == null) {
            return ResponseUtil.error(400, "商品ID不能为空");
        }
        if (!params.containsKey("addressId") || params.get("addressId") == null) {
            return ResponseUtil.error(400, "地址ID不能为空");
        }
        if (!params.containsKey("price") || params.get("price") == null) {
            return ResponseUtil.error(400, "价格不能为空");
        }
        
        Long sellerId = Long.parseLong(params.get("sellerId").toString());
        
        if (userId.equals(sellerId)) {
            return ResponseUtil.error(400, "不能购买自己的商品");
        }
        
        order.setSellerId(sellerId);
        order.setGoodsId(Long.parseLong(params.get("goodsId").toString()));
        order.setAddressId(Long.parseLong(params.get("addressId").toString()));
        order.setPrice(new java.math.BigDecimal(params.get("price").toString()));
        order.setStatus("待支付");
        
        Object goodsTitleObj = params.get("goodsTitle");
        if (goodsTitleObj instanceof String) {
            order.setGoodsTitle((String) goodsTitleObj);
        }
        
        Object goodsImageObj = params.get("goodsImage");
        if (goodsImageObj instanceof List) {
            List<String> goodsImageList = (List<String>) goodsImageObj;
            if (!goodsImageList.isEmpty()) {
                order.setGoodsImage(goodsImageList.get(0));
            }
        } else if (goodsImageObj instanceof String) {
            order.setGoodsImage((String) goodsImageObj);
        }
        
        Object tradeMethodObj = params.get("tradeMethod");
        if (tradeMethodObj instanceof List) {
            List<String> tradeMethodList = (List<String>) tradeMethodObj;
            if (!tradeMethodList.isEmpty()) {
                order.setTradeMethod(tradeMethodList.get(0));
            }
        } else if (tradeMethodObj instanceof String) {
            order.setTradeMethod((String) tradeMethodObj);
        }
        
        Order createdOrder = orderService.createOrder(order);
        return ResponseUtil.success(createdOrder, "创建成功");
    }

    @PostMapping("/{id}/pay")
    public HashMap<String, Object> pay(@PathVariable Long id) {
        boolean success = orderService.payOrder(id);
        if (success) {
            return ResponseUtil.success(null, "支付成功");
        }
        return ResponseUtil.error(400, "支付失败");
    }

    @PostMapping("/{id}/cancel")
    public HashMap<String, Object> cancel(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }
        Order order = orderService.getById(id);
        if (order != null && order.getBuyerId().equals(userId)) {
            boolean success = orderService.cancelOrder(id);
            if (success) {
                return ResponseUtil.success(null, "取消成功");
            }
        }
        return ResponseUtil.error(400, "取消失败");
    }

    @PostMapping("/{id}/confirm")
    public HashMap<String, Object> confirm(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }
        Order order = orderService.getById(id);
        if (order != null && order.getBuyerId().equals(userId)) {
            boolean success = orderService.confirmOrder(id);
            if (success) {
                return ResponseUtil.success(null, "确认收货成功");
            }
        }
        return ResponseUtil.error(400, "确认收货失败");
    }

    @PutMapping("/{id}/status")
    public HashMap<String, Object> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> params) {
        String status = params.get("status");
        if (status == null || status.isEmpty()) {
            return ResponseUtil.error(400, "状态不能为空");
        }
        boolean success = orderService.updateStatus(id, status);
        if (success) {
            return ResponseUtil.success(null, "状态更新成功");
        }
        return ResponseUtil.error(400, "状态更新失败");
    }

    @PostMapping("/{id}/refund")
    public HashMap<String, Object> refund(@PathVariable Long id) {
        boolean success = orderService.refundOrder(id);
        if (success) {
            return ResponseUtil.success(null, "退款成功");
        }
        return ResponseUtil.error(400, "退款失败");
    }

    @PutMapping("/{id}/update")
    public HashMap<String, Object> updateOrder(@PathVariable Long id, @RequestBody Map<String, Object> params, @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }
        
        Order order = orderService.getById(id);
        if (order == null) {
            return ResponseUtil.error(404, "订单不存在");
        }
        
        if (!order.getBuyerId().equals(userId)) {
            return ResponseUtil.error(403, "无权修改该订单");
        }
        
        if (!"待支付".equals(order.getStatus())) {
            return ResponseUtil.error(400, "只能修改待支付状态的订单");
        }
        
        if (params.containsKey("tradeMethod")) {
            order.setTradeMethod((String) params.get("tradeMethod"));
        }
        
        if (params.containsKey("addressId")) {
            Object addressIdObj = params.get("addressId");
            if (addressIdObj instanceof Long) {
                order.setAddressId((Long) addressIdObj);
            } else if (addressIdObj instanceof String) {
                order.setAddressId(Long.parseLong((String) addressIdObj));
            }
        }
        
        if (params.containsKey("pickupPointId")) {
            Object pickupPointIdObj = params.get("pickupPointId");
            if (pickupPointIdObj instanceof Long) {
                order.setPickupPointId((Long) pickupPointIdObj);
            } else if (pickupPointIdObj instanceof String) {
                order.setPickupPointId(Long.parseLong((String) pickupPointIdObj));
            }
        }
        
        if (params.containsKey("pickupPointName")) {
            order.setPickupPointName((String) params.get("pickupPointName"));
        }
        
        if (params.containsKey("pickupTime")) {
            order.setPickupTime((String) params.get("pickupTime"));
        }
        
        boolean success = orderService.update(order);
        if (success) {
            return ResponseUtil.success(null, "订单修改成功");
        }
        return ResponseUtil.error(400, "订单修改失败");
    }

    @PutMapping("/batch/status")
    public HashMap<String, Object> batchUpdateStatus(@RequestBody Map<String, Object> requestBody) {
        List<?> idsObj = (List<?>) requestBody.get("ids");
        String status = (String) requestBody.get("status");
        
        if (idsObj == null || idsObj.isEmpty()) {
            return ResponseUtil.error(400, "订单ID列表不能为空");
        }
        if (status == null || status.isEmpty()) {
            return ResponseUtil.error(400, "状态不能为空");
        }
        
        int successCount = 0;
        for (Object idObj : idsObj) {
            Long id = null;
            if (idObj instanceof Long) {
                id = (Long) idObj;
            } else if (idObj instanceof Integer) {
                id = ((Integer) idObj).longValue();
            } else if (idObj instanceof String) {
                id = Long.parseLong((String) idObj);
            }
            
            if (id != null && orderService.updateStatus(id, status)) {
                successCount++;
            }
        }
        
        HashMap<String, Object> data = new HashMap<>();
        data.put("successCount", successCount);
        data.put("totalCount", idsObj.size());
        return ResponseUtil.success(data, "批量更新完成");
    }

    @PostMapping("/batch/refund")
    public HashMap<String, Object> batchRefund(@RequestBody Map<String, Object> requestBody) {
        List<?> idsObj = (List<?>) requestBody.get("ids");
        
        if (idsObj == null || idsObj.isEmpty()) {
            return ResponseUtil.error(400, "订单ID列表不能为空");
        }
        
        int successCount = 0;
        for (Object idObj : idsObj) {
            Long id = null;
            if (idObj instanceof Long) {
                id = (Long) idObj;
            } else if (idObj instanceof Integer) {
                id = ((Integer) idObj).longValue();
            } else if (idObj instanceof String) {
                id = Long.parseLong((String) idObj);
            }
            
            if (id != null && orderService.refundOrder(id)) {
                successCount++;
            }
        }
        
        HashMap<String, Object> data = new HashMap<>();
        data.put("successCount", successCount);
        data.put("totalCount", idsObj.size());
        return ResponseUtil.success(data, "批量退款完成");
    }

    @GetMapping("/seller/list")
    public HashMap<String, Object> getSellerList(@RequestParam(required = false, defaultValue = "1") Integer page, 
                                                 @RequestParam(required = false, defaultValue = "10") Integer pageSize, 
                                                 @RequestParam(required = false) String status,
                                                 @RequestParam(required = false) String keyword,
                                                 @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }
        
        int offset = (page - 1) * pageSize;
        
        List<Order> orderList = orderService.getSellerList(userId, offset, pageSize, status, keyword);
        int total = orderService.getSellerCount(userId, status, keyword);
        
        List<HashMap<String, Object>> resultList = orderList.stream()
            .map(order -> {
                HashMap<String, Object> map = new HashMap<>();
                map.put("id", order.getId());
                map.put("orderNo", order.getOrderNo());
                map.put("buyerId", order.getBuyerId());
                map.put("sellerId", order.getSellerId());
                map.put("goodsId", order.getGoodsId());
                map.put("goodsTitle", order.getGoodsTitle());
                map.put("goodsImage", order.getGoodsImage());
                map.put("price", order.getPrice());
                map.put("addressId", order.getAddressId());
                map.put("status", order.getStatus());
                map.put("tradeMethod", order.getTradeMethod());
                map.put("createTime", order.getCreateTime());
                map.put("updateTime", order.getUpdateTime());
                
                User buyer = userService.getById(order.getBuyerId());
                map.put("username", buyer != null ? buyer.getUsername() : "未知用户");
                
                User seller = userService.getById(order.getSellerId());
                map.put("sellerUsername", seller != null ? seller.getUsername() : "未知卖家");
                map.put("sellerAvatar", seller != null ? seller.getAvatar() : "");
                
                int commentCount = commentService.getCountByOrderId(order.getId());
                map.put("hasComment", commentCount > 0);
                
                return map;
            })
            .toList();
        
        HashMap<String, Object> data = new HashMap<>();
        data.put("list", resultList);
        data.put("total", total);
        data.put("page", page);
        data.put("pageSize", pageSize);
        data.put("totalPages", (total + pageSize - 1) / pageSize);
        
        return ResponseUtil.success(data, "获取成功");
    }
}