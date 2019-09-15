package com.bsu.skc.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

@SpringBootApplication
@EnableDiscoveryClient                  //启用eureka对该项目的服务发现
@EnableCircuitBreaker                   //启用Hystrix，应用断路器（熔断）
@EnableHystrix                          //启用Hystrix
public class OrderApplication {
    public static void main(String[] args){
        SpringApplication.run(OrderApplication.class);
    }
}
