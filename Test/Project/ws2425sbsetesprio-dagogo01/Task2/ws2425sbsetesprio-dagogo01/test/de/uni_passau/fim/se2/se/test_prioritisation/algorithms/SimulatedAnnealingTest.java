package de.uni_passau.fim.se2.se.test_prioritisation.algorithms;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.Encoding;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.EncodingGenerator;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions.StoppingCondition;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class SimulatedAnnealingTest {

    private SimulatedAnnealing<TestEncoding> simulatedAnnealing;
    private StoppingCondition stoppingCondition;
    private EncodingGenerator<TestEncoding> encodingGenerator;
    private FitnessFunction<TestEncoding> fitnessFunction;
    private Random random;

    @BeforeEach
    void setUp() {
        stoppingCondition = new TestStoppingCondition();
        encodingGenerator = new TestEncodingGenerator();
        fitnessFunction = new TestFitnessFunction();
        random = new Random(42);

        simulatedAnnealing = new SimulatedAnnealing<>(
                stoppingCondition,
                encodingGenerator,
                fitnessFunction,
                5, // Degrees of freedom
                random
        );
    }

    @Test
    void testFindSolution() {
        TestEncoding solution = simulatedAnnealing.findSolution();
        assertNotNull(solution, "The solution should not be null");
        assertEquals(4, solution.getPositions().length, "The solution should have the expected positions length");
    }

    @Test
    void testAcceptanceProbabilityWhenNewEnergyIsLower() {
        double currentEnergy = 10.0;
        double newEnergy = 5.0;
        double temperature = 100.0;

        double probability = simulatedAnnealing.acceptanceProbability(currentEnergy, newEnergy, temperature);
        assertEquals(1.0, probability, "Acceptance probability should be 1.0 when new energy is lower");
    }

    @Test
    void testAcceptanceProbabilityWhenNewEnergyIsHigher() {
        double currentEnergy = 5.0;
        double newEnergy = 10.0;
        double temperature = 100.0;

        double probability = simulatedAnnealing.acceptanceProbability(currentEnergy, newEnergy, temperature);
        assertTrue(probability < 1.0 && probability > 0.0, "Acceptance probability should be between 0 and 1 when new energy is higher");
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

        @Override
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
            return evaluations >= 1000; // Example stopping condition after 1000 evaluations
        }

        @Override
        public double getProgress() {
            return evaluations / 1000.0; // Example progress as a percentage
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
            return Math.random(); // Example fitness function using a random value
        }

        @Override
        public double maximise(TestEncoding encoding) {
            return applyAsDouble(encoding); // Example maximize function
        }

        @Override
        public double minimise(TestEncoding encoding) {
            return -applyAsDouble(encoding); // Example minimize function
        }
    }
}
