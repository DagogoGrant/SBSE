package de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes;

import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;



import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomTestSuiteGenerator implements ChromosomeGenerator<TestSuiteChromosome> {

    private final int testSuiteSize;

    /**
     * Constructs a generator for random test suite chromosomes.
     *
     * @param testSuiteSize the size of the test suite
     */
    public RandomTestSuiteGenerator(int testSuiteSize) {
        this.testSuiteSize = testSuiteSize;
    }

    @Override
    public TestSuiteChromosome get() {
        boolean[] testSuite = new boolean[testSuiteSize];
        Random random = new Random(); // Replaced Randomness.getRandom() with java.util.Random

        // Ensure at least one test case is included
        boolean atLeastOneIncluded = false;
        for (int i = 0; i < testSuiteSize; i++) {
            testSuite[i] = random.nextBoolean();
            if (testSuite[i]) atLeastOneIncluded = true;
        }

        // If no test case is included, randomly pick one
        if (!atLeastOneIncluded) {
            testSuite[random.nextInt(testSuiteSize)] = true;
        }

        // Return the new chromosome with identity mutation/crossover
        return new TestSuiteChromosome(testSuite, Mutation.identity(), Crossover.identity());
    }
    public List<DummyChromosomeFixture> initializePopulation(int populationSize) {
        List<DummyChromosomeFixture> population = new ArrayList<>();
        Random random = new Random();
    
        for (int i = 0; i < populationSize; i++) {
            // Initialize chromosomes with random suiteSize and fitness
            int suiteSize = random.nextInt(100);  // Example range: 0-99
            population.add(new DummyChromosomeFixture(suiteSize, 0.0));
        }
        return population;
    }
    

}
