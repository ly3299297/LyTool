package com.ly.gpt.bootstrap;


import org.springframework.boot.SpringApplication;

/**
 * 排除 原生Druid的快速配置类。
 * DruidDataSourceAutoConfigure会注入一个DataSourceWrapper，其会在原生的spring.datasource下找url,username,password等。而我们动态数据源的配置路径是变化的
 */

public class AIManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AIManagerApplication.class, args);
    }
}
