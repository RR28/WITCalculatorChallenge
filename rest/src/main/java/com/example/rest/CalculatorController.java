package com.example.rest;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@RestController
public class CalculatorController {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    // A blocking queue to temporarily store the result received from Kafka
    private final BlockingQueue<String> responseQueue = new ArrayBlockingQueue<>(1);

    private static final Logger logger = LoggerFactory.getLogger(CalculatorController.class);


     /**
     * HTTP GET endpoint for arithmetic operations.
     * It sends a calculation request to Kafka and waits for the result.
     *
     * @param operation the operation to perform (sum, sub, mul, div)
     * @param a the first operand
     * @param b the second operand
     * @param response the HTTP response object to add headers
     * @return a Map containing either the result or an error message
     * @throws InterruptedException if polling is interrupted
    */

    @GetMapping("/{operation}")
    public Map<String, String> calculate(@PathVariable String operation, @RequestParam BigDecimal a,
            @RequestParam BigDecimal b, HttpServletResponse response) throws InterruptedException {

        // Generate a unique ID to trace the request across services        
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId); // Add to logging context
        response.setHeader("X-Request-ID", requestId); // Add to HTTP response headers

        // Format request as operation,a,b (e.g., sum,3.0,2.0)
        String request = String.format("%s,%f,%f", operation, a, b);
        logger.info("Received request: {}", request);

        // Send message to Kafka topic "calc-requests"
        kafkaTemplate.send("calc-requests", requestId + "|" + request);

        // Wait up to 5 seconds for a response from Kafka
        String result = responseQueue.poll(5, TimeUnit.SECONDS);
        MDC.clear(); // Always clear MDC context

        // If timeout occurs (no response received)
        if (result == null) {
            logger.warn("Timeout for requestId={}", requestId);
            return Map.of("error", "Timeout waiting for response");
        }

        // If calculator module returned an error
        if (result.startsWith("error")) {
            logger.error("Error from calculator: {}", result);
            return Map.of("error", result);
        }

        // Return successful result
        logger.info("Returning result: {}", result);
        return Map.of("result", result);
    }

    /**
     * Kafka listener for "calc-responses" topic.
     * Receives the result and places it into the blocking queue.
     *
     * @param record the Kafka message containing requestId|result
    */

    @KafkaListener(topics = "calc-responses", groupId = "rest")
    public void listenResponse(ConsumerRecord<String, String> record) {
        String value = record.value();
        String[] parts = value.split("\\|", 2);
        String requestId = parts[0];
        String result = parts[1];

        // Put requestId into MDC for traceable logging
        MDC.put("requestId", requestId);
        logger.debug("Received response: {}", result);

        // Offer result to queue (for the waiting controller thread)
        responseQueue.offer(result);

        // Clean up logging context
        MDC.clear();
    }
}
