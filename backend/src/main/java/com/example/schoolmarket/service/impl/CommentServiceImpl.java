package com.example.schoolmarket.service.impl;

import com.example.schoolmarket.entity.Comment;
import com.example.schoolmarket.mapper.CommentMapper;
import com.example.schoolmarket.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;

    @Override
    public List<Comment> getByGoodsId(Long goodsId) {
        Map<String, Object> params = new HashMap<>();
        params.put("goodsId", goodsId);
        return commentMapper.selectByGoodsId(params);
    }

    @Override
    public List<Comment> getByGoodsId(Long goodsId, int offset, int pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("goodsId", goodsId);
        params.put("offset", offset);
        params.put("pageSize", pageSize);
        return commentMapper.selectByGoodsId(params);
    }

    @Override
    public int getCountByGoodsId(Long goodsId) {
        Map<String, Object> params = new HashMap<>();
        params.put("goodsId", goodsId);
        return commentMapper.countByGoodsId(params);
    }

    @Override
    public List<Comment> getByUserId(Map<String, Object> params) {
        return commentMapper.selectByUserId(params);
    }

    @Override
    public int getCountByUserId(Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        return commentMapper.countByUserId(params);
    }

    @Override
    public int getCountByOrderId(Long orderId) {
        Map<String, Object> params = new HashMap<>();
        params.put("orderId", orderId);
        return commentMapper.countByOrderId(params);
    }

    @Override
    public boolean replyComment(Long commentId, String reply) {
        Comment comment = getById(commentId);
        if (comment != null) {
            comment.setReply(reply);
            return commentMapper.update(comment) > 0;
        }
        return false;
    }

    @Override
    public Comment getById(Long id) {
        return commentMapper.selectById(id);
    }

    @Override
    public boolean save(Comment comment) {
        return commentMapper.insert(comment) > 0;
    }

    @Override
    public boolean update(Comment comment) {
        return commentMapper.update(comment) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return commentMapper.delete(id) > 0;
    }

    @Override
    public List<Comment> list() {
        return commentMapper.selectCommentList();
    }

    @Override
    public int countAll() {
        return commentMapper.countAll();
    }

    @Override
    public HashMap<String, Object> getRatingStatistics() {
        HashMap<String, Object> result = new HashMap<>();
        int total = countAll();
        
        for (int i = 1; i <= 5; i++) {
            Map<String, Object> params = new HashMap<>();
            params.put("rating", i);
            int count = commentMapper.countByRating(params);
            double percentage = total > 0 ? (count * 100.0 / total) : 0;
            result.put(String.valueOf(i), new HashMap<String, Object>() {{
                put("count", count);
                put("percentage", Math.round(percentage * 100.0) / 100.0);
            }});
        }
        
        return result;
    }

    @Override
    public List<Comment> getBySellerId(Long sellerId, int offset, int pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("sellerId", sellerId);
        params.put("offset", offset);
        params.put("pageSize", pageSize);
        return commentMapper.selectBySellerId(params);
    }

    @Override
    public int getCountBySellerId(Long sellerId) {
        Map<String, Object> params = new HashMap<>();
        params.put("sellerId", sellerId);
        return commentMapper.countBySellerId(params);
    }

    @Override
    public boolean auditComment(Long commentId, String status, String auditComment) {
        Comment comment = getById(commentId);
        if (comment != null) {
            comment.setStatus(status);
            comment.setAuditComment(auditComment);
            return commentMapper.update(comment) > 0;
        }
        return false;
    }
}