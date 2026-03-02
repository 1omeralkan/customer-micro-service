package com.omeralkan.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CustomerMicroServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerMicroServiceApplication.class, args);
    }

}
