package com.api.hitshot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HitShotApplication {

    public static void main(String[] args) {
        SpringApplication.run(HitShotApplication.class, args);
    }

}
