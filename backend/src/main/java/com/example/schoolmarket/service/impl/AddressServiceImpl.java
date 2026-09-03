package com.example.schoolmarket.service.impl;

import com.example.schoolmarket.entity.Address;
import com.example.schoolmarket.mapper.AddressMapper;
import com.example.schoolmarket.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private AddressMapper addressMapper;

    @Override
    public List<Address> getByUserId(Long userId) {
        return addressMapper.selectByUserId(userId);
    }

    @Override
    public Address getDefaultByUserId(Long userId) {
        List<Address> addresses = addressMapper.selectByUserId(userId);
        for (Address address : addresses) {
            if (address.getIsDefault()) {
                return address;
            }
        }
        return null;
    }

    @Override
    public void setDefault(Long addressId, Long userId) {
        // 先获取所有地址
        List<Address> addresses = addressMapper.selectByUserId(userId);
        // 将所有地址设置为非默认
        for (Address address : addresses) {
            address.setIsDefault(false);
            addressMapper.update(address);
        }
        // 再将指定地址设置为默认
        Address address = addressMapper.selectById(addressId);
        if (address != null) {
            address.setIsDefault(true);
            addressMapper.update(address);
        }
    }

    @Override
    public Address getById(Long id) {
        return addressMapper.selectById(id);
    }

    @Override
    public boolean save(Address address) {
        return addressMapper.insert(address) > 0;
    }

    @Override
    public boolean update(Address address) {
        return addressMapper.update(address) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return addressMapper.delete(id) > 0;
    }
}