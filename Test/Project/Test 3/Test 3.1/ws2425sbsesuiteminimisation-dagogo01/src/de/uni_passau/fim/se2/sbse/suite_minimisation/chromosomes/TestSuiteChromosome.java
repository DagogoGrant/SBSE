package de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes;

import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Randomness;

import java.util.*;

/**
 * TestSuiteChromosome represents a subset of test cases for test suite minimisation.
 * Each chromosome contains indices representing the selected test cases.
 */
public class TestSuiteChromosome extends Chromosome<TestSuiteChromosome> {

    private List<Integer> testCases; // Selected test cases
    private final int totalTestCases; // Total available test cases

    /**
     * Constructs a TestSuiteChromosome with given mutation, crossover, and test case count.
     *
     * @param mutation       the mutation operator
     * @param crossover      the crossover operator
     * @param totalTestCases the total number of test cases in the suite
     * @return true if this chromosome dominates the other chromosome.
     */
    public boolean dominates(TestSuiteChromosome other,
                             FitnessFunction<TestSuiteChromosome> sizeFF,
                             FitnessFunction<TestSuiteChromosome> coverageFF) {
        // Compute fitness values
        double thisSize = sizeFF.applyAsDouble(this);
        double thisCoverage = coverageFF.applyAsDouble(this);

        double otherSize = sizeFF.applyAsDouble(other);
        double otherCoverage = coverageFF.applyAsDouble(other);

        // Dominance conditions
        boolean betterOrEqualInSize = thisSize <= otherSize;
        boolean betterOrEqualInCoverage = thisCoverage >= otherCoverage;
        boolean strictlyBetter = (thisSize < otherSize) || (thisCoverage > otherCoverage);

        return betterOrEqualInSize && betterOrEqualInCoverage && strictlyBetter;
    }
    public TestSuiteChromosome(Mutation<TestSuiteChromosome> mutation,
                               Crossover<TestSuiteChromosome> crossover,
                               int totalTestCases) {
        super(mutation, crossover);
        this.totalTestCases = totalTestCases;
        this.testCases = new ArrayList<>();
        generateRandomChromosome();
    }
    public int getTotalTestCases() {
        return totalTestCases;
    }

    /**
     * Copy constructor for TestSuiteChromosome.
     *
     * @param other the chromosome to copy
     */
    public TestSuiteChromosome(TestSuiteChromosome other) {
        super(other);
        this.testCases = new ArrayList<>(other.testCases);
        this.totalTestCases = other.totalTestCases;
    }

    /**
     * Generates a random chromosome (subset of test cases).
     */
    private void generateRandomChromosome() {
        Random random = Randomness.random();
        int numTests = 1 + random.nextInt(totalTestCases); // Ensure at least one test case
        Set<Integer> uniqueTestCases = new HashSet<>();

        while (uniqueTestCases.size() < numTests) {
            int testCase = random.nextInt(totalTestCases); // Random test case index
            uniqueTestCases.add(testCase);
        }
        testCases.addAll(uniqueTestCases);
    }

    /**
     * Calculates the size fitness of the chromosome.
     * Size fitness = fraction of selected test cases.
     *
     * @return size fitness (normalized)
     */
    public double getSizeFitness() {
        return (double) testCases.size() / totalTestCases;
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

        for (int testCase : testCases) {
            for (int line = 0; line < coverageMatrix[testCase].length; line++) {
                if (coverageMatrix[testCase][line]) {
                    coveredLines.add(line);
                }
            }
        }

        int totalLines = coverageMatrix[0].length; // Assumes all test cases cover the same lines
        return (double) coveredLines.size() / totalLines;
    }

    /**
     * Returns the list of test cases in this chromosome.
     *
     * @return selected test cases
     */
    public List<Integer> getTestCases() {
        return Collections.unmodifiableList(testCases);
    }

    /**
     * Replaces the list of test cases with a new list.
     *
     * @param newTestCases the new list of test cases
     */
    public void setTestCases(List<Integer> newTestCases) {
        this.testCases = new ArrayList<>(newTestCases);
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
     * Returns a reference to the self-type of this chromosome.
     *
     * @return this instance
     */
    @Override
    public TestSuiteChromosome self() {
        return this;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof TestSuiteChromosome)) return false;
        TestSuiteChromosome that = (TestSuiteChromosome) other;
        return Objects.equals(testCases, that.testCases);
    }

    @Override
    public int hashCode() {
        return Objects.hash(testCases);
    }

    @Override
    public String toString() {
        return "TestSuiteChromosome{" +
                "testCases=" + testCases +
                ", sizeFitness=" + getSizeFitness() +
                '}';
    }
    
 
}
