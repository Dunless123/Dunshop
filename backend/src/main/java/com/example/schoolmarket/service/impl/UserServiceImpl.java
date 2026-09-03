package com.example.schoolmarket.service.impl;

import com.example.schoolmarket.entity.User;
import com.example.schoolmarket.mapper.CommentMapper;
import com.example.schoolmarket.mapper.GoodsMapper;
import com.example.schoolmarket.mapper.OrderMapper;
import com.example.schoolmarket.mapper.UserMapper;
import com.example.schoolmarket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public User login(String studentId, String password) {
        User user = userMapper.selectByStudentId(studentId);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    @Override
    public boolean register(User user) {
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            if (user.getStudentId() != null && !user.getStudentId().isEmpty()) {
                user.setUsername(user.getStudentId());
            } else {
                return false;
            }
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("普通用户");
        user.setStatus("正常");
        user.setAuthStatus("待审核");
        return userMapper.insert(user) > 0;
    }

    @Override
    public boolean resetPassword(String studentId, String newPassword) {
        User user = userMapper.selectByStudentId(studentId);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            return userMapper.update(user) > 0;
        }
        return false;
    }

    @Override
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public boolean save(User user) {
        return userMapper.insert(user) > 0;
    }

    @Override
    public boolean update(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            User existingUser = userMapper.selectById(user.getId());
            if (existingUser != null && !user.getPhone().equals(existingUser.getPhone())) {
                Map<String, Object> params = new HashMap<>();
                params.put("phone", user.getPhone());
                List<User> usersWithPhone = userMapper.selectByPhone(params);
                if (usersWithPhone != null && !usersWithPhone.isEmpty()) {
                    return false;
                }
            }
        }
        
        return userMapper.update(user) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return userMapper.delete(id) > 0;
    }

    @Override
    public List<User> list() {
        return userMapper.selectAllUsers();
    }

    @Override
    public int getTotalUserCount() {
        return userMapper.countAllUsers();
    }

    @Override
    public Map<String, Object> getUserStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        Map<String, Object> goodsParams = new HashMap<>();
        goodsParams.put("sellerId", userId);
        goodsParams.put("status", "在售");
        int goodsCount = goodsMapper.countGoods(goodsParams);
        stats.put("goodsCount", goodsCount);
        
        Map<String, Object> orderParams = new HashMap<>();
        orderParams.put("buyerId", userId);
        int orderCount = orderMapper.countByBuyerId(orderParams);
        stats.put("orderCount", orderCount);
        
        Map<String, Object> commentParams = new HashMap<>();
        commentParams.put("userId", userId);
        int commentCount = commentMapper.countByUserId(commentParams);
        stats.put("commentCount", commentCount);
        
        return stats;
    }
}