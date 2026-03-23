package de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FitnessFunctionsTest {

    @Test
    void testSizeFitnessFunction() {
        int totalTestCases = 5;
        MinimizingFitnessFunction<TestSuiteChromosome> sizeFitnessFunction = new SizeFitnessFunction(totalTestCases);

        // Test case 1: Only one test case selected (minimized selection)
        boolean[] genes1 = {true, false, false, false, false};
        TestSuiteChromosome chromosome1 = new TestSuiteChromosome(genes1);
        assertEquals(0.2, sizeFitnessFunction.applyAsDouble(chromosome1), 0.01);

        // Test case 2: Reduced test cases while maintaining partial coverage
        boolean[] genes2 = {true, false, true, false, false};
        TestSuiteChromosome chromosome2 = new TestSuiteChromosome(genes2);
        assertEquals(0.4, sizeFitnessFunction.applyAsDouble(chromosome2), 0.01);

        // Test case 3: All test cases selected (maximal selection)
        boolean[] genes3 = {true, true, true, true, true};
        TestSuiteChromosome chromosome3 = new TestSuiteChromosome(genes3);
        assertEquals(1.0, sizeFitnessFunction.applyAsDouble(chromosome3), 0.01);
    }

    @Test
    void testCoverageFitnessFunction() {
        boolean[][] coverageMatrix = {
                {true, false, true},  // Test case 0
                {false, true, false}, // Test case 1
                {true, true, false},  // Test case 2
                {false, false, true}  // Test case 3
        };
        MaximizingFitnessFunction<TestSuiteChromosome> coverageFitnessFunction = new CoverageFitnessFunction(coverageMatrix);

        // Test case 1: Select test cases 0 and 2 (minimal coverage)
        boolean[] genes1 = {true, false, true, false};
        TestSuiteChromosome chromosome1 = new TestSuiteChromosome(genes1);
        assertEquals(1.0, coverageFitnessFunction.applyAsDouble(chromosome1), 0.01); // All lines covered

        // Test case 2: Select all test cases
        boolean[] genes2 = {true, true, true, true};
        TestSuiteChromosome chromosome2 = new TestSuiteChromosome(genes2);
        assertEquals(1.0, coverageFitnessFunction.applyAsDouble(chromosome2), 0.01); // All lines covered

        // Test case 3: Select redundant test cases (inefficient selection)
        boolean[] genes3 = {true, true, true, false};
        TestSuiteChromosome chromosome3 = new TestSuiteChromosome(genes3);
        assertEquals(1.0, coverageFitnessFunction.applyAsDouble(chromosome3), 0.01); // All lines covered
    }

    @Test
    void testReduceTestCasesWithCoverage() {
        boolean[][] coverageMatrix = {
                {true, false, true},  // Test case 0
                {false, true, false}, // Test case 1
                {true, true, false},  // Test case 2
                {false, false, true}  // Test case 3
        };
        MaximizingFitnessFunction<TestSuiteChromosome> coverageFitnessFunction = new CoverageFitnessFunction(coverageMatrix);

        // Test case: Minimal set of test cases covering all lines
        boolean[] minimalGenes = {true, false, true, false};
        TestSuiteChromosome minimalChromosome = new TestSuiteChromosome(minimalGenes);
        assertEquals(1.0, coverageFitnessFunction.applyAsDouble(minimalChromosome), 0.01);

        // Test case: Redundant test cases (inefficient selection)
        boolean[] redundantGenes = {true, true, true, false};
        TestSuiteChromosome redundantChromosome = new TestSuiteChromosome(redundantGenes);
        assertEquals(1.0, coverageFitnessFunction.applyAsDouble(redundantChromosome), 0.01);

        // Verify that minimalChromosome uses fewer test cases
        assertTrue(countTrueGenes(minimalGenes) < countTrueGenes(redundantGenes),
                "Minimal test cases should use fewer genes while maintaining coverage.");
    }

    private int countTrueGenes(boolean[] genes) {
        int count = 0;
        for (boolean gene : genes) {
            if (gene) count++;
        }
        return count;
    }
}
