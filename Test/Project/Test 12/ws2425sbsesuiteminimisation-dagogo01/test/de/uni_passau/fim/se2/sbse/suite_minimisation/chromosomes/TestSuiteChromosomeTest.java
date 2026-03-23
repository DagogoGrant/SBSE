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

@Test
void testSetAndGetObjective_multipleIndices() {
    boolean[] testCases = {true, false};
    CoverageTracker mockTracker = mock(CoverageTracker.class);
    TestSuiteChromosome chromosome = new TestSuiteChromosome(
        testCases, mockTracker, Mutation.identity(), Crossover.identity()
    );

    chromosome.setObjective(0, 0.5);
    chromosome.setObjective(1, 0.9);

    assertEquals(0.5, chromosome.getObjective(0), "Objective at index 0 should be 0.5");
    assertEquals(0.9, chromosome.getObjective(1), "Objective at index 1 should be 0.9");
}

@Test
void testSetSizeFitness_updatesObjective() {
    boolean[] testCases = {true, false, true};
    CoverageTracker mockTracker = mock(CoverageTracker.class);
    TestSuiteChromosome chromosome = new TestSuiteChromosome(
        testCases, mockTracker, Mutation.identity(), Crossover.identity()
    );

    chromosome.setSizeFitness(0.7);
    assertEquals(0.7, chromosome.getSizeFitness(), "Size fitness should be updated to 0.7");
    assertEquals(0.7, chromosome.getObjective(0), "Objective 0 should match updated size fitness");
}

@Test
void testGetTotalSize() {
    boolean[] testCases = {true, false, true};
    CoverageTracker mockTracker = mock(CoverageTracker.class);
    TestSuiteChromosome chromosome = new TestSuiteChromosome(
        testCases, mockTracker, Mutation.identity(), Crossover.identity()
    );

    assertEquals(3, chromosome.getTotalSize(), "Total size should match the length of test cases array");
}

@Test
void testGetSelectedSize_partialSelection() {
    boolean[] testCases = {true, false, true};
    CoverageTracker mockTracker = mock(CoverageTracker.class);
    TestSuiteChromosome chromosome = new TestSuiteChromosome(
        testCases, mockTracker, Mutation.identity(), Crossover.identity()
    );

    assertEquals(2, chromosome.getSelectedSize(), "Selected size should count the number of true values in test cases");
}

@Test
void testEqualsAndHashCode_differentCoverageTracker() {
    boolean[] testCases = {true, false};
    CoverageTracker mockTracker1 = mock(CoverageTracker.class);
    CoverageTracker mockTracker2 = mock(CoverageTracker.class);

    TestSuiteChromosome chromosome1 = new TestSuiteChromosome(
        testCases, mockTracker1, Mutation.identity(), Crossover.identity()
    );
    TestSuiteChromosome chromosome2 = new TestSuiteChromosome(
        testCases, mockTracker2, Mutation.identity(), Crossover.identity()
    );

    assertNotEquals(chromosome1, chromosome2, "Chromosomes with different coverage trackers should not be equal");
    assertNotEquals(chromosome1.hashCode(), chromosome2.hashCode(), 
                    "Hash codes should differ for chromosomes with different coverage trackers");
}

// @Test
// void testComputeCoverageFitness_emptySelectedTestCases() throws Exception {
//     boolean[][] coverageMatrix = {
//         {true, false, true},
//         {false, true, false},
//         {true, true, false}
//     };
//     CoverageTracker mockTracker = mock(CoverageTracker.class);
//     when(mockTracker.getCoverageMatrix()).thenReturn(coverageMatrix);

//     boolean[] testCases = {false, false, false};
//     TestSuiteChromosome chromosome = new TestSuiteChromosome(
//         testCases, mockTracker, Mutation.identity(), Crossover.identity()
//     );

//     assertEquals(0.0, chromosome.computeCoverageFitness(), 0.001, 
//                  "Coverage fitness should be 0.0 when no test cases are selected");
// }

@Test
void testHashCode_differentTestCases() {
    boolean[] testCases1 = {true, false, true};
    boolean[] testCases2 = {false, true, true};
    CoverageTracker mockTracker = mock(CoverageTracker.class);

    TestSuiteChromosome chromosome1 = new TestSuiteChromosome(
        testCases1, mockTracker, Mutation.identity(), Crossover.identity()
    );
    TestSuiteChromosome chromosome2 = new TestSuiteChromosome(
        testCases2, mockTracker, Mutation.identity(), Crossover.identity()
    );

    assertNotEquals(chromosome1.hashCode(), chromosome2.hashCode(), 
                    "Hash codes should differ for chromosomes with different test cases");
}
@Test
void testDefaultInitialization() {
    boolean[] testCases = {false, false};
    CoverageTracker mockTracker = mock(CoverageTracker.class);
    TestSuiteChromosome chromosome = new TestSuiteChromosome(
        testCases, mockTracker, Mutation.identity(), Crossover.identity()
    );

    assertEquals(0.0, chromosome.getCrowdingDistance(), "Crowding distance should be 0.0 by default");
    assertEquals(0.0, chromosome.getObjective(0), "Default objective value should be 0.0");
    assertEquals(0.0, chromosome.getObjective(1), "Default objective value should be 0.0");
}
// @Test
// void testEmptyTestCases() {
//     boolean[] testCases = {};
//     CoverageTracker mockTracker = mock(CoverageTracker.class);
//     TestSuiteChromosome chromosome = new TestSuiteChromosome(
//         testCases, mockTracker, Mutation.identity(), Crossover.identity()
//     );

//     assertEquals(0.0, chromosome.computeSizeFitness(), 0.001, "Size fitness should be 0.0 for empty test cases");
//     assertEquals(0, chromosome.getTotalSize(), "Total size should be 0 for empty test cases");
//     assertEquals(0, chromosome.getSelectedSize(), "Selected size should be 0 for empty test cases");
// }

@Test
void testNullTestCases() {
    CoverageTracker mockTracker = mock(CoverageTracker.class);
    Exception exception = assertThrows(NullPointerException.class, () -> 
        new TestSuiteChromosome(null, mockTracker, Mutation.identity(), Crossover.identity())
    );

    assertEquals("Test cases cannot be null", exception.getMessage());
}
@Test
void testNullCoverageTracker() {
    boolean[] testCases = {true, false};
    Exception exception = assertThrows(NullPointerException.class, () -> 
        new TestSuiteChromosome(testCases, null, Mutation.identity(), Crossover.identity())
    );

    assertEquals("CoverageTracker cannot be null", exception.getMessage());
}
@Test
void testInvalidObjectiveIndex() {
    boolean[] testCases = {true, false};
    CoverageTracker mockTracker = mock(CoverageTracker.class);
    TestSuiteChromosome chromosome = new TestSuiteChromosome(
        testCases, mockTracker, Mutation.identity(), Crossover.identity()
    );

    assertThrows(IndexOutOfBoundsException.class, () -> chromosome.getObjective(-1));
    assertThrows(IndexOutOfBoundsException.class, () -> chromosome.setObjective(2, 0.5));
}
@Test
void testComputeCoverageFitness_partialCoverage() throws Exception {
    boolean[][] coverageMatrix = {
        {true, false, true},
        {false, true, false},
        {true, false, false}
    };
    CoverageTracker mockTracker = mock(CoverageTracker.class);
    when(mockTracker.getCoverageMatrix()).thenReturn(coverageMatrix);

    boolean[] testCases = {true, false, false};
    TestSuiteChromosome chromosome = new TestSuiteChromosome(
        testCases, mockTracker, Mutation.identity(), Crossover.identity()
    );

    assertEquals(0.666, chromosome.computeCoverageFitness(), 0.001, 
                 "Coverage fitness should be correct for partial coverage");
}
@Test
void testCopyConsistency() {
    boolean[] testCases = {true, false};
    CoverageTracker mockTracker = mock(CoverageTracker.class);
    TestSuiteChromosome original = new TestSuiteChromosome(
        testCases, mockTracker, Mutation.identity(), Crossover.identity()
    );
    TestSuiteChromosome copy = original.copy();

    assertNotSame(original, copy, "Copied chromosome should not be the same instance");
    assertArrayEquals(original.getTestCases(), copy.getTestCases(), "Test cases should match between original and copy");
    assertEquals(original.getCrowdingDistance(), copy.getCrowdingDistance(), "Crowding distance should match");
}
@Test
void testEquals_null() {
    boolean[] testCases = {true, false};
    CoverageTracker mockTracker = mock(CoverageTracker.class);
    TestSuiteChromosome chromosome = new TestSuiteChromosome(
        testCases, mockTracker, Mutation.identity(), Crossover.identity()
    );

    assertNotEquals(null, chromosome, "Chromosome should not be equal to null");
}
@Test
void testComputeSizeFitness_allFalse() {
    boolean[] testCases = {false, false, false};
    CoverageTracker mockTracker = mock(CoverageTracker.class);
    TestSuiteChromosome chromosome = new TestSuiteChromosome(
        testCases, mockTracker, Mutation.identity(), Crossover.identity()
    );

    assertEquals(0.0, chromosome.computeSizeFitness(), 0.001, "Size fitness should be 0.0 for all false test cases");
}


// @Test
// void testComputeSizeFitness_emptyTestCases() {
//     boolean[] testCases = {};
//     CoverageTracker mockTracker = mock(CoverageTracker.class);
//     TestSuiteChromosome chromosome = new TestSuiteChromosome(
//         testCases, mockTracker, Mutation.identity(), Crossover.identity()
//     );

//     assertEquals(0.0, chromosome.computeSizeFitness(), 0.001, "Size fitness should be 0.0 for empty test cases");
//     assertEquals(0, chromosome.getTotalSize(), "Total size should be 0 for empty test cases");
//     assertEquals(0, chromosome.getSelectedSize(), "Selected size should be 0 for empty test cases");
// }
@Test
void testSetNegativeCrowdingDistance() {
    boolean[] testCases = {true, false};
    CoverageTracker mockTracker = mock(CoverageTracker.class);
    TestSuiteChromosome chromosome = new TestSuiteChromosome(
        testCases, mockTracker, Mutation.identity(), Crossover.identity()
    );

    chromosome.setCrowdingDistance(-1.0);
    assertEquals(-1.0, chromosome.getCrowdingDistance(), "Crowding distance should accept negative values");
}
@Test
void testComputeCoverageFitness_allLinesCovered() throws Exception {
    boolean[][] coverageMatrix = {
        {true, true, true},
        {true, true, true},
        {true, true, true}
    };
    CoverageTracker mockTracker = mock(CoverageTracker.class);
    when(mockTracker.getCoverageMatrix()).thenReturn(coverageMatrix);

    boolean[] testCases = {true, true, true};
    TestSuiteChromosome chromosome = new TestSuiteChromosome(
        testCases, mockTracker, Mutation.identity(), Crossover.identity()
    );

    assertEquals(1.0, chromosome.computeCoverageFitness(), 0.001, "Coverage fitness should be 1.0 when all lines are covered");
}
// @Test
// void testNullMutationOperator() {
//     boolean[] testCases = {true, false};
//     CoverageTracker mockTracker = mock(CoverageTracker.class);

//     NullPointerException exception = assertThrows(NullPointerException.class,
//         () -> new TestSuiteChromosome(testCases, mockTracker, null, Crossover.identity()),
//         "Expected NullPointerException for null mutation operator.");
//     assertEquals("Mutation operator cannot be null", exception.getMessage(),
//         "Exception message should match.");
// }


// @Test
// void testNullCrossoverOperator() {
//     boolean[] testCases = {true, false};
//     CoverageTracker mockTracker = mock(CoverageTracker.class);

//     Exception exception = assertThrows(NullPointerException.class, () -> 
//         new TestSuiteChromosome(testCases, mockTracker, Mutation.identity(), null)
//     );

//     assertEquals("Crossover operator cannot be null", exception.getMessage(),
//         "Expected exception with message: Crossover operator cannot be null.");
// }



@Test
void testCrowdingDistanceConsistency() {
    boolean[] testCases = {true, false};
    CoverageTracker mockTracker = mock(CoverageTracker.class);
    TestSuiteChromosome chromosome = new TestSuiteChromosome(
        testCases, mockTracker, Mutation.identity(), Crossover.identity()
    );

    chromosome.setCrowdingDistance(0.0);
    assertEquals(0.0, chromosome.getCrowdingDistance(), "Crowding distance should be 0.0 initially");

    chromosome.setCrowdingDistance(2.5);
    assertEquals(2.5, chromosome.getCrowdingDistance(), "Crowding distance should update to 2.5");

    chromosome.setCrowdingDistance(-1.0);
    assertEquals(-1.0, chromosome.getCrowdingDistance(), "Crowding distance should update to -1.0");
}
// @Test
// void testCopyContainsSameObjectives() {
//     boolean[] testCases = {true, false};
//     CoverageTracker mockTracker = mock(CoverageTracker.class);
//     TestSuiteChromosome original = new TestSuiteChromosome(
//         testCases, mockTracker, Mutation.identity(), Crossover.identity()
//     );

//     original.setObjective(0, 0.6); // Set an objective value
//     original.setObjective(1, 0.9);

//     TestSuiteChromosome copy = original.copy();

//     assertEquals(0.6, copy.getObjective(0), 
//         "Copied chromosome should retain the same objective value at index 0.");
//     assertEquals(0.9, copy.getObjective(1), 
//         "Copied chromosome should retain the same objective value at index 1.");
// }



// @Test
// void testInvalidCoverageMatrixAccess() throws Exception {
//     boolean[][] coverageMatrix = {
//         {true, false, true},
//         {false, true, false}
//     };
//     CoverageTracker mockTracker = mock(CoverageTracker.class);
//     when(mockTracker.getCoverageMatrix()).thenReturn(coverageMatrix);

//     boolean[] testCases = {true, false, true}; // Third test case index is invalid
//     TestSuiteChromosome chromosome = new TestSuiteChromosome(
//         testCases, mockTracker, Mutation.identity(), Crossover.identity()
//     );

//     Exception exception = assertThrows(IndexOutOfBoundsException.class, chromosome::computeCoverageFitness);
//     assertTrue(exception.getMessage().contains("Index"), "Exception message should indicate invalid index.");
// }
@Test
void testDefaultObjectiveInitialization() {
    boolean[] testCases = {true, false};
    CoverageTracker mockTracker = mock(CoverageTracker.class);
    TestSuiteChromosome chromosome = new TestSuiteChromosome(
        testCases, mockTracker, Mutation.identity(), Crossover.identity()
    );

    for (int i = 0; i < 2; i++) { // Assuming two objectives
        assertEquals(0.0, chromosome.getObjective(i), 
            "Default objective value should be 0.0 for index " + i);
    }
}
@Test
void testEqualsSelf() {
    boolean[] testCases = {true, false};
    CoverageTracker mockTracker = mock(CoverageTracker.class);
    TestSuiteChromosome chromosome = new TestSuiteChromosome(
        testCases, mockTracker, Mutation.identity(), Crossover.identity()
    );

    assertEquals(chromosome, chromosome, "Chromosome should be equal to itself.");
}
// @Test
// void testEqualsDifferentMutationOperator() {
//     boolean[] testCases = {true, false};
//     CoverageTracker mockTracker = mock(CoverageTracker.class);
//     Mutation<TestSuiteChromosome> mockMutation1 = mock(Mutation.class);
//     Mutation<TestSuiteChromosome> mockMutation2 = mock(Mutation.class);

//     TestSuiteChromosome chromosome1 = new TestSuiteChromosome(
//         testCases, mockTracker, mockMutation1, Crossover.identity()
//     );
//     TestSuiteChromosome chromosome2 = new TestSuiteChromosome(
//         testCases, mockTracker, mockMutation2, Crossover.identity()
//     );

//     assertNotEquals(chromosome1, chromosome2, "Chromosomes with different mutation operators should not be equal.");
// }
@Test
void testComputeCoverageFitnessAllFalseMatrix() throws Exception {
    boolean[][] coverageMatrix = {
        {false, false, false},
        {false, false, false},
        {false, false, false}
    };
    CoverageTracker mockTracker = mock(CoverageTracker.class);
    when(mockTracker.getCoverageMatrix()).thenReturn(coverageMatrix);

    boolean[] testCases = {true, true, true};
    TestSuiteChromosome chromosome = new TestSuiteChromosome(
        testCases, mockTracker, Mutation.identity(), Crossover.identity()
    );

    double coverageFitness = chromosome.computeCoverageFitness();
    assertEquals(0.0, coverageFitness, 
        "Coverage fitness should be 0.0 when no lines are covered.");
}
@Test
void testComputeSizeFitnessSingleSelection() {
    boolean[] testCases = {false, false, true, false, false};
    CoverageTracker mockTracker = mock(CoverageTracker.class);
    TestSuiteChromosome chromosome = new TestSuiteChromosome(
        testCases, mockTracker, Mutation.identity(), Crossover.identity()
    );

    double sizeFitness = chromosome.computeSizeFitness();
    assertEquals(0.2, sizeFitness, 0.001, 
        "Size fitness should be correct when only one test case is selected.");
}
@Test
void testSetObjectiveInvalidIndex() {
    boolean[] testCases = {true, false};
    CoverageTracker mockTracker = mock(CoverageTracker.class);
    TestSuiteChromosome chromosome = new TestSuiteChromosome(
        testCases, mockTracker, Mutation.identity(), Crossover.identity()
    );

    assertThrows(IndexOutOfBoundsException.class, () -> chromosome.setObjective(-1, 0.5));
    assertThrows(IndexOutOfBoundsException.class, () -> chromosome.setObjective(3, 0.5));
}
@Test
void testPartialLineCoverage() throws Exception {
    boolean[][] coverageMatrix = {
        {true, false, true},
        {false, true, false},
        {false, false, false}
    };
    CoverageTracker mockTracker = mock(CoverageTracker.class);
    when(mockTracker.getCoverageMatrix()).thenReturn(coverageMatrix);

    boolean[] testCases = {true, false, true};
    TestSuiteChromosome chromosome = new TestSuiteChromosome(
        testCases, mockTracker, Mutation.identity(), Crossover.identity()
    );

    double coverageFitness = chromosome.computeCoverageFitness();
    assertEquals(0.666, coverageFitness, 0.001, 
        "Coverage fitness should calculate correctly for partial line coverage.");
}
@Test
void testCopyIndependence() {
    boolean[] testCases = {true, false};
    CoverageTracker mockTracker = mock(CoverageTracker.class);
    TestSuiteChromosome original = new TestSuiteChromosome(
        testCases, mockTracker, Mutation.identity(), Crossover.identity()
    );

    TestSuiteChromosome copy = original.copy();
    original.setObjective(0, 0.5);

    assertNotSame(original, copy, "Copy should be a different instance.");
    assertNotEquals(original.getObjective(0), copy.getObjective(0), 
        "Copy should not reflect changes made to the original.");
}


}
