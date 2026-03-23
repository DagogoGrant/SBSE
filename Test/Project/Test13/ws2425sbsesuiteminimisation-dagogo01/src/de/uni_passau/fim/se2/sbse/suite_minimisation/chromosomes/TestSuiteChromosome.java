package de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes;

import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.CoverageTracker;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class TestSuiteChromosome extends Chromosome<TestSuiteChromosome> {

    private final boolean[] testCases; // Test case selection
    private final CoverageTracker coverageTracker;
    private double sizeFitness;
    private final double[] objectives; // Array to store multiple objectives
    private double crowdingDistance;

    /**
     * Constructs a new TestSuiteChromosome.
     *
     * @param testCases       The boolean array representing selected test cases.
     * @param coverageTracker The tracker for coverage metrics.
     * @param mutation        The mutation operator for this chromosome.
     * @param crossover       The crossover operator for this chromosome.
     */
    public TestSuiteChromosome(boolean[] testCases, CoverageTracker coverageTracker,
                                Mutation<TestSuiteChromosome> mutation,
                                Crossover<TestSuiteChromosome> crossover) {
        super(mutation, crossover);
        this.testCases = Objects.requireNonNull(testCases, "Test cases cannot be null");
        this.coverageTracker = Objects.requireNonNull(coverageTracker, "CoverageTracker cannot be null");
        this.objectives = new double[2]; // Assuming two objectives: size and coverage
        Arrays.fill(this.objectives, 0.0);
    }

    @Override
    public TestSuiteChromosome copy() {
        return new TestSuiteChromosome(
            Arrays.copyOf(testCases, testCases.length),
            coverageTracker,
            getMutation(),
            getCrossover()
        );
    }

    @Override
    public TestSuiteChromosome self() {
        return this;
    }

    public double getObjective(int index) {
        if (index < 0 || index >= objectives.length) {
            throw new IndexOutOfBoundsException("Invalid objective index: " + index);
        }
        return objectives[index];
    }

    public void setObjective(int index, double value) {
        if (index < 0 || index >= objectives.length) {
            throw new IndexOutOfBoundsException("Invalid objective index: " + index);
        }
        objectives[index] = value;
    }

    public void setSizeFitness(double sizeFitness) {
        this.sizeFitness = sizeFitness;
        setObjective(0, sizeFitness); // Set size fitness as the first objective
    }

    public double getSizeFitness() {
        return this.sizeFitness;
    }

    public double getCoverageFitness(boolean[][] coverageMatrix) {
        Set<Integer> coveredLines = new HashSet<>();
        for (int i = 0; i < testCases.length; i++) {
            if (testCases[i]) { // Only consider selected test cases
                for (int line = 0; line < coverageMatrix[i].length; line++) {
                    if (coverageMatrix[i][line]) {
                        coveredLines.add(line);
                    }
                }
            }
        }
        int totalLines = coverageMatrix[0].length; // Assumes all test cases cover the same lines
        return (double) coveredLines.size() / totalLines;
    }

    public double computeCoverageFitness() throws Exception {
        boolean[][] coverageMatrix = coverageTracker.getCoverageMatrix();
    
        if (coverageMatrix == null || coverageMatrix.length == 0) {
            throw new IllegalStateException("Coverage matrix is null or empty.");
        }
        
    
        Set<Integer> coveredLines = new HashSet<>();
        for (int i = 0; i < testCases.length; i++) {
            if (testCases[i]) { // Only consider selected test cases
                for (int line = 0; line < coverageMatrix[i].length; line++) {
                    if (coverageMatrix[i][line]) {
                        coveredLines.add(line);
                    }
                }
            }
        }
    
        int totalLines = coverageMatrix[0].length; // Total lines in source code
        return Math.min((double) coveredLines.size() / totalLines, 1.0); // Cap fitness at 1.0
    }
    

    public double computeSizeFitness() {
        int selectedCount = 0;
        for (boolean testCase : testCases) {
            if (testCase) selectedCount++;
        }
        double sizeFitness = (double) selectedCount / testCases.length;
        setSizeFitness(sizeFitness); // Update the size fitness objective
        return sizeFitness;
    }

    public int getTotalSize() {
        return testCases.length;
    }

    public int getSelectedSize() {
        int selectedCount = 0;
        for (boolean testCase : testCases) {
            if (testCase) selectedCount++;
        }
        return selectedCount;
    }

    public boolean[] getTestCases() {
        return testCases;
    }

    public CoverageTracker getCoverageTracker() {
        return coverageTracker;
    }

    public double getCrowdingDistance() {
        return crowdingDistance;
    }

    public void setCrowdingDistance(double crowdingDistance) {
        this.crowdingDistance = crowdingDistance;
    }

    public int getNumberOfObjectives() {
        return this.objectives.length;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof TestSuiteChromosome)) return false;
        TestSuiteChromosome o = (TestSuiteChromosome) other;
        return Arrays.equals(testCases, o.testCases) &&
               Double.compare(sizeFitness, o.sizeFitness) == 0 &&
               Objects.equals(coverageTracker, o.coverageTracker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(testCases), coverageTracker, sizeFitness);
    }
    @Override
public TestSuiteChromosome clone() {
    try {
        return (TestSuiteChromosome) super.clone(); 
    } catch (CloneNotSupportedException e) {
        throw new AssertionError("Clone should be supported", e);
    }
}

}
