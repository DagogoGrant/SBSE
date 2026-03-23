package de.uni_passau.fim.se2.se.test_prioritisation.algorithms;

import de.uni_passau.fim.se2.se.test_prioritisation.crossover.Crossover;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.Encoding;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.EncodingGenerator;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.se.test_prioritisation.parent_selection.ParentSelection;
import de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions.StoppingCondition;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SimpleGeneticAlgorithmTest {

    private SimpleGeneticAlgorithm<TestEncoding> geneticAlgorithm;
    private StoppingCondition stoppingCondition;
    private EncodingGenerator<TestEncoding> encodingGenerator;
    private FitnessFunction<TestEncoding> fitnessFunction;
    private Crossover<TestEncoding> crossover;
    private ParentSelection<TestEncoding> parentSelection;
    private Random random;

    @BeforeEach
    void setUp() {
        stoppingCondition = new TestStoppingCondition();
        encodingGenerator = new TestEncodingGenerator();
        fitnessFunction = new TestFitnessFunction();
        crossover = new TestCrossover();
        parentSelection = new TestParentSelection();
        random = new Random(42);

        geneticAlgorithm = new SimpleGeneticAlgorithm<>(
                stoppingCondition,
                encodingGenerator,
                fitnessFunction,
                crossover,
                parentSelection,
                random
        );
    }

    @Test
    void testFindSolution() {
        TestEncoding solution = geneticAlgorithm.findSolution();
        assertNotNull(solution, "The solution should not be null.");
    }

    @Test
    void testInitializePopulation() {
        geneticAlgorithm.initializePopulation();
        List<TestEncoding> population = geneticAlgorithm.getPopulation();
        assertEquals(SimpleGeneticAlgorithm.POPULATION_SIZE, population.size(), "The population size should match the expected value.");
    }

    @Test
    void testEvaluatePopulation() {
        geneticAlgorithm.initializePopulation();
        geneticAlgorithm.evaluatePopulation();
        assertNotNull(geneticAlgorithm.getPopulation(), "The population should not be null after evaluation.");
    }

    @Test
    void testElitism() {
        geneticAlgorithm.initializePopulation();
        geneticAlgorithm.evaluatePopulation();
        List<TestEncoding> elitePopulation = geneticAlgorithm.elitism();
        assertFalse(elitePopulation.isEmpty(), "The elite population should not be empty if a best solution exists.");
    }

    @Test
    void testSetAndGetPopulation() {
        List<TestEncoding> population = new ArrayList<>();
        for (int i = 0; i < SimpleGeneticAlgorithm.POPULATION_SIZE; i++) {
            population.add(new TestEncoding());
        }
        geneticAlgorithm.setPopulation(population);
        assertEquals(SimpleGeneticAlgorithm.POPULATION_SIZE, geneticAlgorithm.getPopulation().size(), "The set population size should match the expected value.");
    }

    @Test
    void testConstructorWithNullArguments() {
        assertThrows(IllegalArgumentException.class, () -> new SimpleGeneticAlgorithm<>(null, encodingGenerator, fitnessFunction, crossover, parentSelection, random), "Should throw IllegalArgumentException if stoppingCondition is null.");
        assertThrows(IllegalArgumentException.class, () -> new SimpleGeneticAlgorithm<>(stoppingCondition, null, fitnessFunction, crossover, parentSelection, random), "Should throw IllegalArgumentException if encodingGenerator is null.");
        assertThrows(IllegalArgumentException.class, () -> new SimpleGeneticAlgorithm<>(stoppingCondition, encodingGenerator, null, crossover, parentSelection, random), "Should throw IllegalArgumentException if fitnessFunction is null.");
        assertThrows(IllegalArgumentException.class, () -> new SimpleGeneticAlgorithm<>(stoppingCondition, encodingGenerator, fitnessFunction, null, parentSelection, random), "Should throw IllegalArgumentException if crossover is null.");
        assertThrows(IllegalArgumentException.class, () -> new SimpleGeneticAlgorithm<>(stoppingCondition, encodingGenerator, fitnessFunction, crossover, null, random), "Should throw IllegalArgumentException if parentSelection is null.");
        assertThrows(IllegalArgumentException.class, () -> new SimpleGeneticAlgorithm<>(stoppingCondition, encodingGenerator, fitnessFunction, crossover, parentSelection, null), "Should throw IllegalArgumentException if random is null.");
    }

    @Test
    void testCrossoverProbability() {
        geneticAlgorithm.setCrossoverProbability(0.5);
        // Assuming crossover probability was changed successfully
        assertDoesNotThrow(() -> geneticAlgorithm.setCrossoverProbability(0.8), "Setting crossover probability should not throw an exception.");
    }

    // Helper classes for testing purposes

    private static class TestEncoding extends Encoding<TestEncoding> {
        private final int[] positions;

        public TestEncoding() {
            super(mutation -> mutation);
            this.positions = new int[]{0, 1, 2, 3}; // Example positions array
        }

        @Override
        public TestEncoding deepCopy() {
            return new TestEncoding();
        }

       
        public int[] getPositions() {
            return positions;
        }

        @Override
        public TestEncoding self() {
            return this;
        }
    }

    private static class TestStoppingCondition implements StoppingCondition {
        private int evaluations = 0;

        @Override
        public void notifySearchStarted() {
            evaluations = 0;
        }

        @Override
        public void notifyFitnessEvaluation() {
            evaluations++;
        }

        @Override
        public boolean searchMustStop() {
            return evaluations >= 1000;
        }

        @Override
        public double getProgress() {
            return (double) evaluations / 1000.0;
        }
    }

    private static class TestEncodingGenerator implements EncodingGenerator<TestEncoding> {
        @Override
        public TestEncoding get() {
            return new TestEncoding();
        }
    }

    private static class TestFitnessFunction implements FitnessFunction<TestEncoding> {
        @Override
        public double applyAsDouble(TestEncoding encoding) {
            return Math.random();
        }

        @Override
        public double maximise(TestEncoding encoding) {
            return applyAsDouble(encoding);
        }

        @Override
        public double minimise(TestEncoding encoding) {
            return -applyAsDouble(encoding);
        }
    }

    private static class TestCrossover implements Crossover<TestEncoding> {
        @Override
        public TestEncoding apply(TestEncoding parent1, TestEncoding parent2) {
            return new TestEncoding();
        }
    }

    private static class TestParentSelection implements ParentSelection<TestEncoding> {
        @Override
        public TestEncoding selectParent(List<TestEncoding> population) {
            return population.get(0);
        }
    }
}
