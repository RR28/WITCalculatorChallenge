package com.example.calculator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Entry point for the Calculator module of the application.
 * 
 * This module is responsible for listening to Kafka topics,
 * processing arithmetic operations, and publishing results.
*/
@SpringBootApplication
public class CalculatorApplication {

    /**
     * Main method that launches the Spring Boot application.
     * 
     * @param args Command-line arguments (not used here)
    */
    public static void main(String[] args) {
        SpringApplication.run(CalculatorApplication.class, args);
    }
}
