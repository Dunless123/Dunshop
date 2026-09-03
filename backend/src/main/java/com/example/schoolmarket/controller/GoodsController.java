package com.example.schoolmarket.controller;

import com.example.schoolmarket.entity.Goods;
import com.example.schoolmarket.service.GoodsService;
import com.example.schoolmarket.utils.JwtUtil;
import com.example.schoolmarket.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/goods")
public class GoodsController {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private com.example.schoolmarket.service.OrderService orderService;

    private Long getUserIdFromToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        String token = authorization.replace("Bearer ", "");
        return jwtUtil.getUserIdFromToken(token);
    }

    @GetMapping("/list")
    public HashMap<String, Object> getList(@RequestParam(required = false, defaultValue = "1") Integer page, @RequestParam(required = false, defaultValue = "10") Integer pageSize, @RequestParam(required = false) Long categoryId, @RequestParam(required = false) Long campusId, @RequestParam(required = false) String keyword, @RequestParam(required = false) String sortBy, @RequestParam(required = false, defaultValue = "desc") String order) {
        // 计算偏移量
        int offset = (page - 1) * pageSize;
        
        // 调用服务层方法获取商品列表
        List<Goods> goodsList = goodsService.getList(offset, pageSize, categoryId, campusId, keyword, sortBy, order);
        
        // 获取商品总数
        int total = goodsService.getCount(categoryId, campusId, keyword);
        
        // 构建返回数据
        HashMap<String, Object> data = new HashMap<>();
        data.put("list", goodsList);
        data.put("total", total);
        data.put("page", page);
        data.put("pageSize", pageSize);
        data.put("totalPages", (total + pageSize - 1) / pageSize);
        
        return ResponseUtil.success(data, "获取成功");
    }

    @GetMapping("/{id}")
    public HashMap<String, Object> getDetail(@PathVariable Long id) {
        Goods goods = goodsService.getById(id);
        return ResponseUtil.success(goods, "获取成功");
    }

    @PostMapping("/{id}/view")
    public HashMap<String, Object> addViewCount(@PathVariable Long id) {
        boolean success = goodsService.addViewCount(id);
        if (success) {
            return ResponseUtil.success(null, "浏览量增加成功");
        }
        return ResponseUtil.error(400, "浏览量增加失败");
    }

    @PostMapping("/publish")
    public HashMap<String, Object> publish(@RequestBody Map<String, Object> params, @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }
        // Validate required fields
        if (!params.containsKey("title") || params.get("title") == null) {
            return ResponseUtil.error(400, "标题不能为空");
        }
        if (!params.containsKey("description") || params.get("description") == null) {
            return ResponseUtil.error(400, "描述不能为空");
        }
        if (!params.containsKey("price") || params.get("price") == null) {
            return ResponseUtil.error(400, "价格不能为空");
        }
        if (!params.containsKey("categoryId") || params.get("categoryId") == null) {
            return ResponseUtil.error(400, "分类不能为空");
        }
        if (!params.containsKey("campusId") || params.get("campusId") == null) {
            return ResponseUtil.error(400, "校区不能为空");
        }
        if (!params.containsKey("tags") || params.get("tags") == null) {
            return ResponseUtil.error(400, "标签不能为空");
        }
        if (!params.containsKey("quality") || params.get("quality") == null) {
            return ResponseUtil.error(400, "商品质量不能为空");
        }
        if (!params.containsKey("tradeMethods") || params.get("tradeMethods") == null) {
            return ResponseUtil.error(400, "交易方式不能为空");
        }
        if (!params.containsKey("images") || params.get("images") == null) {
            return ResponseUtil.error(400, "图片不能为空");
        }
        Goods goods = new Goods();
        goods.setSellerId(userId);
        goods.setStatus("在售");
        goods.setViewCount(0);
        goods.setFavoriteCount(0);
        goods.setTitle((String) params.get("title"));
        goods.setDescription((String) params.get("description"));
        goods.setPrice(new BigDecimal(params.get("price").toString()));
        goods.setOriginalPrice(params.containsKey("originalPrice") && params.get("originalPrice") != null ? new BigDecimal(params.get("originalPrice").toString()) : null);
        goods.setCategoryId(Long.parseLong(params.get("categoryId").toString()));
        goods.setCampusId(Long.parseLong(params.get("campusId").toString()));
        goods.setQuality((String) params.get("quality"));
        
        // Handle tags - convert to JSON array
        Object tagsObj = params.get("tags");
        if (tagsObj instanceof List) {
            List<String> tagsList = (List<String>) tagsObj;
            // Convert list to JSON array string
            StringBuilder tagsJsonBuilder = new StringBuilder("[");
            for (int i = 0; i < tagsList.size(); i++) {
                tagsJsonBuilder.append('"').append(tagsList.get(i)).append('"');
                if (i < tagsList.size() - 1) {
                    tagsJsonBuilder.append(',');
                }
            }
            tagsJsonBuilder.append("]");
            goods.setTags(tagsJsonBuilder.toString());
        } else if (tagsObj instanceof String) {
            String tagsStr = (String) tagsObj;
            if (tagsStr.startsWith("[") && tagsStr.endsWith("]")) {
                goods.setTags(tagsStr);
            } else {
                // Treat comma-separated string as tags
                String[] tagArray = tagsStr.split(",");
                StringBuilder tagsJsonBuilder = new StringBuilder("[");
                for (int i = 0; i < tagArray.length; i++) {
                    tagsJsonBuilder.append('"').append(tagArray[i].trim()).append('"');
                    if (i < tagArray.length - 1) {
                        tagsJsonBuilder.append(',');
                    }
                }
                tagsJsonBuilder.append("]");
                goods.setTags(tagsJsonBuilder.toString());
            }
        }
        
        // Handle tradeMethods - convert to JSON array
        Object tradeMethodsObj = params.get("tradeMethods");
        if (tradeMethodsObj instanceof List) {
            List<String> tradeMethodsList = (List<String>) tradeMethodsObj;
            // Convert list to JSON array string
            StringBuilder tradeMethodsJsonBuilder = new StringBuilder("[");
            for (int i = 0; i < tradeMethodsList.size(); i++) {
                tradeMethodsJsonBuilder.append('"').append(tradeMethodsList.get(i)).append('"');
                if (i < tradeMethodsList.size() - 1) {
                    tradeMethodsJsonBuilder.append(',');
                }
            }
            tradeMethodsJsonBuilder.append("]");
            goods.setTradeMethods(tradeMethodsJsonBuilder.toString());
        } else if (tradeMethodsObj instanceof String) {
            String tradeMethodsStr = (String) tradeMethodsObj;
            if (tradeMethodsStr.startsWith("[") && tradeMethodsStr.endsWith("]")) {
                goods.setTradeMethods(tradeMethodsStr);
            } else {
                // Treat slash-separated string as trade methods
                String[] tradeMethodsArray = tradeMethodsStr.split("/");
                StringBuilder tradeMethodsJsonBuilder = new StringBuilder("[");
                for (int i = 0; i < tradeMethodsArray.length; i++) {
                    tradeMethodsJsonBuilder.append('"').append(tradeMethodsArray[i].trim()).append('"');
                    if (i < tradeMethodsArray.length - 1) {
                        tradeMethodsJsonBuilder.append(',');
                    }
                }
                tradeMethodsJsonBuilder.append("]");
                goods.setTradeMethods(tradeMethodsJsonBuilder.toString());
            }
        }
        // Handle images array
        Object imagesObj = params.get("images");
        if (imagesObj instanceof List) {
            List<String> imagesList = (List<String>) imagesObj;
            // Convert list to JSON array string
            StringBuilder jsonBuilder = new StringBuilder("[");
            for (int i = 0; i < imagesList.size(); i++) {
                jsonBuilder.append('"').append(imagesList.get(i)).append('"');
                if (i < imagesList.size() - 1) {
                    jsonBuilder.append(',');
                }
            }
            jsonBuilder.append("]");
            goods.setImages(jsonBuilder.toString());
        } else if (imagesObj instanceof String) {
            // Check if it's already a JSON array
            String imagesStr = (String) imagesObj;
            if (imagesStr.startsWith("[") && imagesStr.endsWith("]")) {
                goods.setImages(imagesStr);
            } else {
                // Treat as single image
                goods.setImages("[\"" + imagesStr + "\"]");
            }
        }
        boolean success = goodsService.save(goods);
        if (success) {
            return ResponseUtil.success(goods, "发布成功");
        }
        return ResponseUtil.error(400, "发布失败");
    }

    @PutMapping("/{id}")
    public HashMap<String, Object> update(@PathVariable Long id, @RequestBody Map<String, Object> params, @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }
        Goods existingGoods = goodsService.getById(id);
        if (existingGoods != null && existingGoods.getSellerId().equals(userId)) {
            Goods goods = new Goods();
            goods.setId(id);
            if (params.containsKey("title")) goods.setTitle((String) params.get("title"));
            if (params.containsKey("description")) goods.setDescription((String) params.get("description"));
            if (params.containsKey("price")) goods.setPrice(new BigDecimal(params.get("price").toString()));
            if (params.containsKey("originalPrice")) goods.setOriginalPrice(new BigDecimal(params.get("originalPrice").toString()));
            if (params.containsKey("categoryId")) goods.setCategoryId(Long.parseLong(params.get("categoryId").toString()));
            if (params.containsKey("campusId")) goods.setCampusId(Long.parseLong(params.get("campusId").toString()));
            if (params.containsKey("quality")) goods.setQuality((String) params.get("quality"));
            if (params.containsKey("status")) {
                Object statusObj = params.get("status");
                String status;
                if (statusObj instanceof Integer) {
                    status = (Integer) statusObj == 1 ? "在售" : "已下架";
                } else {
                    status = String.valueOf(statusObj);
                }
                goods.setStatus(status);
            }
            
            // Handle tags - convert to JSON array
            if (params.containsKey("tags")) {
                Object tagsObj = params.get("tags");
                if (tagsObj instanceof List) {
                    List<String> tagsList = (List<String>) tagsObj;
                    // Convert list to JSON array string
                    StringBuilder tagsJsonBuilder = new StringBuilder("[");
                    for (int i = 0; i < tagsList.size(); i++) {
                        tagsJsonBuilder.append('"').append(tagsList.get(i)).append('"');
                        if (i < tagsList.size() - 1) {
                            tagsJsonBuilder.append(',');
                        }
                    }
                    tagsJsonBuilder.append("]");
                    goods.setTags(tagsJsonBuilder.toString());
                } else if (tagsObj instanceof String) {
                    String tagsStr = (String) tagsObj;
                    if (tagsStr.startsWith("[") && tagsStr.endsWith("]")) {
                        goods.setTags(tagsStr);
                    } else {
                        // Treat comma-separated string as tags
                        String[] tagArray = tagsStr.split(",");
                        StringBuilder tagsJsonBuilder = new StringBuilder("[");
                        for (int i = 0; i < tagArray.length; i++) {
                            tagsJsonBuilder.append('"').append(tagArray[i].trim()).append('"');
                            if (i < tagArray.length - 1) {
                                tagsJsonBuilder.append(',');
                            }
                        }
                        tagsJsonBuilder.append("]");
                        goods.setTags(tagsJsonBuilder.toString());
                    }
                }
            }
            
            // Handle tradeMethods - convert to JSON array
            if (params.containsKey("tradeMethods")) {
                Object tradeMethodsObj = params.get("tradeMethods");
                if (tradeMethodsObj instanceof List) {
                    List<String> tradeMethodsList = (List<String>) tradeMethodsObj;
                    // Convert list to JSON array string
                    StringBuilder tradeMethodsJsonBuilder = new StringBuilder("[");
                    for (int i = 0; i < tradeMethodsList.size(); i++) {
                        tradeMethodsJsonBuilder.append('"').append(tradeMethodsList.get(i)).append('"');
                        if (i < tradeMethodsList.size() - 1) {
                            tradeMethodsJsonBuilder.append(',');
                        }
                    }
                    tradeMethodsJsonBuilder.append("]");
                    goods.setTradeMethods(tradeMethodsJsonBuilder.toString());
                } else if (tradeMethodsObj instanceof String) {
                    String tradeMethodsStr = (String) tradeMethodsObj;
                    if (tradeMethodsStr.startsWith("[") && tradeMethodsStr.endsWith("]")) {
                        goods.setTradeMethods(tradeMethodsStr);
                    } else {
                        // Treat slash-separated string as trade methods
                        String[] tradeMethodsArray = tradeMethodsStr.split("/");
                        StringBuilder tradeMethodsJsonBuilder = new StringBuilder("[");
                        for (int i = 0; i < tradeMethodsArray.length; i++) {
                            tradeMethodsJsonBuilder.append('"').append(tradeMethodsArray[i].trim()).append('"');
                            if (i < tradeMethodsArray.length - 1) {
                                tradeMethodsJsonBuilder.append(',');
                            }
                        }
                        tradeMethodsJsonBuilder.append("]");
                        goods.setTradeMethods(tradeMethodsJsonBuilder.toString());
                    }
                }
            }
            // Handle images array
            if (params.containsKey("images")) {
                Object imagesObj = params.get("images");
                if (imagesObj instanceof List) {
                    List<String> imagesList = (List<String>) imagesObj;
                    // Convert list to JSON array string
                    StringBuilder jsonBuilder = new StringBuilder("[");
                    for (int i = 0; i < imagesList.size(); i++) {
                        jsonBuilder.append('"').append(imagesList.get(i)).append('"');
                        if (i < imagesList.size() - 1) {
                            jsonBuilder.append(',');
                        }
                    }
                    jsonBuilder.append("]");
                    goods.setImages(jsonBuilder.toString());
                } else if (imagesObj instanceof String) {
                    // Check if it's already a JSON array
                    String imagesStr = (String) imagesObj;
                    if (imagesStr.startsWith("[") && imagesStr.endsWith("]")) {
                        goods.setImages(imagesStr);
                    } else {
                        // Treat as single image
                        goods.setImages("[\"" + imagesStr + "\"]");
                    }
                }
            }
            boolean success = goodsService.update(goods);
            if (success) {
                return ResponseUtil.success(null, "更新成功");
            }
        }
        return ResponseUtil.error(400, "更新失败");
    }

    @DeleteMapping("/{id}")
    public HashMap<String, Object> delete(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }
        Goods goods = goodsService.getById(id);
        if (goods != null && goods.getSellerId().equals(userId)) {
            boolean success = goodsService.delete(id);
            if (success) {
                return ResponseUtil.success(null, "删除成功");
            }
        }
        return ResponseUtil.error(400, "删除失败");
    }

    @PostMapping("/{id}/favorite")
    public HashMap<String, Object> addFavorite(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }
        boolean success = goodsService.addFavorite(userId, id);
        if (success) {
            return ResponseUtil.success(null, "收藏成功");
        }
        return ResponseUtil.error(400, "收藏失败");
    }

    @DeleteMapping("/{id}/favorite")
    public HashMap<String, Object> removeFavorite(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }
        boolean success = goodsService.removeFavorite(userId, id);
        if (success) {
            return ResponseUtil.success(null, "取消收藏成功");
        }
        return ResponseUtil.error(400, "取消收藏失败");
    }

    @GetMapping("/my")
    public HashMap<String, Object> getMyGoods(@RequestParam(required = false, defaultValue = "1") Integer page, @RequestParam(required = false, defaultValue = "10") Integer pageSize, @RequestParam(required = false) String status, @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }
        // 计算偏移量
        int offset = (page - 1) * pageSize;
        
        // 调用服务层方法获取我的商品列表
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("sellerId", userId);
        params.put("status", status);
        params.put("offset", offset);
        params.put("pageSize", pageSize);
        List<Goods> goodsList = goodsService.getBySellerId(params);
        
        // 构建返回数据
        HashMap<String, Object> data = new HashMap<>();
        data.put("list", goodsList);
        data.put("total", goodsList.size());
        data.put("page", page);
        data.put("pageSize", pageSize);
        data.put("totalPages", (goodsList.size() + pageSize - 1) / pageSize);
        
        return ResponseUtil.success(data, "获取成功");
    }

    @GetMapping("/favorites/check")
    public HashMap<String, Object> checkFavorite(@RequestParam Long goodsId, @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }
        boolean isFavorited = goodsService.isFavorited(userId, goodsId);
        HashMap<String, Object> data = new HashMap<>();
        data.put("isFavorited", isFavorited);
        return ResponseUtil.success(data, "获取成功");
    }

    @GetMapping("/favorites")
    public HashMap<String, Object> getFavorites(@RequestParam(required = false, defaultValue = "1") Integer page, @RequestParam(required = false, defaultValue = "10") Integer pageSize, @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }
        // 计算偏移量
        int offset = (page - 1) * pageSize;
        
        // 调用服务层方法获取收藏的商品列表
        List<Goods> goodsList = goodsService.getFavorites(userId, offset, pageSize);
        
        // 获取收藏总数
        int total = goodsService.getFavoritesCount(userId);
        
        // 构建返回数据
        HashMap<String, Object> data = new HashMap<>();
        data.put("list", goodsList);
        data.put("total", total);
        data.put("page", page);
        data.put("pageSize", pageSize);
        data.put("totalPages", (total + pageSize - 1) / pageSize);
        
        return ResponseUtil.success(data, "获取成功");
    }

    @GetMapping("/{id}/orders")
    public HashMap<String, Object> getGoodsOrders(@PathVariable Long id, @RequestParam(required = false) String status, @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }
        
        Goods goods = goodsService.getById(id);
        if (goods == null || !goods.getSellerId().equals(userId)) {
            return ResponseUtil.error(400, "无权查看此商品的订单");
        }
        
        List<com.example.schoolmarket.entity.Order> orders = orderService.getOrdersByGoodsId(id, status);
        
        HashMap<String, Object> data = new HashMap<>();
        data.put("list", orders);
        data.put("total", orders.size());
        
        return ResponseUtil.success(data, "获取成功");
    }
}