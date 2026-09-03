package com.example.schoolmarket.controller;

import com.example.schoolmarket.entity.Category;
import com.example.schoolmarket.service.CategoryService;
import com.example.schoolmarket.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public HashMap<String, Object> getList() {
        List<Category> categories = categoryService.list();
        return ResponseUtil.success(categories, "获取成功");
    }

    @GetMapping("/{id}")
    public HashMap<String, Object> getDetail(@org.springframework.web.bind.annotation.PathVariable Long id) {
        Category category = categoryService.getById(id);
        return ResponseUtil.success(category, "获取成功");
    }
}