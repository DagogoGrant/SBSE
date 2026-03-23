package de.uni_passau.fim.se2.sbse.suite_minimisation.crossover;


import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Pair;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestSuiteCrossoverTest {

    @Test
    void testCrossoverProducesValidOffspring() {
        int totalTestCases = 10;
        
        // Provide valid mutation and crossover operators
        Mutation<TestSuiteChromosome> identityMutation = Mutation.identity();
        TestSuiteCrossover crossover = new TestSuiteCrossover();

        // Create parent chromosomes with identity mutation and valid crossover
        TestSuiteChromosome parent1 = new TestSuiteChromosome(identityMutation, crossover, totalTestCases);
        TestSuiteChromosome parent2 = new TestSuiteChromosome(identityMutation, crossover, totalTestCases);

        // Apply crossover
        Pair<TestSuiteChromosome> offspring = crossover.apply(parent1, parent2);

        // Assertions
        assertNotNull(offspring, "Crossover should produce offspring");
        assertFalse(offspring.getFst().getTestCases().isEmpty(), "Offspring 1 should have test cases");
        assertFalse(offspring.getSnd().getTestCases().isEmpty(), "Offspring 2 should have test cases");
        assertNotSame(parent1, offspring.getFst(), "Offspring 1 should be a new instance");
        assertNotSame(parent2, offspring.getSnd(), "Offspring 2 should be a new instance");
    }
}

