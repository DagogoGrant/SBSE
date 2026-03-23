package de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes;

import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A chromosome representing a test suite for the test suite minimisation problem.
 */
public class TestSuiteChromosome extends Chromosome<TestSuiteChromosome> {

    private List<Integer> testCases; // List of test case indices
    private final int totalTestCases; // Total available test cases

    /**
     * Constructs a new TestSuiteChromosome with specified mutation and crossover operators.
     *
     * @param mutation       mutation operator
     * @param crossover      crossover operator
     * @param totalTestCases total number of available test cases
     */
    public TestSuiteChromosome(Mutation<TestSuiteChromosome> mutation,
                                Crossover<TestSuiteChromosome> crossover,
                                int totalTestCases) {
        super(mutation, crossover);
        this.totalTestCases = totalTestCases;
        this.testCases = new ArrayList<>();
    }
    public TestSuiteChromosome(List<Integer> testCases, int totalTestCases) {
        if (testCases.size() > totalTestCases) {
            throw new IllegalArgumentException("Test cases exceed total available test cases.");
        }
        this.testCases = testCases;
        this.totalTestCases = totalTestCases;
    }
    public void setTestCases(List<Integer> testCases) {
        for (int testCase : testCases) {
            if (testCase < 0 || testCase >= totalTestCases) {
                throw new IllegalArgumentException("Test case index out of bounds: " + testCase);
            }
        }
        this.testCases = new ArrayList<>(testCases);
    }
    
    public void addTestCase(int testCaseIndex) {
        if (testCaseIndex < 0 || testCaseIndex >= totalTestCases) {
            throw new IllegalArgumentException("Test case index out of bounds: " + testCaseIndex);
        }
        if (!testCases.contains(testCaseIndex)) {
            testCases.add(testCaseIndex);
        }
    }
    

    /**
     * Constructs a new TestSuiteChromosome with the provided list of test cases.
     *
     * @param testCases list of test cases
     */
    public TestSuiteChromosome(List<Integer> testCases) {
        super(null, null); // No mutation or crossover provided
        this.testCases = new ArrayList<>(testCases);
        this.totalTestCases = 0; // Unknown total test cases in this context
    }

    /**
     * Constructs a copy of another TestSuiteChromosome.
     *
     * @param other the chromosome to copy
     */
    public TestSuiteChromosome(TestSuiteChromosome other) {
        super(other);
        this.totalTestCases = other.totalTestCases;
        this.testCases = new ArrayList<>(other.testCases);
    }

    /**
     * Removes a test case from the test suite.
     *
     * @param testCaseIndex index of the test case to remove
     */
    public void removeTestCase(int testCaseIndex) {
        testCases.remove((Integer) testCaseIndex);
    }

    /**
     * Returns the test cases in this chromosome.
     *
     * @return list of test cases
     */
    public List<Integer> getTestCases() {
        return new ArrayList<>(testCases);
    }

    /**
     * Returns the total number of available test cases.
     *
     * @return total number of available test cases
     */
    public int getTotalTestCases() {
        return totalTestCases;
    }

    /**
     * Returns a reference to the current instance (self-type).
     *
     * @return this instance
     */
    @Override
    public TestSuiteChromosome self() {
        return this;
    }

    /**
     * Creates a copy of this chromosome.
     *
     * @return a new instance with the same properties
     */
    @Override
    public TestSuiteChromosome copy() {
        return new TestSuiteChromosome(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TestSuiteChromosome)) return false;
        TestSuiteChromosome other = (TestSuiteChromosome) obj;
        return testCases.equals(other.testCases);
    }

    @Override
    public int hashCode() {
        return Objects.hash(testCases);
    }
    @Override
    public String toString() {
        return "TestSuiteChromosome{" +
                "testCases=" + testCases +
                '}';
    }
    
}
