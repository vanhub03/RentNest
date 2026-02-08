package com.example.rentnest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RentNestApplication {

    public static void main(String[] args) {
        SpringApplication.run(RentNestApplication.class, args);
    }

}
