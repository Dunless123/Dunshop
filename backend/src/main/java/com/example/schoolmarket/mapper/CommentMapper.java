package com.example.schoolmarket.mapper;

import com.example.schoolmarket.entity.Comment;
import java.util.List;
import java.util.Map;

public interface CommentMapper {
    List<Comment> selectByGoodsId(Map<String, Object> params);
    int countByGoodsId(Map<String, Object> params);
    List<Comment> selectByUserId(Map<String, Object> params);
    int countByUserId(Map<String, Object> params);
    int countByOrderId(Map<String, Object> params);
    List<Comment> selectCommentList();
    Comment selectById(Long id);
    int insert(Comment comment);
    int update(Comment comment);
    int delete(Long id);
    int countAll();
    int countByRating(Map<String, Object> params);
    List<Comment> selectBySellerId(Map<String, Object> params);
    int countBySellerId(Map<String, Object> params);
}