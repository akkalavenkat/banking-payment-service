package com.banking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Banking Payment Service - Main Application Class
 * 
 * This is a comprehensive banking and payment service built with Spring Boot 3,
 * featuring REST APIs, JPA/Hibernate ORM, PostgreSQL persistence, Redis caching,
 * and OAuth2/JWT security.
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
public class BankingPaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankingPaymentServiceApplication.class, args);
    }
}
