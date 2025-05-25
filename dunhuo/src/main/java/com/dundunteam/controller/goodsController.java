package com.dundunteam.controller;


import com.dundunteam.common.context.Result;
import com.dundunteam.pojo.dto.goodsDto;
import com.dundunteam.service.goodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RequestMapping("/goods")
@RestController
public class goodsController {
    @Autowired
    private goodsService goodsService;
    @PostMapping("/addGoods")
    public Result addGoods(@RequestBody goodsDto goodsdto){
        goodsService.addGood(goodsdto);
        return  Result.success();
    }
}
