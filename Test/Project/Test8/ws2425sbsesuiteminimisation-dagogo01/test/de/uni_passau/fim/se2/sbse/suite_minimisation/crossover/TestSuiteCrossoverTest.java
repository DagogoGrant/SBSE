package de.uni_passau.fim.se2.sbse.suite_minimisation.crossover;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.CoverageTracker;

import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Pair;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestSuiteCrossoverTest {

    @Test
    void testSinglePointCrossover() {
        boolean[] parent1Genes = {true, false, true, false};
        boolean[] parent2Genes = {false, true, false, true};

        // Mock CoverageTracker (if needed for TestSuiteChromosome constructor)
        CoverageTracker mockTracker = mock(CoverageTracker.class);

        // Use identity mutation and crossover operators for this test
        Mutation<TestSuiteChromosome> mutation = Mutation.identity();
        Crossover<TestSuiteChromosome> crossover = Crossover.identity();

        TestSuiteChromosome parent1 = new TestSuiteChromosome(parent1Genes, mockTracker, mutation, crossover);
        TestSuiteChromosome parent2 = new TestSuiteChromosome(parent2Genes, mockTracker, mutation, crossover);

        TestSuiteCrossover crossoverOperator = new TestSuiteCrossover();
        Pair<TestSuiteChromosome> offspring = crossoverOperator.apply(parent1, parent2);

        boolean[] offspring1Genes = offspring.getFst().getTestCases();
        boolean[] offspring2Genes = offspring.getSnd().getTestCases();

        // Verify offspring have mixed traits from parents
        assertNotEquals(parent1Genes, offspring1Genes, "Offspring 1 should differ from Parent 1");
        assertNotEquals(parent2Genes, offspring2Genes, "Offspring 2 should differ from Parent 2");
        assertEquals(parent1Genes.length, offspring1Genes.length, "Offspring should have the same length as parents");
    }
}
