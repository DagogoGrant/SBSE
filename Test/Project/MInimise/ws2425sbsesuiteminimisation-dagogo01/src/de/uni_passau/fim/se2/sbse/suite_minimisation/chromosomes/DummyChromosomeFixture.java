package de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes;


import java.util.Objects;

/**
 * A dummy implementation of Chromosome for testing purposes.
 */
public class DummyChromosomeFixture extends Chromosome<DummyChromosomeFixture> {

    private int suiteSize;
    private double fitness;

    // Constructor(s)
    public DummyChromosomeFixture(int suiteSize, double fitness) {
        this.suiteSize = suiteSize;
        this.fitness = fitness;
    }

    // Default constructor (if needed)
    public DummyChromosomeFixture() {
        this(0, 0.0); // Defaults for suiteSize and fitness
    }

    // Getters and Setters
    public int getSuiteSize() {
        return this.suiteSize; // Assuming suiteSize is a field in DummyChromosomeFixture.
    }
    

    public void setSuiteSize(int suiteSize) {
        this.suiteSize = suiteSize;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    // Implement the self() method
    @Override
    public DummyChromosomeFixture self() {
        return this;
    }
    

    // Other methods
    @Override
    public DummyChromosomeFixture copy() {
        return new DummyChromosomeFixture(suiteSize, fitness);
    }

    @Override
public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    DummyChromosomeFixture that = (DummyChromosomeFixture) obj;
    return suiteSize == that.suiteSize;
}

@Override
public int hashCode() {
    return Objects.hash(suiteSize);
}

}
