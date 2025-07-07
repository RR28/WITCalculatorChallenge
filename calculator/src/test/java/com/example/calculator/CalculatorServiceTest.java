package com.example.calculator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

/**
 * Unit tests for the CalculatorService class.
 * 
 * Each test verifies the correctness and error handling
 * of arithmetic operations like addition, subtraction,
 * multiplication, and division.
*/
class CalculatorServiceTest {

    // Instance of the service to test
    private final CalculatorService service = new CalculatorService();

    /**
     * Tests basic addition: 3 + 2 = 5
    */
    @Test
    void testAddition() {
        assertEquals(new BigDecimal(5), service.calculate("sum", new BigDecimal(3), new BigDecimal(2)));
    }

    /**
     * Tests basic subtraction: 4 - 3 = 1
    */

    @Test
    void testSubtraction() {
        assertEquals(new BigDecimal(1), service.calculate("sub", new BigDecimal(4), new BigDecimal(3)));
    }

    /**
     * Tests basic multiplication: 3 * 4 = 12
    */

    @Test
    void testMultiplication() {
        assertEquals(new BigDecimal(12), service.calculate("mul", new BigDecimal(3), new BigDecimal(4)));
    }

    /**
     * Tests basic division: 6 / 3 = 2
     * Uses compareTo to avoid scale-related equality issues.
    */
    @Test
    void testDivision() {
        assertTrue(service.calculate("div", new BigDecimal(6), new BigDecimal(3)).compareTo(new BigDecimal(2)) == 0);
    }

    /**
     * Tests that dividing by zero throws the expected exception.
    */
    @Test
    void testDivisionByZero() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> service.calculate("div", new BigDecimal(1), new BigDecimal(0)));
        assertEquals("Cannot divide by zero", exception.getMessage());
    }

    /**
     * Tests that using an unsupported operation throws an exception.
    */
    @Test
    void testUnsupportedOperation() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> service.calculate("mod", new BigDecimal(1), new BigDecimal(1)));
        assertTrue(exception.getMessage().contains("Unsupported operation"));
    }
}
