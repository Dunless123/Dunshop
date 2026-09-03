package com.example.schoolmarket.controller;

import com.example.schoolmarket.entity.Order;
import com.example.schoolmarket.entity.User;
import com.example.schoolmarket.service.GoodsService;
import com.example.schoolmarket.service.OrderService;
import com.example.schoolmarket.service.UserService;
import com.example.schoolmarket.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private UserService userService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;

    @GetMapping("/users")
    public HashMap<String, Object> getUsers(@RequestParam(required = false, defaultValue = "1") Integer page, 
                                            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                            @RequestParam(required = false) String keyword, @RequestParam(required = false) String status,
                                            @RequestParam(required = false) String authStatus) {
        List<User> userList = userService.list();
        
        List<User> filteredList = userList.stream()
            .filter(user -> {
                if (keyword != null && !keyword.isEmpty()) {
                    String lowerKeyword = keyword.toLowerCase();
                    return (user.getUsername() != null && user.getUsername().toLowerCase().contains(lowerKeyword))
                        || (user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerKeyword))
                        || (user.getPhone() != null && user.getPhone().contains(keyword));
                }
                return true;
            })
            .filter(user -> {
                if (status != null && !status.isEmpty()) {
                    return status.equals(user.getStatus());
                }
                return true;
            })
            .filter(user -> {
                if (authStatus != null && !authStatus.isEmpty()) {
                    return authStatus.equals(user.getAuthStatus());
                }
                return true;
            })
            .toList();
        
        int offset = (page - 1) * pageSize;
        List<User> paginatedList = filteredList.stream()
            .skip(offset)
            .limit(pageSize)
            .toList();
        
        HashMap<String, Object> data = new HashMap<>();
        data.put("list", paginatedList);
        data.put("total", filteredList.size());
        data.put("page", page);
        data.put("pageSize", pageSize);
        data.put("totalPages", (filteredList.size() + pageSize - 1) / pageSize);
        
        return ResponseUtil.success(data, "获取成功");
    }

    @PutMapping("/users/{id}")
    public HashMap<String, Object> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        boolean success = userService.update(user);
        if (success) {
            return ResponseUtil.success(null, "更新成功");
        }
        return ResponseUtil.error(400, "更新失败");
    }

    @DeleteMapping("/users/{id}")
    public HashMap<String, Object> deleteUser(@PathVariable Long id) {
        boolean success = userService.delete(id);
        if (success) {
            return ResponseUtil.success(null, "删除成功");
        }
        return ResponseUtil.error(400, "删除失败");
    }

    @GetMapping("/statistics")
    public HashMap<String, Object> getStatistics() {
        HashMap<String, Object> statistics = new HashMap<>();
        
        statistics.put("totalUsers", userService.getTotalUserCount());
        statistics.put("todayOrders", orderService.getTodayOrders());
        statistics.put("totalAmount", orderService.getTotalSales());
        statistics.put("pendingOrders", orderService.getPendingOrders());
        
        return ResponseUtil.success(statistics, "获取成功");
    }

    @GetMapping("/dashboard")
    public HashMap<String, Object> getDashboard() {
        HashMap<String, Object> data = new HashMap<>();
        
        data.put("totalGoods", goodsService.list().size());
        data.put("totalOrders", orderService.getTotalOrders());
        data.put("totalSales", orderService.getTotalSales());
        data.put("totalViews", goodsService.list().stream().mapToInt(g -> g.getViewCount() != null ? g.getViewCount() : 0).sum());
        data.put("todayOrders", orderService.getTodayOrders());
        data.put("todaySales", orderService.getTodaySales());
        
        return ResponseUtil.success(data, "获取成功");
    }

    @GetMapping("/orders")
    public HashMap<String, Object> getOrders(@RequestParam(required = false, defaultValue = "1") Integer page, 
                                             @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                             @RequestParam(required = false) String keyword,
                                             @RequestParam(required = false) String status) {
        int offset = (page - 1) * pageSize;
        
        List<Order> orderList = orderService.list();
        
        List<Order> filteredList = orderList.stream()
            .filter(order -> {
                if (keyword != null && !keyword.isEmpty()) {
                    String lowerKeyword = keyword.toLowerCase();
                    return (order.getOrderNo() != null && order.getOrderNo().toLowerCase().contains(lowerKeyword))
                        || (order.getGoodsTitle() != null && order.getGoodsTitle().toLowerCase().contains(lowerKeyword));
                }
                return true;
            })
            .filter(order -> {
                if (status != null && !status.isEmpty()) {
                    return status.equals(order.getStatus());
                }
                return true;
            })
            .toList();
        
        List<HashMap<String, Object>> resultList = filteredList.stream()
            .skip(offset)
            .limit(pageSize)
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
                map.put("buyerName", buyer != null ? buyer.getUsername() : "未知用户");
                
                return map;
            })
            .toList();
        
        HashMap<String, Object> data = new HashMap<>();
        data.put("list", resultList);
        data.put("total", filteredList.size());
        data.put("page", page);
        data.put("pageSize", pageSize);
        data.put("totalPages", (filteredList.size() + pageSize - 1) / pageSize);
        
        return ResponseUtil.success(data, "获取成功");
    }

    @GetMapping("/goods")
    public HashMap<String, Object> getGoods(@RequestParam(required = false, defaultValue = "1") Integer page,
                                            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                            @RequestParam(required = false) String keyword,
                                            @RequestParam(required = false) String status) {
        int offset = (page - 1) * pageSize;
        
        List<com.example.schoolmarket.entity.Goods> goodsList = goodsService.list();
        
        List<com.example.schoolmarket.entity.Goods> filteredList = goodsList.stream()
            .filter(goods -> {
                if (keyword != null && !keyword.isEmpty()) {
                    String lowerKeyword = keyword.toLowerCase();
                    return (goods.getTitle() != null && goods.getTitle().toLowerCase().contains(lowerKeyword));
                }
                return true;
            })
            .filter(goods -> {
                if (status != null && !status.isEmpty()) {
                    return status.equals(goods.getStatus());
                }
                return true;
            })
            .toList();
        
        List<HashMap<String, Object>> resultList = filteredList.stream()
            .skip(offset)
            .limit(pageSize)
            .map(goods -> {
                HashMap<String, Object> map = new HashMap<>();
                map.put("id", goods.getId());
                map.put("title", goods.getTitle());
                map.put("price", goods.getPrice());
                map.put("status", goods.getStatus());
                map.put("createTime", goods.getCreateTime());
                
                User seller = userService.getById(goods.getSellerId());
                map.put("sellerName", seller != null ? seller.getUsername() : "未知卖家");
                
                return map;
            })
            .toList();
        
        HashMap<String, Object> data = new HashMap<>();
        data.put("list", resultList);
        data.put("total", filteredList.size());
        data.put("page", page);
        data.put("pageSize", pageSize);
        data.put("totalPages", (filteredList.size() + pageSize - 1) / pageSize);
        
        return ResponseUtil.success(data, "获取成功");
    }

    @PutMapping("/goods/batch/approve")
    public HashMap<String, Object> batchApproveGoods(@RequestBody Map<String, Object> requestBody) {
        List<?> idsObj = (List<?>) requestBody.get("ids");
        
        if (idsObj == null || idsObj.isEmpty()) {
            return ResponseUtil.error(400, "商品ID列表不能为空");
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
            
            if (id != null) {
                com.example.schoolmarket.entity.Goods goods = goodsService.getById(id);
                if (goods != null) {
                    goods.setStatus("在售");
                    if (goodsService.update(goods)) {
                        successCount++;
                    }
                }
            }
        }
        
        HashMap<String, Object> data = new HashMap<>();
        data.put("successCount", successCount);
        data.put("totalCount", idsObj.size());
        return ResponseUtil.success(data, "批量审核通过完成");
    }

    @PutMapping("/goods/batch/reject")
    public HashMap<String, Object> batchRejectGoods(@RequestBody Map<String, Object> requestBody) {
        List<?> idsObj = (List<?>) requestBody.get("ids");
        
        if (idsObj == null || idsObj.isEmpty()) {
            return ResponseUtil.error(400, "商品ID列表不能为空");
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
            
            if (id != null) {
                com.example.schoolmarket.entity.Goods goods = goodsService.getById(id);
                if (goods != null) {
                    goods.setStatus("已拒绝");
                    if (goodsService.update(goods)) {
                        successCount++;
                    }
                }
            }
        }
        
        HashMap<String, Object> data = new HashMap<>();
        data.put("successCount", successCount);
        data.put("totalCount", idsObj.size());
        return ResponseUtil.success(data, "批量拒绝完成");
    }

    @PutMapping("/users/batch/enable")
    public HashMap<String, Object> batchEnableUsers(@RequestBody Map<String, Object> requestBody) {
        List<?> idsObj = (List<?>) requestBody.get("ids");
        
        if (idsObj == null || idsObj.isEmpty()) {
            return ResponseUtil.error(400, "用户ID列表不能为空");
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
            
            if (id != null) {
                User user = userService.getById(id);
                if (user != null) {
                    user.setStatus("正常");
                    if (userService.update(user)) {
                        successCount++;
                    }
                }
            }
        }
        
        HashMap<String, Object> data = new HashMap<>();
        data.put("successCount", successCount);
        data.put("totalCount", idsObj.size());
        return ResponseUtil.success(data, "批量启用完成");
    }

    @PutMapping("/users/batch/disable")
    public HashMap<String, Object> batchDisableUsers(@RequestBody Map<String, Object> requestBody) {
        List<?> idsObj = (List<?>) requestBody.get("ids");
        
        if (idsObj == null || idsObj.isEmpty()) {
            return ResponseUtil.error(400, "用户ID列表不能为空");
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
            
            if (id != null) {
                User user = userService.getById(id);
                if (user != null) {
                    user.setStatus("禁用");
                    if (userService.update(user)) {
                        successCount++;
                    }
                }
            }
        }
        
        HashMap<String, Object> data = new HashMap<>();
        data.put("successCount", successCount);
        data.put("totalCount", idsObj.size());
        return ResponseUtil.success(data, "批量禁用完成");
    }
}