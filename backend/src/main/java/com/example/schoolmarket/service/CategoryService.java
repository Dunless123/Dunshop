package com.example.schoolmarket.service;

import com.example.schoolmarket.entity.Category;

import java.util.List;

public interface CategoryService {
    Category getById(Long id);
    boolean save(Category category);
    boolean update(Category category);
    boolean delete(Long id);
    List<Category> list();
}