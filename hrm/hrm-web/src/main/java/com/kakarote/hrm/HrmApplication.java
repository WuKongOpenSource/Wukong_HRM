package com.kakarote.hrm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author huangmingbo
 */
@SpringBootApplication
@MapperScan(basePackages = {"com.kakarote.hrm.mapper"})
@EnableFeignClients(basePackages = {"com.kakarote"})
public class HrmApplication {
    public static void main(String[] args) {
        SpringApplication.run(HrmApplication.class, args);
    }
}
