package com.example.schoolmarket.service;

import com.example.schoolmarket.entity.Address;

import java.util.List;

public interface AddressService {
    Address getById(Long id);
    boolean save(Address address);
    boolean update(Address address);
    boolean delete(Long id);
    List<Address> getByUserId(Long userId);
    Address getDefaultByUserId(Long userId);
    void setDefault(Long addressId, Long userId);
}