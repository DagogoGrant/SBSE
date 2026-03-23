package de.uni_passau.fim.se2.se.test_prioritisation.parent_selection;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.APLC;
import de.uni_passau.fim.se2.se.test_prioritisation.mutations.Mutation;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class TournamentSelectionTest {

    @Test
    void testSelectParent() {
        // Define a coverage matrix with the same number of rows as the population size
        boolean[][] coverageMatrix = {
                {true, false, true},
                {false, true, false},
                {true, true, false},
                {false, false, true},
                {true, false, false},
                {false, true, true},
                {true, true, true},
                {false, false, false},
                {true, false, true},
                {false, true, false}
        };
        APLC aplc = new APLC(coverageMatrix);
        Random random = new Random(42);

        Mutation<TestOrder> mutation = encoding -> encoding; // Simple identity mutation for testing
        List<TestOrder> population = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            // Construct a valid permutation array for `positions`
            int[] positions = new int[10];
            for (int j = 0; j < 10; j++) {
                positions[j] = j; // Ensures uniqueness and valid range from 0 to 9
            }
            population.add(new TestOrder(mutation, positions));
        }

        TournamentSelection selection = new TournamentSelection(3, aplc, random);
        TestOrder parent = selection.selectParent(population);
        assertNotNull(parent, "Selected parent should not be null.");
        assertTrue(population.contains(parent), "Selected parent must be part of the population.");
    }

    @Test
    void testNullFitnessFunction() {
        Random random = new Random();
        assertThrows(NullPointerException.class, () -> new TournamentSelection(3, null, random), "Should throw NullPointerException for null fitness function");
    }

    @Test
    void testNullRandom() {
        boolean[][] coverageMatrix = {
                {true, false, true},
                {false, true, false},
                {true, true, false}
        };
        APLC aplc = new APLC(coverageMatrix);
        assertThrows(NullPointerException.class, () -> new TournamentSelection(3, aplc, null), "Should throw NullPointerException for null random generator");
    }

    @Test
    void testNegativeTournamentSize() {
        boolean[][] coverageMatrix = {
                {true, false, true},
                {false, true, false},
                {true, true, false}
        };
        APLC aplc = new APLC(coverageMatrix);
        Random random = new Random();
        assertThrows(IllegalArgumentException.class, () -> new TournamentSelection(-1, aplc, random), "Should throw IllegalArgumentException for negative tournament size");
    }

    @Test
    void testSelectParentWithNullPopulation() {
        boolean[][] coverageMatrix = {
                {true, false, true},
                {false, true, false},
                {true, true, false}
        };
        APLC aplc = new APLC(coverageMatrix);
        Random random = new Random();

        TournamentSelection selection = new TournamentSelection(3, aplc, random);
        assertThrows(IllegalArgumentException.class, () -> selection.selectParent(null), "Should throw IllegalArgumentException for null population");
    }

    @Test
    void testSelectParentWithEmptyPopulation() {
        boolean[][] coverageMatrix = {
                {true, false, true},
                {false, true, false},
                {true, true, false}
        };
        APLC aplc = new APLC(coverageMatrix);
        Random random = new Random();

        TournamentSelection selection = new TournamentSelection(3, aplc, random);
        assertThrows(IllegalArgumentException.class, () -> selection.selectParent(new ArrayList<>()), "Should throw IllegalArgumentException for empty population");
    }

    @Test
    void testTournamentSizeGreaterThanPopulation() {
        boolean[][] coverageMatrix = {
                {true, false, true},
                {false, true, false},
                {true, true, false}
        };
        APLC aplc = new APLC(coverageMatrix);
        Random random = new Random();

        Mutation<TestOrder> mutation = encoding -> encoding;
        List<TestOrder> population = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            population.add(new TestOrder(mutation, new int[]{i, (i + 1) % 3, (i + 2) % 3}));
        }

        TournamentSelection selection = new TournamentSelection(4, aplc, random);
        assertThrows(IllegalArgumentException.class, () -> selection.selectParent(population), "Should throw IllegalArgumentException when tournament size is greater than population size");
    }
}
