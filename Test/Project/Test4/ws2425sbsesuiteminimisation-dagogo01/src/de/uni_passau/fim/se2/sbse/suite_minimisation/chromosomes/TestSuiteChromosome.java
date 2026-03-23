package de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes;

import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TestSuiteChromosome extends Chromosome<TestSuiteChromosome> {

    private final boolean[] testCases;
    private final int totalTestCases;

    public TestSuiteChromosome(boolean[] testCases, Mutation<TestSuiteChromosome> mutation, 
                               Crossover<TestSuiteChromosome> crossover) {
        super(mutation, crossover);
        if (testCases == null || testCases.length == 0) {
            throw new IllegalArgumentException("Test cases array cannot be null or empty.");
        }
        this.testCases = testCases;
        this.totalTestCases = testCases.length;
    }

    /**
     * Copy constructor for TestSuiteChromosome.
     *
     * @param other the chromosome to copy
     */
    public TestSuiteChromosome(TestSuiteChromosome other) {
        super(other.getMutation(), other.getCrossover());
        this.testCases = Arrays.copyOf(other.testCases, other.testCases.length);
        this.totalTestCases = other.totalTestCases;
    }

    /**
     * Generates a random chromosome (subset of test cases).
     */
    public static TestSuiteChromosome randomChromosome(int totalTestCases, 
                                                       Mutation<TestSuiteChromosome> mutation, 
                                                       Crossover<TestSuiteChromosome> crossover) {
        boolean[] testCases = new boolean[totalTestCases];
        Arrays.fill(testCases, false);
        int selectedCount = 1 + (int) (Math.random() * totalTestCases); // At least one test case

        for (int i = 0; i < selectedCount; i++) {
            int randomIndex;
            do {
                randomIndex = (int) (Math.random() * totalTestCases);
            } while (testCases[randomIndex]); // Ensure no duplicates
            testCases[randomIndex] = true;
        }
        return new TestSuiteChromosome(testCases, mutation, crossover);
    }

    /**
     * Returns the size fitness of the chromosome.
     * Size fitness = fraction of selected test cases.
     *
     * @return size fitness (normalized)
     */
    public double getSizeFitness() {
        return (double) getSelectedTestCases().length / totalTestCases;
    }

    /**
     * Calculates the coverage fitness of the chromosome.
     *
     * @param coverageMatrix the boolean coverage matrix where:
     *                       - Rows represent test cases.
     *                       - Columns represent lines of code.
     * @return coverage fitness (normalized)
     */
    public double getCoverageFitness(boolean[][] coverageMatrix) {
        Set<Integer> coveredLines = new HashSet<>();

        for (int i = 0; i < testCases.length; i++) {
            if (testCases[i]) {
                for (int j = 0; j < coverageMatrix[i].length; j++) {
                    if (coverageMatrix[i][j]) {
                        coveredLines.add(j);
                    }
                }
            }
        }

        int totalLines = coverageMatrix[0].length; // Assumes all test cases cover the same lines
        return (double) coveredLines.size() / totalLines;
    }

    /**
     * Gets the indices of selected test cases.
     *
     * @return an array of indices of selected test cases
     */
    public int[] getSelectedTestCases() {
        return Arrays.stream(testCases)
                .map(b -> b ? 1 : 0)
                .toArray();
    }

    /**
     * Sets selected test cases using a list of indices.
     *
     * @param selectedTestCases the indices of selected test cases
     */
    public void setSelectedTestCases(int[] selectedTestCases) {
        Arrays.fill(testCases, false);
        for (int index : selectedTestCases) {
            testCases[index] = true;
        }
    }

    /**
     * Determines if this chromosome dominates another chromosome.
     *
     * @param other      the other chromosome
     * @param sizeFF     the size fitness function
     * @param coverageFF the coverage fitness function
     * @return true if this chromosome dominates the other chromosome
     */
    public boolean dominates(TestSuiteChromosome other, FitnessFunction<TestSuiteChromosome> sizeFF, 
                             FitnessFunction<TestSuiteChromosome> coverageFF) {
        double thisSizeFitness = sizeFF.applyAsDouble(this);
        double thisCoverageFitness = coverageFF.applyAsDouble(this);

        double otherSizeFitness = sizeFF.applyAsDouble(other);
        double otherCoverageFitness = coverageFF.applyAsDouble(other);

        boolean betterOrEqualInSize = thisSizeFitness <= otherSizeFitness;
        boolean betterOrEqualInCoverage = thisCoverageFitness >= otherCoverageFitness;
        boolean strictlyBetter = (thisSizeFitness < otherSizeFitness || thisCoverageFitness > otherCoverageFitness);

        return betterOrEqualInSize && betterOrEqualInCoverage && strictlyBetter;
    }

    /**
     * Creates a copy of this chromosome.
     *
     * @return a copy of this chromosome
     */
    @Override
    public TestSuiteChromosome copy() {
        return new TestSuiteChromosome(this);
    }

    /**
     * Returns the total number of test cases.
     *
     * @return the total number of test cases
     */
    public int getTotalTestCases() {
        return totalTestCases;
    }

    @Override
    public TestSuiteChromosome self() {
        return this;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof TestSuiteChromosome)) return false;
        TestSuiteChromosome that = (TestSuiteChromosome) other;
        return Arrays.equals(testCases, that.testCases);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(testCases);
    }

    @Override
    public String toString() {
        return "TestSuiteChromosome{" +
                "testCases=" + Arrays.toString(testCases) +
                ", sizeFitness=" + getSizeFitness() +
                '}';
    }
}
