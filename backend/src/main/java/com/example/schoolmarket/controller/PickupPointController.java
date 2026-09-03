package com.example.schoolmarket.controller;

import com.example.schoolmarket.entity.Pickup;
import com.example.schoolmarket.service.PickupPointService;
import com.example.schoolmarket.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pickup-points")
public class PickupPointController {
    @Autowired
    private PickupPointService pickupPointService;

    @GetMapping("/all")
    public HashMap<String, Object> getAll() {
        List<Pickup> pickupPoints = pickupPointService.list();
        return ResponseUtil.success(pickupPoints, "获取成功");
    }

    @GetMapping("/list")
    public HashMap<String, Object> getList() {
        List<Pickup> pickupPoints = pickupPointService.list();
        return ResponseUtil.success(pickupPoints, "获取成功");
    }

    @GetMapping("/{id}")
    public HashMap<String, Object> getById(@PathVariable Long id) {
        Pickup pickup = pickupPointService.getById(id);
        if (pickup != null) {
            return ResponseUtil.success(pickup, "获取成功");
        }
        return ResponseUtil.error(404, "自提点不存在");
    }

    @PostMapping("/add")
    public HashMap<String, Object> add(@RequestBody Map<String, String> params) {
        String name = params.get("name");
        String address = params.get("address");
        
        if (name == null || name.isEmpty()) {
            return ResponseUtil.error(400, "自提点名称不能为空");
        }
        if (address == null || address.isEmpty()) {
            return ResponseUtil.error(400, "自提点地址不能为空");
        }
        
        Pickup pickup = new Pickup();
        pickup.setName(name);
        pickup.setAddress(address);
        
        boolean success = pickupPointService.save(pickup);
        if (success) {
            return ResponseUtil.success(pickup, "添加成功");
        }
        return ResponseUtil.error(400, "添加失败");
    }

    @PutMapping("/{id}")
    public HashMap<String, Object> update(@PathVariable Long id, @RequestBody Map<String, String> params) {
        Pickup pickup = pickupPointService.getById(id);
        if (pickup == null) {
            return ResponseUtil.error(404, "自提点不存在");
        }
        
        String name = params.get("name");
        String address = params.get("address");
        
        if (name != null && !name.isEmpty()) {
            pickup.setName(name);
        }
        if (address != null && !address.isEmpty()) {
            pickup.setAddress(address);
        }
        
        boolean success = pickupPointService.update(pickup);
        if (success) {
            return ResponseUtil.success(null, "更新成功");
        }
        return ResponseUtil.error(400, "更新失败");
    }

    @DeleteMapping("/{id}")
    public HashMap<String, Object> delete(@PathVariable Long id) {
        Pickup pickup = pickupPointService.getById(id);
        if (pickup == null) {
            return ResponseUtil.error(404, "自提点不存在");
        }
        
        boolean success = pickupPointService.delete(id);
        if (success) {
            return ResponseUtil.success(null, "删除成功");
        }
        return ResponseUtil.error(400, "删除失败");
    }
}