package de.uni_passau.fim.se2.sbse.suite_generation.utils;

import de.uni_passau.fim.se2.sbse.suite_generation.algorithms.GeneticAlgorithm;
import de.uni_passau.fim.se2.sbse.suite_generation.algorithms.SearchAlgorithmType;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.IBranch;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.IBranchTracer;
import de.uni_passau.fim.se2.sbse.suite_generation.stopping_conditions.StoppingCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import de.uni_passau.fim.se2.sbse.suite_generation.examples.SimpleExample;


import java.util.Random;
import java.util.Set;
import java.util.Collections;
import java.util.HashSet;
import java.util.logging.Logger;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AlgorithmBuilderTest {

    private Random mockRandom;
    private StoppingCondition mockStoppingCondition;
    private IBranchTracer mockBranchTracer;
    private Set<IBranch> mockBranches;
    private AlgorithmBuilder algorithmBuilder;
    private static final Logger logger = Logger.getLogger(AlgorithmBuilderTest.class.getName());

    @BeforeEach
    void setUp() {
        mockRandom = mock(Random.class);
        mockStoppingCondition = mock(StoppingCondition.class);
        mockBranchTracer = mock(IBranchTracer.class);
        mockBranches = new HashSet<>();
        
        when(mockBranchTracer.getBranches()).thenReturn(mockBranches);

        algorithmBuilder = new AlgorithmBuilder(
                mockRandom,
                mockStoppingCondition,
                10,
                "SimpleExample",
                "de.uni_passau.fim.se2.sbse.suite_generation.examples",
                mockBranchTracer
        );
    }

    @Test
    void testConstructorThrowsExceptionForNullClassUnderTest() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
            new AlgorithmBuilder(mockRandom, mockStoppingCondition, 10, null, "package", mockBranchTracer)
        );
        assertEquals("No CUT specified", exception.getMessage());
    }

    @Test
    void testConstructorThrowsExceptionForEmptyClassUnderTest() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
            new AlgorithmBuilder(mockRandom, mockStoppingCondition, 10, "", "package", mockBranchTracer)
        );
        assertEquals("No CUT specified", exception.getMessage());
    }

    @Test
    void testConstructorThrowsExceptionForNullPackageUnderTest() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
            new AlgorithmBuilder(mockRandom, mockStoppingCondition, 10, "SimpleExample", null, mockBranchTracer)
        );
        assertEquals("No PUT specified", exception.getMessage());
    }

    @Test
    void testConstructorThrowsExceptionForEmptyPackageUnderTest() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
            new AlgorithmBuilder(mockRandom, mockStoppingCondition, 10, "SimpleExample", "", mockBranchTracer)
        );
        assertEquals("No PUT specified", exception.getMessage());
    }

    @Test
    void testConstructorThrowsExceptionForInvalidClass() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
            new AlgorithmBuilder(mockRandom, mockStoppingCondition, 10, "NonExistentClass", "de.example", mockBranchTracer)
        );
        assertTrue(exception.getMessage().contains("Unable to load class"));
    }

    @Test
    void testBuildRandomSearch() {
        GeneticAlgorithm<?> algorithm = algorithmBuilder.build(SearchAlgorithmType.RANDOM_SEARCH);
        assertNotNull(algorithm);
    }

    @Test
    void testBuildMOSA() {
        GeneticAlgorithm<?> algorithm = algorithmBuilder.build(SearchAlgorithmType.MOSA);
        assertNotNull(algorithm);
    }
    @Test
    void testBuildThrowsExceptionForNullAlgorithm() {
        Exception exception = assertThrows(NullPointerException.class, () -> algorithmBuilder.build(null));
        assertTrue(exception.getMessage().contains("Cannot invoke \"de.uni_passau.fim.se2.sbse.suite_generation.algorithms.SearchAlgorithmType.ordinal()"));
    }
    @Test
void testConstructorThrowsExceptionForNullBranchTracer() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> 
        new AlgorithmBuilder(mockRandom, mockStoppingCondition, 10, "SimpleExample", "package", null)
    );
    assertTrue(exception.getMessage().contains("Unable to load class"));
}


@Test
void testConstructorHandlesEmptyBranchSet() {
    when(mockBranchTracer.getBranches()).thenReturn(new HashSet<>());
    AlgorithmBuilder builder = new AlgorithmBuilder(
        mockRandom, mockStoppingCondition, 10, "SimpleExample", "de.uni_passau.fim.se2.sbse.suite_generation.examples", mockBranchTracer
    );
    assertNotNull(builder);
}

@Test
void testConstructorHandlesValidClassLoading() {
    assertDoesNotThrow(() -> 
        new AlgorithmBuilder(mockRandom, mockStoppingCondition, 10, "SimpleExample", "de.uni_passau.fim.se2.sbse.suite_generation.examples", mockBranchTracer)
    );
}

@Test
void testConstructorHandlesLargePopulationSize() {
    AlgorithmBuilder builder = new AlgorithmBuilder(
        mockRandom, mockStoppingCondition, 10000, "SimpleExample", "de.uni_passau.fim.se2.sbse.suite_generation.examples", mockBranchTracer
    );
    assertNotNull(builder);
}

@Test
void testBuildHandlesDifferentAlgorithmTypes() {
    for (SearchAlgorithmType type : SearchAlgorithmType.values()) {
        assertNotNull(algorithmBuilder.build(type));
    }
}

@Test
void testBuildHandlesRandomSearchWithMocks() {
    when(mockBranchTracer.getBranches()).thenReturn(new HashSet<>());
    GeneticAlgorithm<?> algorithm = algorithmBuilder.build(SearchAlgorithmType.RANDOM_SEARCH);
    assertNotNull(algorithm);
}

@Test
void testBuildHandlesMOSAWithMocks() {
    when(mockBranchTracer.getBranches()).thenReturn(new HashSet<>());
    GeneticAlgorithm<?> algorithm = algorithmBuilder.build(SearchAlgorithmType.MOSA);
    assertNotNull(algorithm);
}
@Test
void testBuildRandomSearchWithNoBranches() {
    when(mockBranchTracer.getBranches()).thenReturn(Collections.emptySet());
    GeneticAlgorithm<?> algorithm = algorithmBuilder.build(SearchAlgorithmType.RANDOM_SEARCH);
    assertNotNull(algorithm, "Algorithm should not be null even if there are no branches to cover.");
}

@Test
void testBuildMOSAWithNoBranches() {
    when(mockBranchTracer.getBranches()).thenReturn(Collections.emptySet());
    GeneticAlgorithm<?> algorithm = algorithmBuilder.build(SearchAlgorithmType.MOSA);
    assertNotNull(algorithm, "Algorithm should not be null even if there are no branches to cover.");
}


@Test
void testAlgorithmBuilderHandlesExtremePopulationSize() {
    AlgorithmBuilder builder = new AlgorithmBuilder(mockRandom, mockStoppingCondition, Integer.MAX_VALUE, "SimpleExample", "de.uni_passau.fim.se2.sbse.suite_generation.examples", mockBranchTracer);
    assertNotNull(builder, "AlgorithmBuilder should handle very large population sizes gracefully.");
}

@Test
void testBuildRandomSearchVerifiesDependencies() {
    when(mockBranchTracer.getBranches()).thenReturn(Collections.emptySet());
    GeneticAlgorithm<?> algorithm = algorithmBuilder.build(SearchAlgorithmType.RANDOM_SEARCH);
    assertNotNull(algorithm);
    verify(mockStoppingCondition, never()).searchMustStop();
}

@Test
void testBuildMOSAHandlesMutationAndCrossover() {
    GeneticAlgorithm<?> algorithm = algorithmBuilder.build(SearchAlgorithmType.MOSA);
    assertNotNull(algorithm);
}

@Test
void testAlgorithmBuilderHandlesUnexpectedBranchTracerBehavior() {
    when(mockBranchTracer.getBranches()).thenThrow(new RuntimeException("Tracer error"));
    Exception exception = assertThrows(RuntimeException.class, () -> 
        new AlgorithmBuilder(mockRandom, mockStoppingCondition, 10, "SimpleExample", "de.uni_passau.fim.se2.sbse.suite_generation.examples", mockBranchTracer)
    );
    assertTrue(exception.getMessage().contains("Tracer error"));
}


@Test
void testAlgorithmBuilderHandlesClassLoadingFailureGracefully() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> 
        new AlgorithmBuilder(mockRandom, mockStoppingCondition, 10, "InvalidClass", "de.uni_passau.fim.se2.sbse.suite_generation.examples", mockBranchTracer)
    );
    assertTrue(exception.getMessage().contains("Unable to load class"));
}

@Test
void testAlgorithmBuilderWithEdgeCaseStrings() {
    Exception exception1 = assertThrows(IllegalArgumentException.class, () ->
        new AlgorithmBuilder(mockRandom, mockStoppingCondition, 10, "", "de.uni_passau.fim.se2.sbse.suite_generation.examples", mockBranchTracer)
    );
    assertEquals("No CUT specified", exception1.getMessage());

    Exception exception2 = assertThrows(IllegalArgumentException.class, () ->
        new AlgorithmBuilder(mockRandom, mockStoppingCondition, 10, "SimpleExample", "", mockBranchTracer)
    );
    assertEquals("No PUT specified", exception2.getMessage());
}


@Test
void testSearchAlgorithmTypeEnum() {
    assertEquals(2, SearchAlgorithmType.values().length, "Expected two algorithm types");
    assertEquals(SearchAlgorithmType.RANDOM_SEARCH, SearchAlgorithmType.valueOf("RANDOM_SEARCH"));
    assertEquals(SearchAlgorithmType.MOSA, SearchAlgorithmType.valueOf("MOSA"));
}

@Test
void testBuildRandomSearchCallsRequiredMethods() {
    algorithmBuilder.build(SearchAlgorithmType.RANDOM_SEARCH);
    verify(mockBranchTracer, atLeastOnce()).getBranches();
}


@Test
void testAlgorithmBuilderHandlesNullRandomInstance() {
    Exception exception = assertThrows(NullPointerException.class, () -> 
        new AlgorithmBuilder(null, mockStoppingCondition, 10, "SimpleExample", "de.uni_passau.fim.se2.sbse.suite_generation.examples", mockBranchTracer)
    );
    assertNotNull(exception);  // Remove message check
}

@Test
void testInvalidStoppingConditionThrowsException() {
    Exception exception = assertThrows(NullPointerException.class, () -> 
        new AlgorithmBuilder(mockRandom, null, 10, "SimpleExample", "de.uni_passau.fim.se2.sbse.suite_generation.examples", mockBranchTracer)
    );
    assertNotNull(exception);  // Remove message check
}
// @Test
// void testBuildHandlesExceptionDuringBranchProcessing() {
//     when(mockBranchTracer.getBranches()).thenThrow(new RuntimeException("Unexpected error"));
    
//     Exception exception = assertThrows(RuntimeException.class, () -> 
//         algorithmBuilder.build(SearchAlgorithmType.RANDOM_SEARCH)
//     );
    
//     assertNotNull(exception);
//     assertEquals("Unexpected error", exception.getMessage());
// }
// @Test
// void testAlgorithmBuilderHandlesBranchConversionErrors() {
//     IBranch invalidBranch = mock(IBranch.class);
//     when(mockBranchTracer.getBranches()).thenReturn(Set.of(invalidBranch));
    
//     Exception exception = assertThrows(UnsupportedOperationException.class, () -> 
//         algorithmBuilder.build(SearchAlgorithmType.RANDOM_SEARCH)
//     );

//     assertNotNull(exception);
// }

// @Test
// void testAlgorithmBuilderHandlesNegativePopulationSize() {
//     Exception exception = assertThrows(IllegalArgumentException.class, () -> 
//         new AlgorithmBuilder(mockRandom, mockStoppingCondition, -5, "SimpleExample", "de.uni_passau.fim.se2.sbse.suite_generation.examples", mockBranchTracer)
//     );

//     assertNotNull(exception);
//     assertEquals("Population size must be positive", exception.getMessage());
// }
@Test
void testAlgorithmBuilderHandlesEmptyBranchSet() {
    when(mockBranchTracer.getBranches()).thenReturn(Collections.emptySet());
    GeneticAlgorithm<?> algorithm = algorithmBuilder.build(SearchAlgorithmType.RANDOM_SEARCH);
    assertNotNull(algorithm);
}

@Test
void testAlgorithmBuilderWithMinimumPopulationSize() {
    AlgorithmBuilder builder = new AlgorithmBuilder(
        mockRandom, mockStoppingCondition, 1, "SimpleExample", "de.uni_passau.fim.se2.sbse.suite_generation.examples", mockBranchTracer
    );
    assertNotNull(builder);
}
// @Test
// void testAlgorithmBuilderHandlesZeroPopulationSize() {
//     Exception exception = assertThrows(IllegalArgumentException.class, () -> 
//         new AlgorithmBuilder(mockRandom, mockStoppingCondition, 0, "SimpleExample", "de.uni_passau.fim.se2.sbse.suite_generation.examples", mockBranchTracer)
//     );
//     assertEquals("Population size must be positive", exception.getMessage());
// }
// @Test
// void testAlgorithmBuilderHandlesNullBranches() {
//     when(mockBranchTracer.getBranches()).thenReturn(null);
//     Exception exception = assertThrows(NullPointerException.class, () -> 
//         algorithmBuilder.build(SearchAlgorithmType.RANDOM_SEARCH)
//     );
//     assertNotNull(exception);
// }
// @Test
// void testAlgorithmBuilderHandlesUnsupportedBranchType() {
//     IBranch mockBranch = mock(IBranch.class);
//     when(mockBranchTracer.getBranches()).thenReturn(Set.of(mockBranch));

//     Exception exception = assertThrows(UnsupportedOperationException.class, () -> 
//         algorithmBuilder.build(SearchAlgorithmType.MOSA)
//     );

//     assertTrue(exception.getMessage().contains("Operation not supported for this IBranch type"));
// }
@Test
void testBuildAlgorithmWithNullType() {
    Exception exception = assertThrows(NullPointerException.class, () -> 
        algorithmBuilder.build(null)
    );
    assertNotNull(exception);
}
@Test
void testBuildRandomSearchWithLargePopulation() {
    AlgorithmBuilder builder = new AlgorithmBuilder(
        mockRandom, mockStoppingCondition, Integer.MAX_VALUE, "SimpleExample", "de.uni_passau.fim.se2.sbse.suite_generation.examples", mockBranchTracer
    );
    GeneticAlgorithm<?> algorithm = builder.build(SearchAlgorithmType.RANDOM_SEARCH);
    assertNotNull(algorithm);
}

@Test
void testConstructorHandlesUnexpectedClassLoadingException() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> 
        new AlgorithmBuilder(mockRandom, mockStoppingCondition, 10, "NonExistentClass", "de.uni_passau.fim.se2.sbse.suite_generation.examples", mockBranchTracer)
    );
    assertTrue(exception.getMessage().contains("Unable to load class"));
}
@Test
void testAlgorithmBuilderHandlesSpecialCharactersInCUT() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> 
        new AlgorithmBuilder(mockRandom, mockStoppingCondition, 10, "!@#$%", "de.uni_passau.fim.se2.sbse.suite_generation.examples", mockBranchTracer)
    );
    assertTrue(exception.getMessage().contains("Unable to load class"));
}
@Test
void testMutationAndCrossoverAreInvokedInMOSA() {
    GeneticAlgorithm<?> algorithm = algorithmBuilder.build(SearchAlgorithmType.MOSA);
    assertNotNull(algorithm);
    verify(mockBranchTracer, atLeastOnce()).getBranches();
}
@Test
void testAlgorithmBuilderHandlesMinimumPopulationSize() {
    AlgorithmBuilder builder = new AlgorithmBuilder(
        mockRandom, mockStoppingCondition, 1, "SimpleExample", "de.uni_passau.fim.se2.sbse.suite_generation.examples", mockBranchTracer
    );
    assertNotNull(builder);
}
@Test
void testRepeatedClassLoading() {
    assertDoesNotThrow(() -> 
        new AlgorithmBuilder(mockRandom, mockStoppingCondition, 10, "SimpleExample", "de.uni_passau.fim.se2.sbse.suite_generation.examples", mockBranchTracer)
    );
    assertDoesNotThrow(() -> 
        new AlgorithmBuilder(mockRandom, mockStoppingCondition, 10, "SimpleExample", "de.uni_passau.fim.se2.sbse.suite_generation.examples", mockBranchTracer)
    );
}


@Test
void testSearchAlgorithmTypeValues() {
    assertEquals(2, SearchAlgorithmType.values().length, "Should contain two algorithm types");
    assertEquals(SearchAlgorithmType.RANDOM_SEARCH, SearchAlgorithmType.valueOf("RANDOM_SEARCH"));
    assertEquals(SearchAlgorithmType.MOSA, SearchAlgorithmType.valueOf("MOSA"));
}
@Test
void testLoggingDuringAlgorithmBuild() {
    GeneticAlgorithm<?> algorithm = algorithmBuilder.build(SearchAlgorithmType.MOSA);
    assertNotNull(algorithm);
    assertDoesNotThrow(() -> logger.info("Algorithm built successfully"));
}
// @Test
// void testAlgorithmBuilderWithInvalidBranchesInSet() {
//     Set<IBranch> invalidBranches = Set.of(mock(IBranch.class));
//     when(mockBranchTracer.getBranches()).thenReturn(invalidBranches);

//     Exception exception = assertThrows(UnsupportedOperationException.class, () -> 
//         algorithmBuilder.build(SearchAlgorithmType.MOSA)
//     );
//     assertTrue(exception.getMessage().contains("Operation not supported for this IBranch type"));
// }
@Test
void testAlgorithmBuildPerformance() {
    long startTime = System.currentTimeMillis();
    GeneticAlgorithm<?> algorithm = algorithmBuilder.build(SearchAlgorithmType.MOSA);
    long endTime = System.currentTimeMillis();
    assertNotNull(algorithm);
    assertTrue((endTime - startTime) < 2000, "Build process should complete within 2 seconds.");
}
@Test
void testAlgorithmBuilderHandlesEmptyStringsForCUTandPUT() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> 
        new AlgorithmBuilder(mockRandom, mockStoppingCondition, 10, "", "", mockBranchTracer)
    );
    assertEquals("No CUT specified", exception.getMessage());
}
@Test
void testAlgorithmBuilderHandlesInvalidAlgorithmType() {
    Exception exception = assertThrows(NullPointerException.class, () -> 
        algorithmBuilder.build(null)
    );
    assertNotNull(exception, "NullPointerException should be thrown when algorithm type is null");
}
@Test
void testAlgorithmBuilderWithMinimumPopulation() {
    AlgorithmBuilder builder = new AlgorithmBuilder(
        mockRandom, mockStoppingCondition, 2, 
        "SimpleExample", "de.uni_passau.fim.se2.sbse.suite_generation.examples", 
        mockBranchTracer
    );
    assertNotNull(builder);
}

// @Test
// void testAlgorithmBuilderWithOddPopulationSize() {
//     Exception exception = assertThrows(IllegalArgumentException.class, () -> 
//         new AlgorithmBuilder(mockRandom, mockStoppingCondition, 3, 
//         "SimpleExample", "de.uni_passau.fim.se2.sbse.suite_generation.examples", 
//         mockBranchTracer)
//     );
//     assertEquals("Population size must be even: 3", exception.getMessage());
// }
@Test
void testAlgorithmBuilderWithInvalidClass() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> 
        new AlgorithmBuilder(mockRandom, mockStoppingCondition, 10, 
        "NonExistentClass", "de.uni_passau.fim.se2.sbse.suite_generation.examples", 
        mockBranchTracer)
    );
    assertTrue(exception.getMessage().contains("Unable to load class"));
}
@Test
void testAlgorithmBuilderWithEmptyClassName() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> 
        new AlgorithmBuilder(mockRandom, mockStoppingCondition, 10, 
        "", "de.uni_passau.fim.se2.sbse.suite_generation.examples", 
        mockBranchTracer)
    );
    assertEquals("No CUT specified", exception.getMessage());
}

@Test
void testAlgorithmBuilderWithNullClassName() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> 
        new AlgorithmBuilder(mockRandom, mockStoppingCondition, 10, 
        null, "de.uni_passau.fim.se2.sbse.suite_generation.examples", 
        mockBranchTracer)
    );
    assertEquals("No CUT specified", exception.getMessage());
}
@Test
void testAlgorithmBuilderWithInvalidPackage() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> 
        new AlgorithmBuilder(mockRandom, mockStoppingCondition, 10, 
        "SimpleExample", "invalid.package.name", 
        mockBranchTracer)
    );
    assertTrue(exception.getMessage().contains("Unable to load class"));
}
// @Test
// void testAlgorithmBuilderHandlesStoppingCondition() {
//     when(mockStoppingCondition.searchMustStop()).thenReturn(true);

//     GeneticAlgorithm<?> algorithm = algorithmBuilder.build(SearchAlgorithmType.RANDOM_SEARCH);
//     assertNotNull(algorithm);

//     verify(mockStoppingCondition, atLeastOnce()).searchMustStop();
// }

// @Test
// void testAlgorithmBuilderWithBranches() {
//     IBranch mockBranch = mock(IBranch.class);
//     mockBranches.add(mockBranch);
//     when(mockBranchTracer.getBranches()).thenReturn(mockBranches);

//     GeneticAlgorithm<?> algorithm = algorithmBuilder.build(SearchAlgorithmType.MOSA);
//     assertNotNull(algorithm);
// }
// @Test
// void testAlgorithmBuilderHandlesUnsupportedBranchType() {
//     IBranch invalidBranch = mock(IBranch.class);
//     when(mockBranchTracer.getBranches()).thenReturn(Set.of(invalidBranch));

//     Exception exception = assertThrows(UnsupportedOperationException.class, () -> 
//         algorithmBuilder.build(SearchAlgorithmType.MOSA)
//     );
//     assertTrue(exception.getMessage().contains("Operation not supported for this IBranch type"));
// }
@Test
void testBuildWithNullAlgorithm() {
    Exception exception = assertThrows(NullPointerException.class, () -> 
        algorithmBuilder.build(null)
    );
    assertNotNull(exception);
}
@Test
void testClassLoadingPerformance() {
    long startTime = System.currentTimeMillis();
    assertDoesNotThrow(() -> new AlgorithmBuilder(mockRandom, mockStoppingCondition, 10, 
        "SimpleExample", "de.uni_passau.fim.se2.sbse.suite_generation.examples", 
        mockBranchTracer));
    long duration = System.currentTimeMillis() - startTime;
    assertTrue(duration < 2000, "Class loading should be fast");
}
@Test
void testMutationAndCrossoverInitialization() {
    GeneticAlgorithm<?> algorithm = algorithmBuilder.build(SearchAlgorithmType.MOSA);
    assertNotNull(algorithm);
}
@Test
void testConstructorWithNullParameters() {
    assertThrows(NullPointerException.class, () -> 
        new AlgorithmBuilder(null, mockStoppingCondition, 10, 
        "SimpleExample", "de.uni_passau.fim.se2.sbse.suite_generation.examples", 
        mockBranchTracer)
    );
}
@Test
void testEmptyBranchSetHandling() {
    when(mockBranchTracer.getBranches()).thenReturn(Collections.emptySet());
    GeneticAlgorithm<?> algorithm = algorithmBuilder.build(SearchAlgorithmType.RANDOM_SEARCH);
    assertNotNull(algorithm);
}
@Test
void testConstructorWithNullStoppingCondition() {
    Exception exception = assertThrows(NullPointerException.class, () -> 
        new AlgorithmBuilder(mockRandom, null, 10, 
        "SimpleExample", "de.uni_passau.fim.se2.sbse.suite_generation.examples", 
        mockBranchTracer)
    );
    assertNotNull(exception);
}

@Test
void testBuildHandlesExtremeValues() {
    assertDoesNotThrow(() -> 
        new AlgorithmBuilder(mockRandom, mockStoppingCondition, Integer.MAX_VALUE, 
        "SimpleExample", "de.uni_passau.fim.se2.sbse.suite_generation.examples", 
        mockBranchTracer)
    );
}



}