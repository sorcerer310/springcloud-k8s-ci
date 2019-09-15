package com.bsu.skc.storage2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class Storage2Application {

    public static void main(String[] args){
        SpringApplication.run(Storage2Application.class,args);
    }
}
