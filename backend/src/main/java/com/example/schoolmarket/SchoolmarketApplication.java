package com.example.schoolmarket;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@MapperScan("com.example.schoolmarket.mapper")
@SpringBootApplication
@EnableAspectJAutoProxy
@EnableAsync
public class SchoolmarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchoolmarketApplication.class, args);
    }

}
