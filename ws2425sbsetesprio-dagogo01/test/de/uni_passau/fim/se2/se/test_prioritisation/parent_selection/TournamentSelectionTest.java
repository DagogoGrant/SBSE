package de.uni_passau.fim.se2.se.test_prioritisation.parent_selection;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.APLC;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TournamentSelectionTest {

    private APLC fitnessFunction;
    private Random random;

    @BeforeEach
    void setup() {
        fitnessFunction = mock(APLC.class);
        random = new Random(42); // Fixed seed for reproducibility
    }

    @Test
    void testSelectParentReturnsValidParent() {
        // Create a population of TestOrders with mock fitness values
        List<TestOrder> population = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            TestOrder testOrder = mock(TestOrder.class);
            population.add(testOrder);
            when(fitnessFunction.applyAsDouble(testOrder)).thenReturn((double) i);
        }

        // Create TournamentSelection with tournament size 3
        TournamentSelection tournamentSelection = new TournamentSelection(3, fitnessFunction, random);

        // Select a parent
        TestOrder selectedParent = tournamentSelection.selectParent(population);

        // Verify that the selected parent is in the population
        assertTrue(population.contains(selectedParent));

        // Verify that the selected parent is one of the most fit in the tournament
        double selectedFitness = fitnessFunction.applyAsDouble(selectedParent);
        assertTrue(selectedFitness >= 7.0); // Based on mock setup and fixed seed
    }

    @Test
    void testTournamentSelectionFailsForSmallPopulation() {
        List<TestOrder> smallPopulation = new ArrayList<>();
        smallPopulation.add(mock(TestOrder.class));

        TournamentSelection tournamentSelection = new TournamentSelection(3, fitnessFunction, random);

        // Should throw an exception because the population is smaller than the tournament size
        assertThrows(IllegalArgumentException.class, () -> tournamentSelection.selectParent(smallPopulation));
    }

    @Test
    void testTournamentSelectionWithDefaultSize() {
        // Create a population of TestOrders
        List<TestOrder> population = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            TestOrder testOrder = mock(TestOrder.class);
            population.add(testOrder);
            when(fitnessFunction.applyAsDouble(testOrder)).thenReturn((double) i);
        }

        // Create TournamentSelection with default size
        TournamentSelection tournamentSelection = new TournamentSelection(fitnessFunction, random);

        // Select a parent
        TestOrder selectedParent = tournamentSelection.selectParent(population);

        // Verify that the selected parent is in the population
        assertTrue(population.contains(selectedParent));
    }

    @Test
    void testSelectParentWithTournamentSizeEqualsPopulationSize() {
        // Create a population of TestOrders
        List<TestOrder> population = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            TestOrder testOrder = mock(TestOrder.class);
            population.add(testOrder);
            when(fitnessFunction.applyAsDouble(testOrder)).thenReturn((double) i);
        }

        // Create TournamentSelection with tournament size equal to population size
        TournamentSelection tournamentSelection = new TournamentSelection(5, fitnessFunction, random);

        // Select a parent
        TestOrder selectedParent = tournamentSelection.selectParent(population);

        // Verify that the selected parent is in the population
        assertTrue(population.contains(selectedParent));

        // Verify that the selected parent is the fittest in the population
        double selectedFitness = fitnessFunction.applyAsDouble(selectedParent);
        assertEquals(4.0, selectedFitness);
    }

    @Test
    void testTournamentSelectionThrowsExceptionForNullPopulation() {
        TournamentSelection tournamentSelection = new TournamentSelection(3, fitnessFunction, random);

        // Should throw an exception because the population is null
        assertThrows(IllegalArgumentException.class, () -> tournamentSelection.selectParent(null));
    }

    @Test
    void testConstructorThrowsExceptionForNullFitnessFunction() {
        // Should throw a NullPointerException for null fitness function
        assertThrows(NullPointerException.class, () -> new TournamentSelection(3, null, random));
    }

    @Test
    void testConstructorThrowsExceptionForNullRandom() {
        // Should throw a NullPointerException for null random generator
        assertThrows(NullPointerException.class, () -> new TournamentSelection(3, fitnessFunction, null));
    }

    @Test
    void testConstructorThrowsExceptionForInvalidTournamentSize() {
        // Should throw an IllegalArgumentException for tournament size less than 1
        assertThrows(IllegalArgumentException.class, () -> new TournamentSelection(0, fitnessFunction, random));
    }

    @Test
    void testSelectParentWithSingleIndividualPopulation() {
        // Create a population with only one individual
        List<TestOrder> population = new ArrayList<>();
        TestOrder testOrder = mock(TestOrder.class);
        population.add(testOrder);
        when(fitnessFunction.applyAsDouble(testOrder)).thenReturn(1.0);

        // Create TournamentSelection with tournament size 1
        TournamentSelection tournamentSelection = new TournamentSelection(1, fitnessFunction, random);

        // Select a parent
        TestOrder selectedParent = tournamentSelection.selectParent(population);

        // Verify that the selected parent is the only individual in the population
        assertEquals(testOrder, selectedParent);
    }
}
