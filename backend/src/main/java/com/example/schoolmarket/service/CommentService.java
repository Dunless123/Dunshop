package com.example.schoolmarket.service;

import com.example.schoolmarket.entity.Comment;

import java.util.List;
import java.util.Map;

public interface CommentService {
    Comment getById(Long id);
    boolean save(Comment comment);
    boolean update(Comment comment);
    boolean delete(Long id);
    List<Comment> getByGoodsId(Long goodsId);
    List<Comment> getByGoodsId(Long goodsId, int offset, int pageSize);
    int getCountByGoodsId(Long goodsId);
    List<Comment> getByUserId(Map<String, Object> params);
    int getCountByUserId(Long userId);
    int getCountByOrderId(Long orderId);
    boolean replyComment(Long commentId, String reply);
    List<Comment> list();
    int countAll();
    java.util.HashMap<String, Object> getRatingStatistics();
    List<Comment> getBySellerId(Long sellerId, int offset, int pageSize);
    int getCountBySellerId(Long sellerId);
    boolean auditComment(Long commentId, String status, String auditComment);
}