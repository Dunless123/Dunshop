package com.example.schoolmarket.mapper;

import com.example.schoolmarket.entity.PayPassword;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PayPasswordMapper {
    int insert(PayPassword payPassword);
    int update(PayPassword payPassword);
    PayPassword selectByUserId(@Param("userId") Long userId);
    int deleteByUserId(@Param("userId") Long userId);
}