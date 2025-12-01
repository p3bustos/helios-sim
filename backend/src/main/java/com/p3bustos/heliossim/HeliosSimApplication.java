package com.p3bustos.heliossim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class HeliosSimApplication {

    public static void main(String[] args) {
        SpringApplication.run(HeliosSimApplication.class, args);
    }
}