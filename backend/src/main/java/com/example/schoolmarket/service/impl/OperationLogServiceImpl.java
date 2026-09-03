package com.example.schoolmarket.service.impl;

import com.example.schoolmarket.entity.OperationLog;
import com.example.schoolmarket.mapper.OperationLogMapper;
import com.example.schoolmarket.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperationLogServiceImpl implements OperationLogService {
    @Autowired
    private OperationLogMapper operationLogMapper;

    @Override
    public List<OperationLog> list() {
        return operationLogMapper.selectList();
    }

    @Override
    public OperationLog getById(Long id) {
        return operationLogMapper.selectById(id);
    }

    @Override
    public boolean save(OperationLog log) {
        return operationLogMapper.insert(log) > 0;
    }

    @Override
    public List<OperationLog> search(String keyword, String date) {
        return operationLogMapper.search(keyword, date);
    }

    @Override
    public List<OperationLog> getByPage(int page, int size) {
        int offset = (page - 1) * size;
        return operationLogMapper.selectByPage(offset, size);
    }

    @Override
    public int getTotalCount() {
        return operationLogMapper.count();
    }

    @Override
    public int getCountByKeyword(String keyword) {
        return operationLogMapper.countByKeyword(keyword);
    }
}