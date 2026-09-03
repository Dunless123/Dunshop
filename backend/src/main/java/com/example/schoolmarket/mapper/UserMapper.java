package com.example.schoolmarket.mapper;

import com.example.schoolmarket.entity.User;
import java.util.List;
import java.util.Map;

public interface UserMapper {
    List<User> selectAllUsers();
    List<User> selectUserList(Map<String, Object> params);
    User selectByStudentId(String studentId);
    User selectById(Long id);
    int insert(User user);
    int update(User user);
    int delete(Long id);
    int countAllUsers();
    User selectByOpenId(String openId);
    List<User> selectByPhone(Map<String, Object> params);
}