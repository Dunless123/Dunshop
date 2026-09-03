package com.example.schoolmarket.service.impl;

import com.example.schoolmarket.entity.FileStorage;
import com.example.schoolmarket.mapper.FileStorageMapper;
import com.example.schoolmarket.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    @Autowired
    private FileStorageMapper fileStorageMapper;

    @Override
    public List<FileStorage> list() {
        return fileStorageMapper.selectList();
    }

    @Override
    public FileStorage getById(Long id) {
        return fileStorageMapper.selectById(id);
    }

    @Override
    public boolean save(FileStorage file) {
        return fileStorageMapper.insert(file) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return fileStorageMapper.delete(id) > 0;
    }
}