package com.example.calculator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;


/**
 * Unit tests for the CalculatorKafkaListener class.
 * 
 * This test class verifies how the listener processes Kafka messages,
 * including valid operations, invalid operations, and malformed input.
*/
public class CalculatorKafkaListenerTest {

    // Mocked dependencies
    @Mock
    private CalculatorService calculatorService;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    // The system under test, with mocks injected
    @InjectMocks
    private CalculatorKafkaListener listener;

    /**
    * Initializes mocks before each test.
    */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Tests a valid message containing a supported operation.
     * Verifies that the result is correctly sent back through Kafka.
    */

    @Test
    void testValidMessageProcessing() {
        String requestId = "abc123";
        String message = requestId + "|sum,2,3";

        when(calculatorService.calculate("sum", new BigDecimal("2"), new BigDecimal("3")))
                .thenReturn(new BigDecimal("5"));

        listener.listen(message);

        verify(kafkaTemplate).send("calc-responses", requestId + "|5");
    }

    /**
     * Tests handling of an invalid operation.
     * Verifies that an error message is sent back.
    */

    @Test
    void testErrorMessageOnInvalidOperation() {
        String requestId = "err1";
        String message = requestId + "|badop,1,1";

        when(calculatorService.calculate(eq("badop"), any(), any()))
                .thenThrow(new IllegalArgumentException("Unsupported operation"));

        listener.listen(message);

        verify(kafkaTemplate).send(eq("calc-responses"), contains("error"));
    }

    /**
     * Tests behavior when the message format is incorrect or incomplete.
     * Ensures that a generic error is returned through Kafka.
    */

    @Test
    void testMalformedMessageIsHandled() {
        String requestId = "oops";
        String message = requestId + "|justonepart";

        listener.listen(message);

        verify(kafkaTemplate).send(eq("calc-responses"), contains("error"));
    }
}
