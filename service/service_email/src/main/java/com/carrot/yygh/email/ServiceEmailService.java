package com.carrot.yygh.email;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan(basePackages = "com.carrot")
@EnableDiscoveryClient
public class ServiceEmailService {
    public static void main(String[] args) {
        SpringApplication.run(ServiceEmailService.class, args);
    }
}
