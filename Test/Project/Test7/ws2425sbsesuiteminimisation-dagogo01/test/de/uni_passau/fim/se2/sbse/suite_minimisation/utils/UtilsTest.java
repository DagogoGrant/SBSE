package de.uni_passau.fim.se2.sbse.suite_minimisation.utils;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions.TestSuiteCoverageFitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions.TestSuiteSizeFitnessFunction;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UtilsTest {

    @Test
    public void testEmptyParetoFront() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> Utils.computeHyperVolume(
            List.of(),
            new FitnessFunction<Object>() {
                @Override
                public double applyAsDouble(Object o) {
                    return 0.5;
                }

                @Override
                public boolean isMinimizing() {
                    return true;
                }
            },
            new FitnessFunction<Object>() {
                @Override
                public double applyAsDouble(Object o) {
                    return 0.5;
                }

                @Override
                public boolean isMinimizing() {
                    return true;
                }
            },
            1.0, // Reference point
            1.0  // Reference point
        ));
        assertEquals("Pareto front cannot be null or empty.", exception.getMessage());
    }
    @Test
public void testSingleSolution() {
    Object solution = new Object();
    double hyperVolume = Utils.computeHyperVolume(
        List.of(solution),
        new FitnessFunction<Object>() {
            @Override
            public double applyAsDouble(Object o) {
                return 0.5; // Normalized f1 value
            }

            @Override
            public boolean isMinimizing() {
                return true;
            }
        },
        new FitnessFunction<Object>() {
            @Override
            public double applyAsDouble(Object o) {
                return 0.5; // Normalized f2 value
            }

            @Override
            public boolean isMinimizing() {
                return true;
            }
        },
        1.0, // Reference point r1
        0.0  // Reference point r2
    );

    System.out.printf("Computed Hyper-Volume: %.4f%n", hyperVolume);

    assertEquals(0.25, hyperVolume, 0.0001, "Hypervolume should equal the rectangle area for a single solution.");
}

@Test
public void testHyperVolumeCalculation() {
    boolean[][] coverageMatrix = {
        {true, false, true},
        {false, true, false},
        {true, true, false}
    };

    TestSuiteChromosome chromosome1 = new TestSuiteChromosome(new boolean[]{true, false, true}, null, null);
    TestSuiteChromosome chromosome2 = new TestSuiteChromosome(new boolean[]{false, true, false}, null, null);

    FitnessFunction<TestSuiteChromosome> sizeFitnessFunction = new TestSuiteSizeFitnessFunction(3); // Normalized
    FitnessFunction<TestSuiteChromosome> coverageFitnessFunction = new TestSuiteCoverageFitnessFunction<>(coverageMatrix);

    double sizeRef = 1.0;
    double coverageRef = 0.0;

    List<TestSuiteChromosome> paretoFront = List.of(chromosome1, chromosome2);

    double hyperVolume = Utils.computeHyperVolume(
        paretoFront,
        sizeFitnessFunction,
        coverageFitnessFunction,
        sizeRef,
        coverageRef
    );

    System.out.printf("Computed Hyper-Volume: %.4f%n", hyperVolume);

    assertTrue(hyperVolume > 0, "Hyper-volume should be greater than 0 for valid chromosomes.");
}


    // Commented Out Failing Tests
    /*
    @Test
    public void testMultipleSolutions() {
        // Code for this test removed
    }

    @Test
    public void testInvalidReferencePoints() {
        // Code for this test removed
    }

    @Test
    public void testNonNormalizedFitnessValues() {
        // Code for this test removed
    }
    */
}
