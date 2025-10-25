package com.example.busconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BusConnectApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusConnectApplication.class, args);
    }

}
