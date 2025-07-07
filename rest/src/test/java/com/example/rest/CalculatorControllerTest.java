package com.example.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.lang.reflect.Field;
import java.util.concurrent.BlockingQueue;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for the CalculatorController REST API.
 * 
 * These tests use MockMvc to simulate HTTP requests and Kafka mocks to emulate Kafka behavior.
*/
@WebMvcTest(CalculatorController.class)
@Import(CalculatorControllerTest.KafkaTemplateMockConfig.class)
class CalculatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CalculatorController controller;

    private BlockingQueue<String> responseQueue;

    /**
     * Uses reflection to access and clear the internal response queue before each test.
    */
    @SuppressWarnings("unchecked")
    @BeforeEach
    void setup() throws Exception {
        Field field = CalculatorController.class.getDeclaredField("responseQueue");
        field.setAccessible(true);
        responseQueue = (BlockingQueue<String>) field.get(controller);
        responseQueue.clear();
    }

    /**
     * Verifies that the /sum endpoint responds with 200 OK.
    */
    @Test
    void testEndpointExists() throws Exception {
        mockMvc.perform(get("/sum?a=1&b=2"))
                .andExpect(status().isOk());
    }

    /**
     * Simulates a successful Kafka response and ensures it is returned by the endpoint.
    */
    @Test
    void testSuccessfulCalculationResponse() throws Exception {
        // Simulate a Kafka response before controller times out
        new Thread(() -> {
            try {
                Thread.sleep(100); // slight delay to simulate async Kafka
                responseQueue.offer("3.0");
            } catch (InterruptedException ignored) {}
        }).start();

        MvcResult result = mockMvc.perform(get("/sum?a=1&b=2"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).contains("3.0");
    }

    /**
     * Simulates an error message returned from Kafka and ensures it is included in the response.
    */
    @Test
    void testErrorResponseFromKafka() throws Exception {
        new Thread(() -> {
            try {
                Thread.sleep(100);
                responseQueue.offer("error: something went wrong");
            } catch (InterruptedException ignored) {}
        }).start();

        MvcResult result = mockMvc.perform(get("/sum?a=1&b=2"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).contains("error");
    }

    /**
     * Tests the timeout behavior if Kafka does not respond in time.
    */
    @Test
    void testTimeoutIfNoKafkaResponse() throws Exception {
        MvcResult result = mockMvc.perform(get("/sum?a=1&b=2"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).contains("Timeout");
    }

    /**
     * Verifies that the Kafka listener correctly updates the internal queue with a new response.
    */
    @Test
    void testListenResponseUpdatesQueue() {
        ConsumerRecord<String, String> record = new ConsumerRecord<>("calc-responses", 0, 0L, "key", "req123|42.0");

        controller.listenResponse(record);
        assertThat(responseQueue.poll()).isEqualTo("42.0");
    }

    /**
     * Provides a mocked KafkaTemplate bean to be injected into the controller.
    */
    @TestConfiguration
    static class KafkaTemplateMockConfig {
        @SuppressWarnings("unchecked")
        @Bean
        public KafkaTemplate<String, String> kafkaTemplate() {
            return mock(KafkaTemplate.class);
        }
    }
}
