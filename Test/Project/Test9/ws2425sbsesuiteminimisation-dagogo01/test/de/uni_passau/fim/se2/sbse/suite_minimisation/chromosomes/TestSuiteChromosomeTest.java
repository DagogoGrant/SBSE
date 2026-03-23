package de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes;

import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.CoverageTracker;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestSuiteChromosomeTest {

    @Test
void testComputeCoverageFitness_emptyMatrix() throws Exception {
    // Mock an empty coverage matrix
    CoverageTracker mockTracker = mock(CoverageTracker.class);
    when(mockTracker.getCoverageMatrix()).thenReturn(new boolean[][]{});

    boolean[] testCases = {true, false};
    TestSuiteChromosome chromosome = new TestSuiteChromosome(
        testCases, mockTracker, Mutation.identity(), Crossover.identity()
    );

    Exception exception = assertThrows(IllegalStateException.class, chromosome::computeCoverageFitness);
    assertEquals("Coverage matrix is null or empty.", exception.getMessage());
}


    @Test
    void testGetObjective_validIndex() {
        boolean[] testCases = {true, false};
        CoverageTracker mockTracker = mock(CoverageTracker.class);
        TestSuiteChromosome chromosome = new TestSuiteChromosome(
            testCases, mockTracker, Mutation.identity(), Crossover.identity()
        );

        chromosome.setObjective(0, 0.8); // Set objective
        assertEquals(0.8, chromosome.getObjective(0), "Objective at index 0 should be 0.8");
    }

    @Test
    void testGetObjective_invalidIndex() {
        boolean[] testCases = {true, false};
        CoverageTracker mockTracker = mock(CoverageTracker.class);
        TestSuiteChromosome chromosome = new TestSuiteChromosome(
            testCases, mockTracker, Mutation.identity(), Crossover.identity()
        );

        assertThrows(IndexOutOfBoundsException.class, () -> chromosome.getObjective(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> chromosome.getObjective(3));
    }

    @Test
    void testGetCrowdingDistance_defaultValue() {
        boolean[] testCases = {true, true};
        CoverageTracker mockTracker = mock(CoverageTracker.class);
        TestSuiteChromosome chromosome = new TestSuiteChromosome(
            testCases, mockTracker, Mutation.identity(), Crossover.identity()
        );

        assertEquals(0.0, chromosome.getCrowdingDistance(), "Default crowding distance should be 0.0");
    }

    @Test
    void testSetCrowdingDistance() {
        boolean[] testCases = {true, false};
        CoverageTracker mockTracker = mock(CoverageTracker.class);
        TestSuiteChromosome chromosome = new TestSuiteChromosome(
            testCases, mockTracker, Mutation.identity(), Crossover.identity()
        );

        chromosome.setCrowdingDistance(1.5);
        assertEquals(1.5, chromosome.getCrowdingDistance(), "Crowding distance should be set to 1.5");
    }

    @Test
    void testEqualsAndHashCode_sameObjects() {
        boolean[] testCases = {true, false};
        CoverageTracker mockTracker = mock(CoverageTracker.class);
        TestSuiteChromosome chromosome1 = new TestSuiteChromosome(
            testCases, mockTracker, Mutation.identity(), Crossover.identity()
        );
        TestSuiteChromosome chromosome2 = new TestSuiteChromosome(
            testCases, mockTracker, Mutation.identity(), Crossover.identity()
        );

        assertEquals(chromosome1, chromosome2, "Chromosomes with the same data should be equal");
        assertEquals(chromosome1.hashCode(), chromosome2.hashCode(), "Hash codes should match for equal chromosomes");
    }

    @Test
    void testEquals_differentTestCases() {
        boolean[] testCases1 = {true, false};
        boolean[] testCases2 = {false, true};
        CoverageTracker mockTracker = mock(CoverageTracker.class);
        TestSuiteChromosome chromosome1 = new TestSuiteChromosome(
            testCases1, mockTracker, Mutation.identity(), Crossover.identity()
        );
        TestSuiteChromosome chromosome2 = new TestSuiteChromosome(
            testCases2, mockTracker, Mutation.identity(), Crossover.identity()
        );

        assertNotEquals(chromosome1, chromosome2, "Chromosomes with different test cases should not be equal");
    }

    @Test
    void testCopy() {
        boolean[] testCases = {true, false, true};
        CoverageTracker mockTracker = mock(CoverageTracker.class);
        TestSuiteChromosome original = new TestSuiteChromosome(
            testCases, mockTracker, Mutation.identity(), Crossover.identity()
        );

        TestSuiteChromosome copy = original.copy();

        assertNotSame(original, copy, "Copy should be a new object");
        assertArrayEquals(original.getTestCases(), copy.getTestCases(), "Test cases in the copy should match the original");
        assertEquals(original.getCoverageTracker(), copy.getCoverageTracker(), "Coverage tracker should be the same");
    }

    @Test
    void testComputeSizeFitness_allSelected() {
        boolean[] testCases = {true, true, true};
        CoverageTracker mockTracker = mock(CoverageTracker.class);
        TestSuiteChromosome chromosome = new TestSuiteChromosome(
            testCases, mockTracker, Mutation.identity(), Crossover.identity()
        );

        assertEquals(1.0, chromosome.computeSizeFitness(), 0.001, "Size fitness should be 1.0 when all test cases are selected");
    }

    @Test
    void testComputeSizeFitness_noneSelected() {
        boolean[] testCases = {false, false, false};
        CoverageTracker mockTracker = mock(CoverageTracker.class);
        TestSuiteChromosome chromosome = new TestSuiteChromosome(
            testCases, mockTracker, Mutation.identity(), Crossover.identity()
        );

        assertEquals(0.0, chromosome.computeSizeFitness(), 0.001, "Size fitness should be 0.0 when no test cases are selected");
    }
}
