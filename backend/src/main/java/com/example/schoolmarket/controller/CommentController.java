package com.example.schoolmarket.controller;

import com.example.schoolmarket.entity.Comment;
import com.example.schoolmarket.entity.User;
import com.example.schoolmarket.service.CommentService;
import com.example.schoolmarket.service.GoodsService;
import com.example.schoolmarket.service.UserService;
import com.example.schoolmarket.utils.JwtUtil;
import com.example.schoolmarket.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;
    @Autowired
    private GoodsService goodsService;
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
    public HashMap<String, Object> getList(@RequestParam Long goodsId, @RequestParam(required = false, defaultValue = "1") Integer page, @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        // 计算偏移量
        int offset = (page - 1) * pageSize;
        
        // 调用服务层方法获取评论列表
        List<Comment> comments = commentService.getByGoodsId(goodsId, offset, pageSize);
        
        // 获取评论总数
        int total = commentService.getCountByGoodsId(goodsId);
        
        // 构建返回数据
        HashMap<String, Object> data = new HashMap<>();
        data.put("list", comments);
        data.put("total", total);
        data.put("page", page);
        data.put("pageSize", pageSize);
        data.put("totalPages", (total + pageSize - 1) / pageSize);
        
        return ResponseUtil.success(data, "获取成功");
    }

    @PostMapping("/create")
    public HashMap<String, Object> create(@RequestBody Map<String, Object> params, @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }
        
        Comment comment = new Comment();
        comment.setUserId(userId);
        
        // Validate required parameters
        if (!params.containsKey("goodsId") || params.get("goodsId") == null) {
            return ResponseUtil.error(400, "商品ID不能为空");
        }
        if (!params.containsKey("orderId") || params.get("orderId") == null) {
            return ResponseUtil.error(400, "订单ID不能为空");
        }
        if (!params.containsKey("rating") || params.get("rating") == null) {
            return ResponseUtil.error(400, "评分不能为空");
        }
        if (!params.containsKey("content") || params.get("content") == null) {
            return ResponseUtil.error(400, "评价内容不能为空");
        }
        
        comment.setGoodsId(Long.parseLong(params.get("goodsId").toString()));
        comment.setOrderId(Long.parseLong(params.get("orderId").toString()));
        comment.setRating(Integer.parseInt(params.get("rating").toString()));
        comment.setContent((String) params.get("content"));
        
        // Determine comment type (buyer or seller)
        String type = params.containsKey("type") ? (String) params.get("type") : "buyer";
        comment.setType(type);
        
        // Handle images - convert array to JSON string
        Object imagesObj = params.get("images");
        if (imagesObj instanceof List) {
            List<String> imagesList = (List<String>) imagesObj;
            StringBuilder imagesJsonBuilder = new StringBuilder("[");
            for (int i = 0; i < imagesList.size(); i++) {
                imagesJsonBuilder.append('"').append(imagesList.get(i)).append('"');
                if (i < imagesList.size() - 1) {
                    imagesJsonBuilder.append(',');
                }
            }
            imagesJsonBuilder.append("]");
            comment.setImages(imagesJsonBuilder.toString());
        } else if (imagesObj instanceof String) {
            String imagesStr = (String) imagesObj;
            if (imagesStr.startsWith("[") && imagesStr.endsWith("]")) {
                comment.setImages(imagesStr);
            } else {
                comment.setImages("[\"" + imagesStr + "\"]");
            }
        }
        
        boolean success = commentService.save(comment);
        if (success) {
            return ResponseUtil.success(comment, "评价成功");
        }
        return ResponseUtil.error(400, "评价失败");
    }

    @PostMapping("/{id}/reply")
    public HashMap<String, Object> reply(@PathVariable Long id, @RequestBody HashMap<String, String> params, @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }
        
        Comment comment = commentService.getById(id);
        if (comment == null) {
            return ResponseUtil.error(404, "评价不存在");
        }
        
        com.example.schoolmarket.entity.Goods goods = goodsService.getById(comment.getGoodsId());
        if (goods == null) {
            return ResponseUtil.error(404, "商品不存在");
        }
        
        if (!goods.getSellerId().equals(userId)) {
            return ResponseUtil.error(403, "只能回复自己商品的评价");
        }
        
        String reply = params.get("reply");
        boolean success = commentService.replyComment(id, reply);
        if (success) {
            return ResponseUtil.success(null, "回复成功");
        }
        return ResponseUtil.error(400, "回复失败");
    }

    @GetMapping("/my")
    public HashMap<String, Object> getMyComments(@RequestParam(required = false, defaultValue = "1") Integer page, @RequestParam(required = false, defaultValue = "10") Integer pageSize, @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }
        int offset = (page - 1) * pageSize;
        
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("userId", userId);
        params.put("offset", offset);
        params.put("pageSize", pageSize);
        List<Comment> comments = commentService.getByUserId(params);
        
        int total = commentService.getCountByUserId(userId);
        
        List<HashMap<String, Object>> resultList = comments.stream()
            .map(comment -> {
                HashMap<String, Object> map = new HashMap<>();
                map.put("id", comment.getId());
                map.put("userId", comment.getUserId());
                map.put("goodsId", comment.getGoodsId());
                map.put("orderId", comment.getOrderId());
                map.put("rating", comment.getRating());
                map.put("content", comment.getContent());
                map.put("images", comment.getImages());
                map.put("reply", comment.getReply());
                map.put("createTime", comment.getCreateTime());
                map.put("username", comment.getUsername());
                map.put("avatar", comment.getAvatar());
                map.put("goodsTitle", comment.getGoodsTitle());
                map.put("goodsImages", comment.getGoodsImages());
                
                com.example.schoolmarket.entity.Goods goods = goodsService.getById(comment.getGoodsId());
                boolean canReply = goods != null && goods.getSellerId().equals(userId) && comment.getReply() == null;
                map.put("canReply", canReply);
                
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
    
    @GetMapping("/seller")
    public HashMap<String, Object> getSellerComments(@RequestParam(required = false, defaultValue = "1") Integer page, 
                                                     @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                                     @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }
        
        int offset = (page - 1) * pageSize;
        
        List<Comment> comments = commentService.getBySellerId(userId, offset, pageSize);
        int total = commentService.getCountBySellerId(userId);
        
        List<HashMap<String, Object>> resultList = comments.stream()
            .map(comment -> {
                HashMap<String, Object> map = new HashMap<>();
                map.put("id", comment.getId());
                map.put("userId", comment.getUserId());
                map.put("goodsId", comment.getGoodsId());
                map.put("orderId", comment.getOrderId());
                map.put("rating", comment.getRating());
                map.put("content", comment.getContent());
                map.put("images", comment.getImages());
                map.put("reply", comment.getReply());
                map.put("createTime", comment.getCreateTime());
                map.put("username", comment.getUsername());
                map.put("avatar", comment.getAvatar());
                map.put("goodsTitle", comment.getGoodsTitle());
                map.put("goodsImages", comment.getGoodsImages());
                map.put("canReply", comment.getReply() == null);
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
    
    @GetMapping("/all")
    public HashMap<String, Object> getAllComments(@RequestParam(required = false, defaultValue = "1") Integer page, 
                                                  @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                                  @RequestParam(required = false) String keyword,
                                                  @RequestParam(required = false) Integer rating,
                                                  @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }
        
        // 调用服务层方法获取所有评论
        List<Comment> comments = commentService.list();
        
        // 过滤
        List<Comment> filteredList = comments.stream()
            .filter(comment -> {
                if (keyword != null && !keyword.isEmpty()) {
                    String lowerKeyword = keyword.toLowerCase();
                    return (comment.getContent() != null && comment.getContent().toLowerCase().contains(lowerKeyword));
                }
                return true;
            })
            .filter(comment -> {
                if (rating != null && rating > 0) {
                    return rating.equals(comment.getRating());
                }
                return true;
            })
            .toList();
        
        // 分页
        int offset = (page - 1) * pageSize;
        List<Comment> paginatedList = filteredList.stream()
            .skip(offset)
            .limit(pageSize)
            .toList();
        
        // 构建返回数据 - 添加商品和用户信息
        List<HashMap<String, Object>> resultList = paginatedList.stream()
            .map(comment -> {
                HashMap<String, Object> map = new HashMap<>();
                map.put("id", comment.getId());
                map.put("userId", comment.getUserId());
                map.put("goodsId", comment.getGoodsId());
                map.put("orderId", comment.getOrderId());
                map.put("rating", comment.getRating());
                map.put("content", comment.getContent());
                map.put("images", comment.getImages());
                map.put("reply", comment.getReply());
                map.put("createTime", comment.getCreateTime());
                map.put("updateTime", comment.getUpdateTime());
                map.put("status", comment.getStatus());
                map.put("auditComment", comment.getAuditComment());
                
                // 获取用户信息
                User user = userService.getById(comment.getUserId());
                if (user != null) {
                    map.put("username", user.getUsername());
                    map.put("userEmail", user.getEmail());
                    map.put("userPhone", user.getPhone());
                }
                
                // 获取商品信息
                com.example.schoolmarket.entity.Goods goods = goodsService.getById(comment.getGoodsId());
                if (goods != null) {
                    map.put("goodsTitle", goods.getTitle());
                    map.put("goodsPrice", goods.getPrice());
                    map.put("goodsImage", goods.getImages());
                }
                
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

    @PutMapping("/{id}/rating")
    public HashMap<String, Object> updateRating(@PathVariable Long id, @RequestBody HashMap<String, Integer> params, @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }
        
        Integer rating = params.get("rating");
        if (rating == null || rating < 1 || rating > 5) {
            return ResponseUtil.error(400, "评分必须在1-5星之间");
        }
        
        Comment comment = commentService.getById(id);
        if (comment == null) {
            return ResponseUtil.error(404, "评价不存在");
        }
        
        comment.setRating(rating);
        boolean success = commentService.update(comment);
        if (success) {
            return ResponseUtil.success(comment, "修改成功");
        }
        return ResponseUtil.error(400, "修改失败");
    }

    @DeleteMapping("/{id}")
    public HashMap<String, Object> deleteComment(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }
        
        boolean success = commentService.delete(id);
        if (success) {
            return ResponseUtil.success(null, "删除成功");
        }
        return ResponseUtil.error(400, "删除失败");
    }

    @PutMapping("/{id}/audit")
    public HashMap<String, Object> auditComment(@PathVariable Long id, @RequestBody Map<String, String> params) {
        String status = params.get("status");
        String auditComment = params.get("auditComment");
        
        if (status == null || status.isEmpty()) {
            return ResponseUtil.error(400, "审核状态不能为空");
        }
        
        boolean success = commentService.auditComment(id, status, auditComment);
        if (success) {
            return ResponseUtil.success(null, "审核成功");
        }
        return ResponseUtil.error(400, "审核失败");
    }
}