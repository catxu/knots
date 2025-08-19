package com.knots;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.knots.mapper")
public class KnotsApplication {
    public static void main(String[] args) {
        SpringApplication.run(KnotsApplication.class, args);
    }
}
