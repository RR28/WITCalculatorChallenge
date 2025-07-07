package com.example.calculator;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka listener that processes arithmetic calculation requests received
 * via the "calc-requests" topic and sends the results to the "calc-responses" topic.
 */

@Component
public class CalculatorKafkaListener {

    // Injects the business logic for performing calculations
    @Autowired
    private CalculatorService calculatorService;

    // KafkaTemplate used to send responses back to the response topic
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    // Logger for structured and traceable logging
    private static final Logger logger = LoggerFactory.getLogger(CalculatorKafkaListener.class);


    /**
     * Listens for incoming calculation requests from Kafka.
     * Each message is expected in the format: "requestId|operation,a,b"
     * Example: "req123|sum,4,5"
     *
     * @param message the raw message from Kafka
    */

    @KafkaListener(topics = "calc-requests", groupId = "calculator")
    public void listen(String message) {
        // Split the message into requestId and calculation payload
        String[] split = message.split("\\|", 2);
        String requestId = split[0];
        String payload = split[1];

        // Add requestId to MDC for contextual logging
        MDC.put("requestId", requestId);
        logger.info("Received: {}", payload);

        try {
            // Parse the payload into operation and operands
            String[] parts = payload.split(",");
            String operation = parts[0];
            BigDecimal a = new BigDecimal(parts[1]);
            BigDecimal b = new BigDecimal(parts[2]);

            // Perform the calculation
            BigDecimal result = calculatorService.calculate(operation, a, b);

            // Send the result back to the response topic
            kafkaTemplate.send("calc-responses", requestId + "|" + result);
            logger.info("Sent result: {}", result);
        } catch (Exception e) {
            // On error, send an error message back instead of a result
            logger.error("Processing error", e);
            kafkaTemplate.send("calc-responses", requestId + "|" + "error: " + e.getMessage());
        } finally {
            // Clean up the logging context
            MDC.clear();
        }
    }
}
