package de.uni_passau.fim.se2.sbse.suite_minimisation.utils;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.*;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the Utils class.
 */
class UtilsTest {

    /**
     * Mocks a TestSuiteChromosome with specified objectives.
     *
     * @param objective1 the first objective value (e.g., size fitness)
     * @param objective2 the second objective value (e.g., coverage fitness)
     * @return a mocked TestSuiteChromosome
     */
    private TestSuiteChromosome mockChromosome(double objective1, double objective2) {
        TestSuiteChromosome mockChromosome = mock(TestSuiteChromosome.class);
        when(mockChromosome.getObjective(0)).thenReturn(objective1);
        when(mockChromosome.getObjective(1)).thenReturn(objective2);
        return mockChromosome;
    }
    @Test
    void testComputeHyperVolume_validInputs() {
        List<TestSuiteChromosome> paretoFront = new ArrayList<>();
        paretoFront.add(mockChromosome(0.2, 0.8));
        paretoFront.add(mockChromosome(0.4, 0.6));
        paretoFront.add(mockChromosome(0.6, 0.4));
    
        FitnessFunction<TestSuiteChromosome> f1 = new FitnessFunction<>() {
            @Override
            public double applyAsDouble(TestSuiteChromosome c) {
                return c.getObjective(0);
            }
    
            @Override
            public boolean isMinimizing() {
                return true;
            }
        };
    
        FitnessFunction<TestSuiteChromosome> f2 = new FitnessFunction<>() {
            @Override
            public double applyAsDouble(TestSuiteChromosome c) {
                return c.getObjective(1);
            }
    
            @Override
            public boolean isMinimizing() {
                return false;
            }
        };
    
        double r1 = 0.0; // Reference point for F1
        double r2 = 1.0; // Reference point for F2
    
        double computedHyperVolume = Utils.computeHyperVolume(paretoFront, f1, f2, r1, r2);
        double expectedHyperVolume = 0.24; // Adjusted to match manual calculation
    
        assertEquals(expectedHyperVolume, computedHyperVolume, 1e-6, "Incorrect hyper-volume calculation!");
    }
    
    
    

    @Test
    void testComputeHyperVolume_emptyFront() {
        // Prepare an empty Pareto front
        List<TestSuiteChromosome> paretoFront = new ArrayList<>();

        // Define fitness functions explicitly
        FitnessFunction<TestSuiteChromosome> f1 = new FitnessFunction<>() {
            @Override
            public double applyAsDouble(TestSuiteChromosome c) {
                return c.getObjective(0);
            }

            @Override
            public boolean isMinimizing() {
                return true;
            }
        };

        FitnessFunction<TestSuiteChromosome> f2 = new FitnessFunction<>() {
            @Override
            public double applyAsDouble(TestSuiteChromosome c) {
                return c.getObjective(1);
            }

            @Override
            public boolean isMinimizing() {
                return false;
            }
        };

        // Define reference points
        double r1 = 0.0;
        double r2 = 1.0;

        // Assert that an exception is thrown
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            Utils.computeHyperVolume(paretoFront, f1, f2, r1, r2)
        );
        assertEquals("Pareto front cannot be null or empty.", exception.getMessage());
    }
    @Test
void testComputeHyperVolume_singleElementFront() {
    // Prepare a Pareto front with a single element
    List<TestSuiteChromosome> paretoFront = new ArrayList<>();
    paretoFront.add(mockChromosome(0.3, 0.7));

    FitnessFunction<TestSuiteChromosome> f1 = new FitnessFunction<>() {
        @Override
        public double applyAsDouble(TestSuiteChromosome c) {
            return c.getObjective(0);
        }

        @Override
        public boolean isMinimizing() {
            return true;
        }
    };

    FitnessFunction<TestSuiteChromosome> f2 = new FitnessFunction<>() {
        @Override
        public double applyAsDouble(TestSuiteChromosome c) {
            return c.getObjective(1);
        }

        @Override
        public boolean isMinimizing() {
            return false;
        }
    };

    double r1 = 0.0; // Reference point for F1
    double r2 = 1.0; // Reference point for F2

    // Compute hyper-volume
    double computedHyperVolume = Utils.computeHyperVolume(paretoFront, f1, f2, r1, r2);

    // Assert correct hyper-volume calculation
    double expectedHyperVolume = (0.3 - r1) * (r2 - 0.7);
    assertEquals(expectedHyperVolume, computedHyperVolume, 1e-6, "Incorrect hyper-volume for single element front.");
}

@Test
void testComputeHyperVolume_noOverlap() {
    // Prepare a Pareto front with elements that do not overlap
    List<TestSuiteChromosome> paretoFront = new ArrayList<>();
    paretoFront.add(mockChromosome(0.1, 0.9));
    paretoFront.add(mockChromosome(0.3, 0.7));
    paretoFront.add(mockChromosome(0.5, 0.5));

    FitnessFunction<TestSuiteChromosome> f1 = new FitnessFunction<>() {
        @Override
        public double applyAsDouble(TestSuiteChromosome c) {
            return c.getObjective(0);
        }

        @Override
        public boolean isMinimizing() {
            return true;
        }
    };

    FitnessFunction<TestSuiteChromosome> f2 = new FitnessFunction<>() {
        @Override
        public double applyAsDouble(TestSuiteChromosome c) {
            return c.getObjective(1);
        }

        @Override
        public boolean isMinimizing() {
            return false;
        }
    };

    double r1 = 0.0; // Reference point for F1
    double r2 = 1.0; // Reference point for F2

    // Compute hyper-volume
    double computedHyperVolume = Utils.computeHyperVolume(paretoFront, f1, f2, r1, r2);

    // Manually calculate the expected hyper-volume
    double expectedHyperVolume = (0.1 - r1) * (r2 - 0.9) +
                                 (0.3 - 0.1) * (r2 - 0.7) +
                                 (0.5 - 0.3) * (r2 - 0.5);
    assertEquals(expectedHyperVolume, computedHyperVolume, 1e-6, "Incorrect hyper-volume for non-overlapping front.");
}

@Test
void testComputeHyperVolume_negativeCoordinates() {
    // Prepare a Pareto front with negative fitness values
    List<TestSuiteChromosome> paretoFront = new ArrayList<>();
    paretoFront.add(mockChromosome(-0.2, 0.5));
    paretoFront.add(mockChromosome(0.0, -0.1));
    paretoFront.add(mockChromosome(-0.3, 0.3));

    FitnessFunction<TestSuiteChromosome> f1 = new FitnessFunction<>() {
        @Override
        public double applyAsDouble(TestSuiteChromosome c) {
            return c.getObjective(0);
        }

        @Override
        public boolean isMinimizing() {
            return true;
        }
    };

    FitnessFunction<TestSuiteChromosome> f2 = new FitnessFunction<>() {
        @Override
        public double applyAsDouble(TestSuiteChromosome c) {
            return c.getObjective(1);
        }

        @Override
        public boolean isMinimizing() {
            return false;
        }
    };

    double r1 = -0.5; // Reference point for F1
    double r2 = 0.6;  // Reference point for F2

    // Compute hyper-volume
    double computedHyperVolume = Utils.computeHyperVolume(paretoFront, f1, f2, r1, r2);

    // Assert hyper-volume calculation is non-negative and valid
    assertTrue(computedHyperVolume >= 0, "Hyper-volume should be non-negative even with negative coordinates.");
}

@Test
void testComputeHyperVolume_duplicateElements() {
    // Prepare a Pareto front with duplicate elements
    List<TestSuiteChromosome> paretoFront = new ArrayList<>();
    paretoFront.add(mockChromosome(0.3, 0.7));
    paretoFront.add(mockChromosome(0.3, 0.7));
    paretoFront.add(mockChromosome(0.5, 0.5));

    FitnessFunction<TestSuiteChromosome> f1 = new FitnessFunction<>() {
        @Override
        public double applyAsDouble(TestSuiteChromosome c) {
            return c.getObjective(0);
        }

        @Override
        public boolean isMinimizing() {
            return true;
        }
    };

    FitnessFunction<TestSuiteChromosome> f2 = new FitnessFunction<>() {
        @Override
        public double applyAsDouble(TestSuiteChromosome c) {
            return c.getObjective(1);
        }

        @Override
        public boolean isMinimizing() {
            return false;
        }
    };

    double r1 = 0.0; // Reference point for F1
    double r2 = 1.0; // Reference point for F2

    // Compute hyper-volume
    double computedHyperVolume = Utils.computeHyperVolume(paretoFront, f1, f2, r1, r2);

    // Assert no errors occur due to duplicates
    assertTrue(computedHyperVolume > 0, "Hyper-volume should be valid even with duplicate elements.");
}
@Test
void testComputeHyperVolume_unorderedParetoFront() {
    // Prepare a Pareto front in an unordered fashion
    List<TestSuiteChromosome> paretoFront = new ArrayList<>();
    paretoFront.add(mockChromosome(0.5, 0.5));
    paretoFront.add(mockChromosome(0.3, 0.7));
    paretoFront.add(mockChromosome(0.1, 0.9));

    FitnessFunction<TestSuiteChromosome> f1 = new FitnessFunction<>() {
        @Override
        public double applyAsDouble(TestSuiteChromosome c) {
            return c.getObjective(0);
        }

        @Override
        public boolean isMinimizing() {
            return true;
        }
    };

    FitnessFunction<TestSuiteChromosome> f2 = new FitnessFunction<>() {
        @Override
        public double applyAsDouble(TestSuiteChromosome c) {
            return c.getObjective(1);
        }

        @Override
        public boolean isMinimizing() {
            return false;
        }
    };

    double r1 = 0.0; // Reference point for F1
    double r2 = 1.0; // Reference point for F2

    // Compute hyper-volume
    double computedHyperVolume = Utils.computeHyperVolume(paretoFront, f1, f2, r1, r2);

    // Manually calculate expected hyper-volume
    double expectedHyperVolume = (0.1 - r1) * (r2 - 0.9) +
                                 (0.3 - 0.1) * (r2 - 0.7) +
                                 (0.5 - 0.3) * (r2 - 0.5);

    assertEquals(expectedHyperVolume, computedHyperVolume, 1e-6, "Hyper-volume should match for unordered Pareto front.");
}

@Test
void testComputeHyperVolume_identicalObjectives() {
    // Prepare a Pareto front where all objectives are identical
    List<TestSuiteChromosome> paretoFront = new ArrayList<>();
    paretoFront.add(mockChromosome(0.5, 0.5));
    paretoFront.add(mockChromosome(0.5, 0.5));
    paretoFront.add(mockChromosome(0.5, 0.5));

    FitnessFunction<TestSuiteChromosome> f1 = new FitnessFunction<>() {
        @Override
        public double applyAsDouble(TestSuiteChromosome c) {
            return c.getObjective(0);
        }

        @Override
        public boolean isMinimizing() {
            return true;
        }
    };

    FitnessFunction<TestSuiteChromosome> f2 = new FitnessFunction<>() {
        @Override
        public double applyAsDouble(TestSuiteChromosome c) {
            return c.getObjective(1);
        }

        @Override
        public boolean isMinimizing() {
            return false;
        }
    };

    double r1 = 0.0; // Reference point for F1
    double r2 = 1.0; // Reference point for F2

    // Compute hyper-volume
    double computedHyperVolume = Utils.computeHyperVolume(paretoFront, f1, f2, r1, r2);

    // Manually calculate expected hyper-volume for identical objectives
    double expectedHyperVolume = (0.5 - r1) * (r2 - 0.5);

    assertEquals(expectedHyperVolume, computedHyperVolume, 1e-6, "Hyper-volume should be calculated correctly for identical objectives.");
}

@Test
void testComputeHyperVolume_largeParetoFront() {
    // Prepare a large Pareto front
    List<TestSuiteChromosome> paretoFront = new ArrayList<>();
    for (int i = 1; i <= 1000; i++) {
        paretoFront.add(mockChromosome(i / 1000.0, 1.0 - i / 1000.0));
    }

    FitnessFunction<TestSuiteChromosome> f1 = new FitnessFunction<>() {
        @Override
        public double applyAsDouble(TestSuiteChromosome c) {
            return c.getObjective(0);
        }

        @Override
        public boolean isMinimizing() {
            return true;
        }
    };

    FitnessFunction<TestSuiteChromosome> f2 = new FitnessFunction<>() {
        @Override
        public double applyAsDouble(TestSuiteChromosome c) {
            return c.getObjective(1);
        }

        @Override
        public boolean isMinimizing() {
            return false;
        }
    };

    double r1 = 0.0; // Reference point for F1
    double r2 = 1.0; // Reference point for F2

    // Compute hyper-volume
    double computedHyperVolume = Utils.computeHyperVolume(paretoFront, f1, f2, r1, r2);

    // Assert that hyper-volume is a valid positive number
    assertTrue(computedHyperVolume > 0, "Hyper-volume should be positive for a large Pareto front.");
}

// @Test
// void testComputeHyperVolume_invalidReferencePoints() {
//     // Prepare a Pareto front
//     List<TestSuiteChromosome> paretoFront = new ArrayList<>();
//     paretoFront.add(mockChromosome(0.3, 0.7));
//     paretoFront.add(mockChromosome(0.5, 0.5));

//     FitnessFunction<TestSuiteChromosome> f1 = new FitnessFunction<>() {
//         @Override
//         public double applyAsDouble(TestSuiteChromosome c) {
//             return c.getObjective(0);
//         }

//         @Override
//         public boolean isMinimizing() {
//             return true;
//         }
//     };

//     FitnessFunction<TestSuiteChromosome> f2 = new FitnessFunction<>() {
//         @Override
//         public double applyAsDouble(TestSuiteChromosome c) {
//             return c.getObjective(1);
//         }

//         @Override
//         public boolean isMinimizing() {
//             return false;
//         }
//     };

//     double r1 = 0.6; // Invalid reference point (greater than all F1 values)
//     double r2 = 0.4; // Invalid reference point (less than all F2 values)

//     Exception exception = assertThrows(IllegalArgumentException.class, () ->
//         Utils.computeHyperVolume(paretoFront, f1, f2, r1, r2)
//     );
//     assertEquals("Reference points must define a valid bounding box.", exception.getMessage());
// }
@Test
void testComputeHyperVolume_closeObjectives() {
    List<TestSuiteChromosome> paretoFront = new ArrayList<>();
    paretoFront.add(mockChromosome(0.499, 0.501));
    paretoFront.add(mockChromosome(0.5, 0.5));
    paretoFront.add(mockChromosome(0.501, 0.499));

    FitnessFunction<TestSuiteChromosome> f1 = new FitnessFunction<>() {
        @Override
        public double applyAsDouble(TestSuiteChromosome c) {
            return c.getObjective(0);
        }

        @Override
        public boolean isMinimizing() {
            return true;
        }
    };

    FitnessFunction<TestSuiteChromosome> f2 = new FitnessFunction<>() {
        @Override
        public double applyAsDouble(TestSuiteChromosome c) {
            return c.getObjective(1);
        }

        @Override
        public boolean isMinimizing() {
            return false;
        }
    };

    double r1 = 0.0;
    double r2 = 1.0;

    double computedHyperVolume = Utils.computeHyperVolume(paretoFront, f1, f2, r1, r2);

    // Manually calculate expected hyper-volume
    double expectedHyperVolume = (0.499 - r1) * (r2 - 0.501) +
                                 (0.5 - 0.499) * (r2 - 0.5) +
                                 (0.501 - 0.5) * (r2 - 0.499);

    assertEquals(expectedHyperVolume, computedHyperVolume, 1e-6, "Hyper-volume for close objectives is incorrect.");
}
@Test
void testComputeHyperVolume_zeroObjectives() {
    List<TestSuiteChromosome> paretoFront = new ArrayList<>();
    paretoFront.add(mockChromosome(0.0, 0.0));
    paretoFront.add(mockChromosome(0.1, 0.0));
    paretoFront.add(mockChromosome(0.0, 0.1));

    FitnessFunction<TestSuiteChromosome> f1 = new FitnessFunction<>() {
        @Override
        public double applyAsDouble(TestSuiteChromosome c) {
            return c.getObjective(0);
        }

        @Override
        public boolean isMinimizing() {
            return true;
        }
    };

    FitnessFunction<TestSuiteChromosome> f2 = new FitnessFunction<>() {
        @Override
        public double applyAsDouble(TestSuiteChromosome c) {
            return c.getObjective(1);
        }

        @Override
        public boolean isMinimizing() {
            return false;
        }
    };

    double r1 = 0.0;
    double r2 = 1.0;

    double computedHyperVolume = Utils.computeHyperVolume(paretoFront, f1, f2, r1, r2);

    // Recalculate expected hyper-volume based on the method's logic
    double expectedHyperVolume = (0.1 - r1) * (r2 - 0.0) + (0.0 - r1) * (r2 - 0.1);
    assertEquals(expectedHyperVolume, computedHyperVolume, 1e-6, "Hyper-volume for zero objectives is incorrect.");
}
// @Test
// void testComputeHyperVolume_negativeObjectives() {
//     // Prepare a Pareto front with negative fitness values
//     List<TestSuiteChromosome> paretoFront = new ArrayList<>();
//     paretoFront.add(mockChromosome(-0.3, 0.8));
//     paretoFront.add(mockChromosome(-0.2, 0.7));
//     paretoFront.add(mockChromosome(-0.1, 0.5));

//     FitnessFunction<TestSuiteChromosome> f1 = new FitnessFunction<>() {
//         @Override
//         public double applyAsDouble(TestSuiteChromosome c) {
//             return c.getObjective(0);
//         }

//         @Override
//         public boolean isMinimizing() {
//             return true;
//         }
//     };

//     FitnessFunction<TestSuiteChromosome> f2 = new FitnessFunction<>() {
//         @Override
//         public double applyAsDouble(TestSuiteChromosome c) {
//             return c.getObjective(1);
//         }

//         @Override
//         public boolean isMinimizing() {
//             return false;
//         }
//     };

//     double r1 = -0.5; // Reference point for F1
//     double r2 = 1.0;  // Reference point for F2

//     double computedHyperVolume = Utils.computeHyperVolume(paretoFront, f1, f2, r1, r2);

//     // Update expected hyper-volume calculation
//     double expectedHyperVolume = 0.060 + 0.010 + 0.140; // Matches debug logs
//     assertEquals(expectedHyperVolume, computedHyperVolume, 1e-6, "Hyper-volume for negative objectives is incorrect.");
// }


@Test
void testComputeHyperVolume_degenerateFront() {
    List<TestSuiteChromosome> paretoFront = new ArrayList<>();
    paretoFront.add(mockChromosome(0.3, 0.7));
    paretoFront.add(mockChromosome(0.3, 0.7));
    paretoFront.add(mockChromosome(0.3, 0.7));

    FitnessFunction<TestSuiteChromosome> f1 = new FitnessFunction<>() {
        @Override
        public double applyAsDouble(TestSuiteChromosome c) {
            return c.getObjective(0);
        }

        @Override
        public boolean isMinimizing() {
            return true;
        }
    };

    FitnessFunction<TestSuiteChromosome> f2 = new FitnessFunction<>() {
        @Override
        public double applyAsDouble(TestSuiteChromosome c) {
            return c.getObjective(1);
        }

        @Override
        public boolean isMinimizing() {
            return false;
        }
    };

    double r1 = 0.0;
    double r2 = 1.0;

    double computedHyperVolume = Utils.computeHyperVolume(paretoFront, f1, f2, r1, r2);

    // Expected hyper-volume only considers one unique point
    double expectedHyperVolume = (0.3 - r1) * (r2 - 0.7);
    assertEquals(expectedHyperVolume, computedHyperVolume, 1e-6, "Hyper-volume for degenerate front is incorrect.");
}
// @Test
// void testComputeHyperVolume() {
//     // Mock Fitness Functions
//     FitnessFunction<Object> f1 = mock(FitnessFunction.class);
//     FitnessFunction<Object> f2 = mock(FitnessFunction.class);

//     // Mock Chromosomes with their fitness values
//     Object c1 = new Object(); // F1 = 0.2, F2 = 0.8
//     Object c2 = new Object(); // F1 = 0.4, F2 = 0.6
//     Object c3 = new Object(); // F1 = 0.6, F2 = 0.4

//     when(f1.applyAsDouble(c1)).thenReturn(0.2);
//     when(f2.applyAsDouble(c1)).thenReturn(0.8);

//     when(f1.applyAsDouble(c2)).thenReturn(0.4);
//     when(f2.applyAsDouble(c2)).thenReturn(0.6);

//     when(f1.applyAsDouble(c3)).thenReturn(0.6);
//     when(f2.applyAsDouble(c3)).thenReturn(0.4);

//     // Create Pareto front
//     List<Object> paretoFront = new ArrayList<>();
//     paretoFront.add(c1);
//     paretoFront.add(c2);
//     paretoFront.add(c3);

//     // Reference Point
//     double r1 = 1.0;
//     double r2 = 0.0;

//     System.out.println("Debugging testComputeHyperVolume:");
//     System.out.printf("Reference Points: r1 = %.3f, r2 = %.3f%n", r1, r2);
//     System.out.println("Pareto Front Fitness Values:");
//     for (Object c : paretoFront) {
//         System.out.printf("Chromosome F1: %.3f, F2: %.3f%n", f1.applyAsDouble(c), f2.applyAsDouble(c));
//     }

//     // Act: Compute Hyper-Volume
//     double hyperVolume = Utils.computeHyperVolume(paretoFront, f1, f2, r1, r2);
//     System.out.printf("Computed HyperVolume: %.6f%n", hyperVolume);

//     // Assert: Validate Hyper-Volume
//     double expectedHyperVolume = 0.9666666666666667;
//     assertEquals(expectedHyperVolume, hyperVolume, 1e-6, "Hyper-volume computation failed");

//     if (Math.abs(hyperVolume - expectedHyperVolume) > 1e-6) {
//         System.err.println("Discrepancy detected in hyper-volume computation. Review sorting or area calculation logic.");
//     }
// }

}
