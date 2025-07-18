package com.ivangochev.raceratingapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class RaceRatingApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RaceRatingApiApplication.class, args);
    }
}
