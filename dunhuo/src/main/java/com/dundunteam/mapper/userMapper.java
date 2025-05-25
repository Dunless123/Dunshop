package com.dundunteam.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dundunteam.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface userMapper extends BaseMapper<User> {

}
