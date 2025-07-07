package com.example.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Entry point for the REST module of the Kafka Calculator application.
 * 
 * This module exposes HTTP endpoints that receive arithmetic operation requests
 * and communicates with the calculator module via Kafka to process them.
*/
@SpringBootApplication
public class RestApplication {

    /**
     * Main method that boots up the Spring Boot application.
     *
     * @param args Command-line arguments (not used here)
    */
    public static void main(String[] args) {
        SpringApplication.run(RestApplication.class, args);
    }
}