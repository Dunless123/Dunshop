package com.example.schoolmarket.mapper;

import com.example.schoolmarket.entity.OperationLog;
import java.util.List;

public interface OperationLogMapper {
    List<OperationLog> selectList();
    OperationLog selectById(Long id);
    int insert(OperationLog log);
    List<OperationLog> search(String keyword, String date);
    List<OperationLog> selectByPage(int offset, int limit);
    int count();
    int countByKeyword(String keyword);
}