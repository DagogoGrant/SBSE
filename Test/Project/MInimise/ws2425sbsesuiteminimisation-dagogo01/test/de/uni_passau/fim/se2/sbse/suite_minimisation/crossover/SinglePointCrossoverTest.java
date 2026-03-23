package de.uni_passau.fim.se2.sbse.suite_minimisation.crossover;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.BitFlipMutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Pair;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SinglePointCrossoverTest {

    @Test
    void testCrossoverProducesValidOffspring() {
        // Parent chromosomes
        boolean[] parent1Suite = {true, false, true, false};
        boolean[] parent2Suite = {false, true, false, true};
        SinglePointCrossover crossover = new SinglePointCrossover();
        BitFlipMutation mutation = new BitFlipMutation();

        TestSuiteChromosome parent1 = new TestSuiteChromosome(parent1Suite, mutation, crossover);
        TestSuiteChromosome parent2 = new TestSuiteChromosome(parent2Suite, mutation, crossover);

        // Apply crossover
        Pair<TestSuiteChromosome> offspring = crossover.apply(parent1, parent2);
        boolean[] offspring1Suite = offspring.getFst().getTestSuite();
        boolean[] offspring2Suite = offspring.getSnd().getTestSuite();

        // Ensure offspring are valid
        assertEquals(parent1Suite.length, offspring1Suite.length, "Offspring1 length should match parents");
        assertEquals(parent2Suite.length, offspring2Suite.length, "Offspring2 length should match parents");

        assertTrue(
            containsAtLeastOneTrue(offspring1Suite),
            "Offspring1 must have at least one test case included"
        );
        assertTrue(
            containsAtLeastOneTrue(offspring2Suite),
            "Offspring2 must have at least one test case included"
        );

        // Ensure offspring differ from parents
        assertNotEquals(parent1, offspring.getFst(), "Offspring1 should differ from Parent1");
        assertNotEquals(parent2, offspring.getSnd(), "Offspring2 should differ from Parent2");
    }

    private boolean containsAtLeastOneTrue(boolean[] array) {
        for (boolean value : array) {
            if (value) return true;
        }
        return false;
    }
}
