package com.example.schoolmarket.mapper;

import com.example.schoolmarket.entity.Category;
import java.util.List;

public interface CategoryMapper {
    List<Category> selectCategoryList();
    Category selectById(Long id);
    int insert(Category category);
    int update(Category category);
    int delete(Long id);
    List<Category> selectAll();
}