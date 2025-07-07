package com.example.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

/**
 * Service class that performs basic arithmetic operations.
 * Supported operations: sum, sub, mul, div
*/
@Service
public class CalculatorService {

    /**
     * Executes the specified arithmetic operation on two BigDecimal numbers.
     *
     * @param operation the type of operation ("sum", "sub", "mul", "div")
     * @param a the first operand
     * @param b the second operand
     * @return the result of the calculation as BigDecimal
     * @throws IllegalArgumentException if the operation is unsupported or division by zero occurs
    */
    public BigDecimal calculate(String operation, BigDecimal a, BigDecimal b) {
        return switch (operation) {
            case "sum" -> a.add(b); // Addition
            case "sub" -> a.subtract(b); // Subtraction
            case "mul" -> a.multiply(b); // Multiplication
            case "div" -> {
                // Division with safety check and rounding
                if (b.compareTo(BigDecimal.ZERO) == 0) throw new IllegalArgumentException("Cannot divide by zero");
                // Returns result rounded to 10 decimal places using HALF_UP rounding mode
                yield a.divide(b,10,RoundingMode.HALF_UP);
            }
            default -> throw new IllegalArgumentException("Unsupported operation: " + operation);
        };
    }
}