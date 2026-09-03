package com.example.schoolmarket.controller;

import com.example.schoolmarket.entity.*;
import com.example.schoolmarket.service.*;
import com.example.schoolmarket.utils.JwtUtil;
import com.example.schoolmarket.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/system")
public class SystemController {

    @Autowired
    private CampusService campusService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private UserService userService;
    @Autowired
    private PickupPointService pickupPointService;
    @Autowired
    private OperationLogService operationLogService;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private JwtUtil jwtUtil;

    // ==================== 校区信息管理 ====================

    @GetMapping("/campus")
    public HashMap<String, Object> getCampusList() {
        List<Campus> campusList = campusService.list();
        return ResponseUtil.success(campusList, "获取成功");
    }

    @PostMapping("/campus")
    public HashMap<String, Object> addCampus(@RequestBody Campus campus) {
        boolean success = campusService.save(campus);
        if (success) {
            return ResponseUtil.success(campus, "添加成功");
        }
        return ResponseUtil.error(400, "添加失败");
    }

    @PutMapping("/campus/{id}")
    public HashMap<String, Object> updateCampus(@PathVariable Long id, @RequestBody Campus campus) {
        campus.setId(id);
        boolean success = campusService.update(campus);
        if (success) {
            return ResponseUtil.success(campus, "更新成功");
        }
        return ResponseUtil.error(400, "更新失败");
    }

    @DeleteMapping("/campus/{id}")
    public HashMap<String, Object> deleteCampus(@PathVariable Long id) {
        boolean success = campusService.delete(id);
        if (success) {
            return ResponseUtil.success(null, "删除成功");
        }
        return ResponseUtil.error(400, "删除失败");
    }

    // ==================== 自提点设置 ====================

    @GetMapping("/pickup")
    public HashMap<String, Object> getPickupList() {
        List<Pickup> pickupList = pickupPointService.list();
        return ResponseUtil.success(pickupList, "获取成功");
    }

    @PostMapping("/pickup")
    public HashMap<String, Object> addPickup(@RequestBody Pickup pickup) {
        boolean success = pickupPointService.save(pickup);
        if (success) {
            return ResponseUtil.success(pickup, "添加成功");
        }
        return ResponseUtil.error(400, "添加失败");
    }

    @PutMapping("/pickup/{id}")
    public HashMap<String, Object> updatePickup(@PathVariable Long id, @RequestBody Pickup pickup) {
        pickup.setId(id);
        boolean success = pickupPointService.update(pickup);
        if (success) {
            return ResponseUtil.success(pickup, "更新成功");
        }
        return ResponseUtil.error(400, "更新失败");
    }

    @DeleteMapping("/pickup/{id}")
    public HashMap<String, Object> deletePickup(@PathVariable Long id) {
        boolean success = pickupPointService.delete(id);
        if (success) {
            return ResponseUtil.success(null, "删除成功");
        }
        return ResponseUtil.error(400, "删除失败");
    }

    // ==================== 操作日志 ====================

    @GetMapping("/logs")
    public HashMap<String, Object> getOperationLogs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String date,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        HashMap<String, Object> result = new HashMap<>();
        List<OperationLog> logs;
        int total;
        
        if ((keyword != null && !keyword.isEmpty()) || (date != null && !date.isEmpty())) {
            logs = operationLogService.search(keyword, date);
            total = logs.size();
            int offset = (page - 1) * size;
            if (offset < logs.size()) {
                int end = Math.min(offset + size, logs.size());
                logs = logs.subList(offset, end);
            } else {
                logs = java.util.Collections.emptyList();
            }
        } else {
            logs = operationLogService.getByPage(page, size);
            total = operationLogService.getTotalCount();
        }
        
        result.put("data", logs);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        
        return ResponseUtil.success(result, "获取成功");
    }

    // ==================== 文件存储管理 ====================

    @GetMapping("/files")
    public HashMap<String, Object> getFileList() {
        List<FileStorage> files = fileStorageService.list();
        return ResponseUtil.success(files, "获取成功");
    }

    @GetMapping("/files/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        FileStorage fileStorage = fileStorageService.getById(id);
        if (fileStorage == null) {
            return ResponseEntity.notFound().build();
        }
        
        String filePath = System.getProperty("user.dir") + fileStorage.getFilePath();
        File file = new File(filePath);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        
        Resource resource = new FileSystemResource(file);
        
        String contentType = fileStorage.getFileType();
        if (contentType == null || contentType.isEmpty()) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        
        String originalFileName = fileStorage.getOriginalName();
        String encodedFileName = null;
        try {
            encodedFileName = java.net.URLEncoder.encode(originalFileName, "UTF-8").replace("+", "%20");
        } catch (Exception e) {
            encodedFileName = originalFileName;
        }
        
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName)
                .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                .body(resource);
    }

    @DeleteMapping("/files/{id}")
    public HashMap<String, Object> deleteFile(@PathVariable Long id) {
        FileStorage fileStorage = fileStorageService.getById(id);
        if (fileStorage == null) {
            return ResponseUtil.error(404, "文件不存在");
        }
        
        String filePath = System.getProperty("user.dir") + fileStorage.getFilePath();
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        
        boolean success = fileStorageService.delete(id);
        if (success) {
            return ResponseUtil.success(null, "删除成功");
        }
        return ResponseUtil.error(400, "删除失败");
    }

    // ==================== 数据统计 ====================

    @GetMapping("/stats")
    public HashMap<String, Object> getStats() {
        HashMap<String, Object> stats = new HashMap<>();
        
        long goodsCount = goodsService.getCount(null, null, null);
        long userCount = userService.getTotalUserCount();
        long campusCount = campusService.count();
        
        stats.put("goodsCount", goodsCount);
        stats.put("userCount", userCount);
        stats.put("campusCount", campusCount);
        
        return ResponseUtil.success(stats, "获取成功");
    }
}