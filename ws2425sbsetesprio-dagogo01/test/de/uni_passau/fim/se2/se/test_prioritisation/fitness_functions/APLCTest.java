package de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.mutations.Mutation;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class APLCTest {

    @Test
    void testApplyAsDouble() {
        boolean[][] coverageMatrix = {
                {true, false, true},
                {false, true, false},
                {true, true, false}
        };
        APLC aplc = new APLC(coverageMatrix);

        Mutation<TestOrder> mutation = encoding -> encoding; // Simple identity mutation for testing
        TestOrder testOrder = new TestOrder(mutation, new int[]{0, 1, 2});

        double result = aplc.applyAsDouble(testOrder);
        assertTrue(result >= 0.0 && result <= 1.0, "APLC value should be between 0.0 and 1.0");
    }

    @Test
    void testMaximise() {
        boolean[][] coverageMatrix = {
                {true, false, true},
                {false, true, false},
                {true, true, false}
        };
        APLC aplc = new APLC(coverageMatrix);

        Mutation<TestOrder> mutation = encoding -> encoding; // Simple identity mutation for testing
        TestOrder testOrder = new TestOrder(mutation, new int[]{0, 1, 2});

        double result = aplc.maximise(testOrder);
        assertTrue(result >= 0.0 && result <= 1.0, "Maximise value should be between 0.0 and 1.0");
    }

    @Test
    void testMinimise() {
        boolean[][] coverageMatrix = {
                {true, false, true},
                {false, true, false},
                {true, true, false}
        };
        APLC aplc = new APLC(coverageMatrix);

        Mutation<TestOrder> mutation = encoding -> encoding; // Simple identity mutation for testing
        TestOrder testOrder = new TestOrder(mutation, new int[]{0, 1, 2});

        double result = aplc.minimise(testOrder);
        assertTrue(result >= 0.0 && result <= 1.0, "Minimise value should be between 0.0 and 1.0");
    }

    @Test
    void testNullCoverageMatrix() {
        assertThrows(IllegalArgumentException.class, () -> new APLC(null), "Should throw IllegalArgumentException for null coverage matrix");
    }

    @Test
    void testNullTestOrder() {
        boolean[][] coverageMatrix = {
                {true, false, true},
                {false, true, false},
                {true, true, false}
        };
        APLC aplc = new APLC(coverageMatrix);

        assertThrows(NullPointerException.class, () -> aplc.applyAsDouble(null), "Should throw NullPointerException for null test order");
    }
}
