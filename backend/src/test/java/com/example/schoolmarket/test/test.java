package com.example.schoolmarket.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/test")
public class test {

    @GetMapping("/hello")
    public String S1(String s1) {

        return s1;
    }

}
