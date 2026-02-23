package com.omeralkan.customer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {

    @GetMapping("/test")
    public String testServisi() {
        return "Müşteri Servisi (Customer Microservice) Başarıyla Çalışıyor!";
    }
}