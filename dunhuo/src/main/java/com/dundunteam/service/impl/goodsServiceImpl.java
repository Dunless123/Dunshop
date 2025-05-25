package com.dundunteam.service.impl;

import com.dundunteam.common.constant.StatusConstant;
import com.dundunteam.mapper.goodsMapper;
import com.dundunteam.pojo.dto.goodsDto;
import com.dundunteam.pojo.entity.goods;
import com.dundunteam.service.goodsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class goodsServiceImpl implements goodsService {
    @Autowired
    private goodsMapper goodsMapper;


    public void addGood(goodsDto goodsdto) {
        goods goods=new goods();
        BeanUtils.copyProperties(goodsdto,goods);
        //商品状态，默认上线:1
        goods.setStatus(StatusConstant.ENABLE);
        LocalDateTime now=LocalDateTime.now();
        DateTimeFormatter formatter =DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattDateTime=now.format(formatter);
        String ImagesString = null;
        try {
            ImagesString = new ObjectMapper().writeValueAsString(goodsdto.getImages());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        goods.setImages(ImagesString);
        //创建时间
        goods.setCreate_Time(formattDateTime);
        goodsMapper.insertGood(goods);
    }
}
