package com.kakarote.core.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 项目基础配置类
 * @author zhangzhiwei
 */
@Configuration
@EnableFeignClients(basePackages = {"com.kakarote.core.feign"})
@ComponentScan(basePackages = {"com.kakarote.core"})
public class CommonWebAutoConfiguration {



}
