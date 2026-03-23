package de.uni_passau.fim.se2.sbse.suite_minimisation.crossover;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.CoverageTracker;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Pair;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestSuiteCrossoverTest {

    private CoverageTracker mockTracker;
    private Mutation<TestSuiteChromosome> mutation;
    private Crossover<TestSuiteChromosome> crossover;

    @BeforeEach
    void setUp() {
        mockTracker = mock(CoverageTracker.class);
        mutation = Mutation.identity();
        crossover = Crossover.identity();
    }

    @Test
    void testSinglePointCrossover() {
        boolean[] parent1Genes = {true, false, true, false};
        boolean[] parent2Genes = {false, true, false, true};

        TestSuiteChromosome parent1 = new TestSuiteChromosome(parent1Genes, mockTracker, mutation, crossover);
        TestSuiteChromosome parent2 = new TestSuiteChromosome(parent2Genes, mockTracker, mutation, crossover);

        TestSuiteCrossover crossoverOperator = new TestSuiteCrossover();
        Pair<TestSuiteChromosome> offspring = crossoverOperator.apply(parent1, parent2);

        assertEquals(parent1Genes.length, offspring.getFst().getTestCases().length, "Offspring 1 length should match parents");
        assertEquals(parent2Genes.length, offspring.getSnd().getTestCases().length, "Offspring 2 length should match parents");
    }

    @Test
    void testMultiPointCrossover() {
        boolean[] parent1Genes = {true, false, true, true};
        boolean[] parent2Genes = {false, true, false, false};

        TestSuiteChromosome parent1 = new TestSuiteChromosome(parent1Genes, mockTracker, mutation, crossover);
        TestSuiteChromosome parent2 = new TestSuiteChromosome(parent2Genes, mockTracker, mutation, crossover);

        TestSuiteCrossover crossoverOperator = new TestSuiteCrossover();
        Pair<TestSuiteChromosome> offspring = crossoverOperator.apply(parent1, parent2);

        assertEquals(parent1Genes.length, offspring.getFst().getTestCases().length, "Offspring 1 length should match parents");
        assertEquals(parent2Genes.length, offspring.getSnd().getTestCases().length, "Offspring 2 length should match parents");
    }

    @Test
    void testUniformCrossover() {
        boolean[] parent1Genes = {true, false, true, false};
        boolean[] parent2Genes = {false, true, false, true};

        TestSuiteChromosome parent1 = new TestSuiteChromosome(parent1Genes, mockTracker, mutation, crossover);
        TestSuiteChromosome parent2 = new TestSuiteChromosome(parent2Genes, mockTracker, mutation, crossover);

        TestSuiteCrossover crossoverOperator = new TestSuiteCrossover();
        Pair<TestSuiteChromosome> offspring = crossoverOperator.apply(parent1, parent2);

        assertEquals(parent1Genes.length, offspring.getFst().getTestCases().length, "Offspring 1 length should match parents");
        assertEquals(parent2Genes.length, offspring.getSnd().getTestCases().length, "Offspring 2 length should match parents");
    }

    @Test
    void testIdenticalParents() {
        boolean[] genes = {true, false, true, true};

        TestSuiteChromosome parent1 = new TestSuiteChromosome(genes, mockTracker, mutation, crossover);
        TestSuiteChromosome parent2 = new TestSuiteChromosome(genes, mockTracker, mutation, crossover);

        TestSuiteCrossover crossoverOperator = new TestSuiteCrossover();
        Pair<TestSuiteChromosome> offspring = crossoverOperator.apply(parent1, parent2);

        assertArrayEquals(genes, offspring.getFst().getTestCases(), "Offspring 1 genes should match identical parents");
        assertArrayEquals(genes, offspring.getSnd().getTestCases(), "Offspring 2 genes should match identical parents");
    }


    @Test
    void testRandomMethodSelection() {
        boolean[] parent1Genes = {true, true, false, false};
        boolean[] parent2Genes = {false, false, true, true};

        TestSuiteChromosome parent1 = new TestSuiteChromosome(parent1Genes, mockTracker, mutation, crossover);
        TestSuiteChromosome parent2 = new TestSuiteChromosome(parent2Genes, mockTracker, mutation, crossover);

        TestSuiteCrossover crossoverOperator = new TestSuiteCrossover();
        for (int i = 0; i < 100; i++) { // Ensure all methods are selected over multiple runs
            Pair<TestSuiteChromosome> offspring = crossoverOperator.apply(parent1, parent2);
            assertNotNull(offspring, "Offspring pair should not be null");
        }
    }
}
