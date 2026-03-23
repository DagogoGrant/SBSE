package de.uni_passau.fim.se2.sbse.suite_generation.chromosomes;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;
import de.uni_passau.fim.se2.sbse.suite_generation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_generation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.BranchTracer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A concrete implementation of {@link Chromosome} that represents a test case as a sequence of statements.
 */
public class TestChromosome extends Chromosome<TestChromosome> {

    private final List<Statement> statements;
    private double fitness; // Fitness value for this chromosome

    /**
     * Constructs a new test chromosome with the given mutation and crossover operators.
     *
     * @param mutation  the mutation operator
     * @param crossover the crossover operator
     */
    public TestChromosome(final Mutation<TestChromosome> mutation, final Crossover<TestChromosome> crossover) {
        super(mutation, crossover);
        this.statements = new ArrayList<>();
        this.fitness = 0.0; // Default fitness value
    }

    /**
     * Constructs a new test chromosome by copying the given chromosome.
     *
     * @param other the chromosome to copy
     */
    public TestChromosome(final TestChromosome other) {
        super(other);
        this.statements = new ArrayList<>(other.statements);
        this.fitness = other.fitness;
    }

    /**
     * Adds a statement to this chromosome.
     *
     * @param statement the statement to add
     */
    public void addStatement(final Statement statement) {
        this.statements.add(statement);
    }

    /**
     * Removes a statement at a specific index.
     *
     * @param index the index of the statement to remove
     */
    public void removeStatement(int index) {
        if (index >= 0 && index < statements.size()) {
            statements.remove(index);
        } else {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }
    }

    /**
     * Modifies a statement at a specific index.
     *
     * @param index     the index of the statement to modify
     * @param statement the new statement to replace the old one
     */
    public void modifyStatement(int index, Statement statement) {
        if (index >= 0 && index < statements.size()) {
            statements.set(index, statement);
        } else {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TestChromosome copy() {
        return new TestChromosome(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (!(other instanceof TestChromosome)) return false;
        TestChromosome that = (TestChromosome) other;
        return Objects.equals(statements, that.statements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(statements);
    }

    /**
     * Executes the test case represented by this chromosome and collects branch trace data.
     *
     * @return a map of branch IDs to branch distances
     */
    @Override
    public Map<Integer, Double> call() {
        BranchTracer tracer = BranchTracer.getInstance();
        tracer.clear(); // Clear previous branch traces

        for (Statement statement : statements) {
            try {
                statement.run();
            } catch (Exception e) {
                // Log and ignore runtime exceptions to ensure robustness
                e.printStackTrace();
            }
        }

        Map<Integer, Double> distances = tracer.getDistances();
        setFitness(calculateFitness(distances));
        return distances;
    }

    /**
     * Calculates the fitness of the chromosome based on branch coverage.
     *
     * @param distances the map of branch distances
     * @return the calculated fitness value
     */
    private double calculateFitness(Map<Integer, Double> distances) {
        double fitness = 0.0;
        for (double distance : distances.values()) {
            fitness += 1.0 / (1.0 + distance); // Higher fitness for smaller distances
        }
        return fitness;
    }

    /**
     * Retrieves the fitness value for this chromosome.
     *
     * @return the fitness value
     */
    public double getFitness() {
        return fitness;
    }

    /**
     * Sets the fitness value for this chromosome.
     *
     * @param fitness the fitness value to set
     */
    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Statement> getStatements() {
        return statements;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TestChromosome self() {
        return this;
    }
}
