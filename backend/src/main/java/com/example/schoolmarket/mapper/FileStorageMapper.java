package com.example.schoolmarket.mapper;

import com.example.schoolmarket.entity.FileStorage;
import java.util.List;

public interface FileStorageMapper {
    List<FileStorage> selectList();
    FileStorage selectById(Long id);
    int insert(FileStorage file);
    int delete(Long id);
}