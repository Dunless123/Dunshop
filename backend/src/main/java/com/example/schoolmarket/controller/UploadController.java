package com.example.schoolmarket.controller;

import com.example.schoolmarket.entity.FileStorage;
import com.example.schoolmarket.service.FileStorageService;
import com.example.schoolmarket.utils.JwtUtil;
import com.example.schoolmarket.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private FileStorageService fileStorageService;

    private Long getUserIdFromToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        String token = authorization.replace("Bearer ", "");
        return jwtUtil.getUserIdFromToken(token);
    }

    @PostMapping(value = "/image", consumes = "multipart/form-data")
    public HashMap<String, Object> uploadImage(@RequestParam("file") MultipartFile file, @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }

        if (file.isEmpty()) {
            return ResponseUtil.error(400, "文件不能为空");
        }

        String contentType = file.getContentType();
        if (!contentType.startsWith("image/")) {
            return ResponseUtil.error(400, "只支持图片文件");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + extension;

        String uploadDir = System.getProperty("user.dir") + "/uploads/images";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File dest = new File(dir, filename);
        try {
            file.transferTo(dest);
            String filePath = "/uploads/images/" + filename;
            String fullUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(filePath)
                    .toUriString();
            
            saveFileRecord(filename, originalFilename, filePath, file.getSize(), contentType);
            
            HashMap<String, Object> data = new HashMap<>();
            data.put("url", fullUrl);
            return ResponseUtil.success(data, "上传成功");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseUtil.error(500, "上传失败");
        }
    }

    @PostMapping(value = "/avatar", consumes = "multipart/form-data")
    public HashMap<String, Object> uploadAvatar(@RequestParam("file") MultipartFile file, @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }

        if (file.isEmpty()) {
            return ResponseUtil.error(400, "文件不能为空");
        }

        String contentType = file.getContentType();
        if (!contentType.startsWith("image/")) {
            return ResponseUtil.error(400, "只支持图片文件");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = "avatar_" + userId + "_" + UUID.randomUUID().toString() + extension;

        String uploadDir = System.getProperty("user.dir") + "/uploads/avatars";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File dest = new File(dir, filename);
        try {
            file.transferTo(dest);
            String filePath = "/uploads/avatars/" + filename;
            String fullUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(filePath)
                    .toUriString();
            
            saveFileRecord(filename, originalFilename, filePath, file.getSize(), contentType);
            
            HashMap<String, Object> data = new HashMap<>();
            data.put("url", fullUrl);
            return ResponseUtil.success(data, "上传成功");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseUtil.error(500, "上传失败");
        }
    }

    @PostMapping(value = "/file", consumes = "multipart/form-data")
    public HashMap<String, Object> uploadFile(@RequestParam("file") MultipartFile file, @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return ResponseUtil.error(401, "未授权");
        }

        if (file.isEmpty()) {
            return ResponseUtil.error(400, "文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + extension;

        String uploadDir = System.getProperty("user.dir") + "/uploads/files";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File dest = new File(dir, filename);
        try {
            file.transferTo(dest);
            String filePath = "/uploads/files/" + filename;
            String fullUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(filePath)
                    .toUriString();
            
            saveFileRecord(filename, originalFilename, filePath, file.getSize(), file.getContentType());
            
            HashMap<String, Object> data = new HashMap<>();
            data.put("url", fullUrl);
            return ResponseUtil.success(data, "上传成功");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseUtil.error(500, "上传失败");
        }
    }

    private void saveFileRecord(String fileName, String originalName, String filePath, long fileSize, String fileType) {
        FileStorage fileStorage = new FileStorage();
        fileStorage.setFileName(fileName);
        fileStorage.setOriginalName(originalName);
        fileStorage.setFilePath(filePath);
        fileStorage.setFileSize(fileSize);
        fileStorage.setFileType(fileType);
        fileStorage.setStatus(1);
        fileStorageService.save(fileStorage);
    }
}