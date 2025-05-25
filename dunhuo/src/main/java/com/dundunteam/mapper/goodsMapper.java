package com.dundunteam.mapper;

import com.dundunteam.pojo.entity.goods;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface goodsMapper {
    @Insert("insert into goods(categoryId,name, description, save_place, save_time, price, abradability, images, createdAt, updatedAt) " +
            "values (#{categoryId},#{name},#{description},#{save_place},#{save_time},#{price},#{abradability},#{images},#{createdAt},#{updatedAt})")
    void insertGood(goods goods);
}
