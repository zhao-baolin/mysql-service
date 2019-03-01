package com.mysql.zhaobaolin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.mysql.zhaobaolin.mapper")
public class ZhaobaolinApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZhaobaolinApplication.class, args);
    }
}
