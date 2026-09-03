package com.example.schoolmarket.controller;

import com.example.schoolmarket.entity.Campus;
import com.example.schoolmarket.service.CampusService;
import com.example.schoolmarket.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/campus")
public class CampusController {
    @Autowired
    private CampusService campusService;

    @GetMapping("/list")
    public HashMap<String, Object> getList() {
        List<Campus> campuses = campusService.list();
        return ResponseUtil.success(campuses, "获取成功");
    }

    @GetMapping("/all")
    public HashMap<String, Object> getAll() {
        List<Campus> campuses = campusService.list();
        return ResponseUtil.success(campuses, "获取成功");
    }

    @GetMapping("/{id}")
    public HashMap<String, Object> getDetail(@PathVariable Long id) {
        Campus campus = campusService.getById(id);
        return ResponseUtil.success(campus, "获取成功");
    }
}