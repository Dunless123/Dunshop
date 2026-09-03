package com.example.schoolmarket.mapper;

import com.example.schoolmarket.entity.Address;
import java.util.List;

public interface AddressMapper {
    List<Address> selectByUserId(Long userId);
    Address selectById(Long id);
    int insert(Address address);
    int update(Address address);
    int delete(Long id);
}