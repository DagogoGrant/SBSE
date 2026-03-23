package de.uni_passau.fim.se2.sbse.suite_minimisation.selection;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.Chromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Pair;

import java.util.Objects;

/**
 * A simple fixture for testing purposes.
 */
public class DummyChromosomeFixture extends Chromosome<DummyChromosomeFixture> {

    private final double sizeFitness;
    private final double coverageFitness;

    public DummyChromosomeFixture(double sizeFitness, double coverageFitness) {
        super(dummyMutation(), dummyCrossover()); // Provide dummy mutation and crossover
        this.sizeFitness = sizeFitness;
        this.coverageFitness = coverageFitness;
    }

    /**
     * Returns the combined fitness value.
     *
     * @return Combined fitness value (sum of sizeFitness and coverageFitness)
     */
    public double getFitness() {
        double fitness = sizeFitness + coverageFitness;
        System.out.printf("Fitness calculated: %.2f (size=%.2f, coverage=%.2f)\n", fitness, sizeFitness, coverageFitness);
        return fitness;
    }

    public double getSizeFitness() {
        return sizeFitness;
    }

    public double getCoverageFitness() {
        return coverageFitness;
    }

    @Override
    public DummyChromosomeFixture copy() {
        return new DummyChromosomeFixture(this.sizeFitness, this.coverageFitness);
    }

    @Override
    public DummyChromosomeFixture self() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DummyChromosomeFixture)) return false;
        DummyChromosomeFixture that = (DummyChromosomeFixture) o;
        return Double.compare(that.sizeFitness, sizeFitness) == 0 &&
               Double.compare(that.coverageFitness, coverageFitness) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sizeFitness, coverageFitness);
    }

    @Override
    public String toString() {
        return String.format("DummyChromosomeFixture(%.2f, %.2f)", sizeFitness, coverageFitness);
    }

    /**
     * Provides a dummy Mutation operator for testing purposes.
     */
    public static Mutation<DummyChromosomeFixture> dummyMutation() {
        return chromosome -> {
            double newSizeFitness = chromosome.sizeFitness + 1;
            double newCoverageFitness = chromosome.coverageFitness + 1;
            return new DummyChromosomeFixture(newSizeFitness, newCoverageFitness);
        };
    }
    
    
    /**
     * Provides a dummy Crossover operator for testing purposes.
     */
    private static Crossover<DummyChromosomeFixture> dummyCrossover() {
        // Define crossover logic here
        return (parent1, parent2) -> new Pair<>(parent1, parent2);
    }
}
