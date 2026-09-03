package com.example.schoolmarket.service;

import com.example.schoolmarket.entity.FileStorage;

import java.util.List;

public interface FileStorageService {
    List<FileStorage> list();
    FileStorage getById(Long id);
    boolean save(FileStorage file);
    boolean delete(Long id);
}