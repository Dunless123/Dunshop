package com.example.schoolmarket.controller;

import com.example.schoolmarket.entity.Address;
import com.example.schoolmarket.entity.Pickup;
import com.example.schoolmarket.entity.User;
import com.example.schoolmarket.service.AddressService;
import com.example.schoolmarket.service.UserPickupPointService;
import com.example.schoolmarket.service.UserService;
import com.example.schoolmarket.utils.JwtUtil;
import com.example.schoolmarket.utils.PasswordUtil;
import com.example.schoolmarket.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private UserPickupPointService userPickupPointService;
    @Autowired
    private JwtUtil jwtUtil;

    private Long getUserIdFromToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        String token = authorization.replace("Bearer ", "");
        return jwtUtil.getUserIdFromToken(token);
    }

    @GetMapping("/info")
    public HashMap<String, Object> getInfo(@RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }
        User user = userService.getById(userId);
        return ResponseUtil.success(user, "获取成功");
    }

    @GetMapping("/{id}")
    public HashMap<String, Object> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) {
            return ResponseUtil.success(user, "获取成功");
        }
        return ResponseUtil.error(404, "用户不存在");
    }

    @PutMapping("/update")
    public HashMap<String, Object> update(@RequestBody User user, @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }
        user.setId(userId);
        boolean success = userService.update(user);
        if (success) {
            return ResponseUtil.success(null, "更新成功");
        }
        return ResponseUtil.error(400, "更新失败");
    }

    @GetMapping("/addresses")
    public HashMap<String, Object> getAddresses(@RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }
        List<Address> addresses = addressService.getByUserId(userId);
        return ResponseUtil.success(addresses, "获取成功");
    }

    @PostMapping("/addresses")
    public HashMap<String, Object> addAddress(@RequestBody Address address, @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }
        address.setUserId(userId);
        boolean success = addressService.save(address);
        if (success) {
            if (address.getIsDefault()) {
                addressService.setDefault(address.getId(), userId);
            }
            return ResponseUtil.success(address, "添加成功");
        }
        return ResponseUtil.error(400, "添加失败");
    }

    @PutMapping("/addresses/{id}")
    public HashMap<String, Object> updateAddress(@PathVariable Long id, @RequestBody Address address, @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }
        address.setId(id);
        address.setUserId(userId);
        boolean success = addressService.update(address);
        if (success) {
            if (address.getIsDefault()) {
                addressService.setDefault(id, userId);
            }
            return ResponseUtil.success(null, "更新成功");
        }
        return ResponseUtil.error(400, "更新失败");
    }

    @DeleteMapping("/addresses/{id}")
    public HashMap<String, Object> deleteAddress(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }
        Address address = addressService.getById(id);
        if (address != null && address.getUserId().equals(userId)) {
            boolean success = addressService.delete(id);
            if (success) {
                return ResponseUtil.success(null, "删除成功");
            }
        }
        return ResponseUtil.error(400, "删除失败");
    }

    @GetMapping("/{id}/stats")
    public HashMap<String, Object> getUserStats(@PathVariable Long id) {
        Map<String, Object> stats = userService.getUserStats(id);
        return ResponseUtil.success(stats, "获取成功");
    }

    @PostMapping("/change-password")
    public HashMap<String, Object> changePassword(@RequestBody HashMap<String, String> params, @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        if (oldPassword == null || newPassword == null) {
            return ResponseUtil.error(400, "旧密码和新密码不能为空");
        }
        User user = userService.getById(userId);
        if (user != null) {
            // 验证旧密码
            PasswordUtil passwordUtil = new PasswordUtil();
            if (passwordUtil.matches(oldPassword, user.getPassword())) {
                // 更新密码
                user.setPassword(passwordUtil.encode(newPassword));
                boolean success = userService.update(user);
                if (success) {
                    return ResponseUtil.success(null, "密码修改成功");
                }
            } else {
                return ResponseUtil.error(400, "旧密码错误");
            }
        }
        return ResponseUtil.error(400, "密码修改失败");
    }

    @GetMapping("/auth/status")
    public HashMap<String, Object> getAuthStatus(@RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }
        User user = userService.getById(userId);
        if (user != null) {
            HashMap<String, Object> result = new HashMap<>();
            String authStatus = user.getAuthStatus() != null ? user.getAuthStatus() : "未认证";
            result.put("authStatus", authStatus);
            
            // 转换为前端期望的状态值
            String status;
            switch (authStatus) {
                case "已通过":
                case "已认证":
                    status = "approved";
                    break;
                case "已拒绝":
                    status = "rejected";
                    break;
                case "待审核":
                default:
                    status = "pending";
                    break;
            }
            result.put("status", status);
            result.put("studentId", user.getStudentId());
            result.put("name", user.getUsername());
            result.put("createTime", user.getCreateTime());
            
            return ResponseUtil.success(result, "获取成功");
        }
        return ResponseUtil.error(404, "用户不存在");
    }

    @GetMapping("/pickup-points")
    public HashMap<String, Object> getFavoritePickupPoints(@RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }
        List<Pickup> pickupPoints = userPickupPointService.getPickupPointsByUserId(userId);
        return ResponseUtil.success(pickupPoints, "获取成功");
    }

    @PostMapping("/pickup-points/{pickupId}")
    public HashMap<String, Object> addFavoritePickupPoint(@PathVariable Long pickupId, @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }
        boolean success = userPickupPointService.addPickupPoint(userId, pickupId);
        if (success) {
            return ResponseUtil.success(null, "添加成功");
        }
        return ResponseUtil.error(400, "添加失败，该自提点已添加");
    }

    @DeleteMapping("/pickup-points/{pickupId}")
    public HashMap<String, Object> removeFavoritePickupPoint(@PathVariable Long pickupId, @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }
        boolean success = userPickupPointService.removePickupPoint(userId, pickupId);
        if (success) {
            return ResponseUtil.success(null, "移除成功");
        }
        return ResponseUtil.error(400, "移除失败");
    }
}