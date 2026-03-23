package de.uni_passau.fim.se2.sbse.suite_minimisation.crossover;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Pair;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class TestSuiteCrossoverTest {

    @Test
    void testCrossoverWithValidParents() {
        Crossover<TestSuiteChromosome> crossover = new TestSuiteCrossover();

        TestSuiteChromosome parent1 = new TestSuiteChromosome(mock(Mutation.class), crossover, 10);
        parent1.setTestCases(Arrays.asList(1, 2, 3));

        TestSuiteChromosome parent2 = new TestSuiteChromosome(mock(Mutation.class), crossover, 10);
        parent2.setTestCases(Arrays.asList(4, 5, 6));

        Pair<TestSuiteChromosome> offspring = crossover.apply(parent1, parent2);

        assertNotNull(offspring);
        assertNotNull(offspring.getFst());
        assertNotNull(offspring.getSnd());
    }

    @Test
    void testCrossoverWithEmptyParent() {
        Crossover<TestSuiteChromosome> crossover = new TestSuiteCrossover();

        TestSuiteChromosome parent1 = new TestSuiteChromosome(mock(Mutation.class), crossover, 10);
        parent1.setTestCases(Collections.emptyList());

        TestSuiteChromosome parent2 = new TestSuiteChromosome(mock(Mutation.class), crossover, 10);
        parent2.setTestCases(Arrays.asList(1, 2, 3));

        Pair<TestSuiteChromosome> offspring = crossover.apply(parent1, parent2);

        assertEquals(Collections.emptyList(), offspring.getFst().getTestCases());
        assertEquals(parent2.getTestCases(), offspring.getSnd().getTestCases());
    }

    @Test
    void testCrossoverWithIdenticalParents() {
        Crossover<TestSuiteChromosome> crossover = new TestSuiteCrossover();

        TestSuiteChromosome parent1 = new TestSuiteChromosome(mock(Mutation.class), crossover, 10);
        parent1.setTestCases(Arrays.asList(1, 2, 3));

        Pair<TestSuiteChromosome> offspring = crossover.apply(parent1, parent1);

        assertEquals(parent1.getTestCases(), offspring.getFst().getTestCases());
        assertEquals(parent1.getTestCases(), offspring.getSnd().getTestCases());
    }
}
