package com.tmsdemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.tmsdemo.mapper")

public class TmsdemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(TmsdemoApplication.class, args);
    }
}
