package com.example.schoolmarket.controller;

import com.example.schoolmarket.entity.Notification;
import com.example.schoolmarket.entity.NotificationTemplate;
import com.example.schoolmarket.service.NotificationService;
import com.example.schoolmarket.utils.JwtUtil;
import com.example.schoolmarket.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private JwtUtil jwtUtil;

    private Long getUserIdFromToken(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            return jwtUtil.getUserIdFromToken(token);
        }
        return null;
    }

    @GetMapping("/list")
    public HashMap<String, Object> getList(@RequestHeader("Authorization") String authorization,
                                          @RequestParam(required = false, defaultValue = "1") Integer page,
                                          @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                          @RequestParam(required = false) String type,
                                          @RequestParam(required = false) String status) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未登录");
        }
        
        int offset = (page - 1) * pageSize;
        List<Notification> notifications = notificationService.getList(offset, pageSize, userId, type, status);
        int total = notificationService.getCount(userId, type, status);
        
        HashMap<String, Object> data = new HashMap<>();
        data.put("list", notifications);
        data.put("total", total);
        data.put("page", page);
        data.put("pageSize", pageSize);
        data.put("totalPages", (total + pageSize - 1) / pageSize);
        
        return ResponseUtil.success(data, "获取成功");
    }

    @GetMapping("/{id}")
    public HashMap<String, Object> getDetail(@PathVariable Long id) {
        Notification notification = notificationService.getById(id);
        if (notification != null) {
            return ResponseUtil.success(notification, "获取成功");
        }
        return ResponseUtil.error(404, "通知不存在");
    }

    @PostMapping("/send")
    public HashMap<String, Object> sendNotification(@RequestBody Map<String, Object> params) {
        if (!params.containsKey("userId") || params.get("userId") == null) {
            return ResponseUtil.error(400, "用户ID不能为空");
        }
        if (!params.containsKey("type") || params.get("type") == null) {
            return ResponseUtil.error(400, "通知类型不能为空");
        }
        if (!params.containsKey("content") || params.get("content") == null) {
            return ResponseUtil.error(400, "通知内容不能为空");
        }
        
        Long userId = Long.parseLong(params.get("userId").toString());
        String type = (String) params.get("type");
        String content = (String) params.get("content");
        
        boolean success = notificationService.sendNotification(userId, type, content);
        if (success) {
            return ResponseUtil.success(null, "发送成功");
        }
        return ResponseUtil.error(400, "发送失败");
    }

    @PostMapping("/{id}/resend")
    public HashMap<String, Object> resendNotification(@PathVariable Long id) {
        boolean success = notificationService.resendNotification(id);
        if (success) {
            return ResponseUtil.success(null, "重发成功");
        }
        return ResponseUtil.error(400, "重发失败");
    }

    @DeleteMapping("/{id}")
    public HashMap<String, Object> deleteNotification(@PathVariable Long id) {
        boolean success = notificationService.delete(id);
        if (success) {
            return ResponseUtil.success(null, "删除成功");
        }
        return ResponseUtil.error(400, "删除失败");
    }

    @GetMapping("/templates")
    public HashMap<String, Object> getTemplates() {
        List<NotificationTemplate> templates = notificationService.getTemplates();
        return ResponseUtil.success(templates, "获取成功");
    }

    @GetMapping("/templates/{id}")
    public HashMap<String, Object> getTemplateById(@PathVariable Long id) {
        NotificationTemplate template = notificationService.getTemplateById(id);
        if (template != null) {
            return ResponseUtil.success(template, "获取成功");
        }
        return ResponseUtil.error(404, "模板不存在");
    }

    @PostMapping("/templates")
    public HashMap<String, Object> addTemplate(@RequestBody NotificationTemplate template) {
        if (template.getName() == null || template.getName().isEmpty()) {
            return ResponseUtil.error(400, "模板名称不能为空");
        }
        if (template.getContent() == null || template.getContent().isEmpty()) {
            return ResponseUtil.error(400, "模板内容不能为空");
        }
        
        boolean success = notificationService.addTemplate(template);
        if (success) {
            return ResponseUtil.success(template, "添加成功");
        }
        return ResponseUtil.error(400, "添加失败");
    }

    @PutMapping("/templates/{id}")
    public HashMap<String, Object> updateTemplate(@PathVariable Long id, @RequestBody NotificationTemplate template) {
        template.setId(id);
        boolean success = notificationService.updateTemplate(template);
        if (success) {
            return ResponseUtil.success(null, "更新成功");
        }
        return ResponseUtil.error(400, "更新失败");
    }

    @DeleteMapping("/templates/{id}")
    public HashMap<String, Object> deleteTemplate(@PathVariable Long id) {
        boolean success = notificationService.deleteTemplate(id);
        if (success) {
            return ResponseUtil.success(null, "删除成功");
        }
        return ResponseUtil.error(400, "删除失败");
    }
}