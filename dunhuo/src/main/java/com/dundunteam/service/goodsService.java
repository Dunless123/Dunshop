package com.dundunteam.service;

import com.dundunteam.pojo.dto.goodsDto;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


public interface goodsService {
    void addGood(goodsDto goodsdto);
}
