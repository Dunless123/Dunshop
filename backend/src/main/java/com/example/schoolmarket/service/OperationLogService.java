package com.example.schoolmarket.service;

import com.example.schoolmarket.entity.OperationLog;

import java.util.List;

public interface OperationLogService {
    List<OperationLog> list();
    OperationLog getById(Long id);
    boolean save(OperationLog log);
    List<OperationLog> search(String keyword, String date);
    List<OperationLog> getByPage(int page, int size);
    int getTotalCount();
    int getCountByKeyword(String keyword);
}