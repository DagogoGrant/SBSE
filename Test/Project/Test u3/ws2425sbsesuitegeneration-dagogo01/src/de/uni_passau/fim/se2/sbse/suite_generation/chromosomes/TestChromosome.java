package de.uni_passau.fim.se2.sbse.suite_generation.chromosomes;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;
import de.uni_passau.fim.se2.sbse.suite_generation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.BranchTracer;
import de.uni_passau.fim.se2.sbse.suite_generation.mutation.Mutation;

import java.util.*;

/**
 * A refined implementation of {@link Chromosome} representing a test case as a sequence of statements.
 * Optimized for MOSA and Random Search algorithms.
 */
public class TestChromosome extends Chromosome<TestChromosome> {

    private final List<Statement> statements;
    private double fitness; // Fitness value for this chromosome

    /**
     * Constructs a new ConcreteChromosome with the given mutation and crossover operators.
     *
     * @param mutation  the mutation operator
     * @param crossover the crossover operator
     */
    public TestChromosome(Mutation<TestChromosome> mutation, Crossover<TestChromosome> crossover) {
        super(mutation, crossover);
        this.statements = new ArrayList<>();
        this.fitness = 0.0; // Default fitness value
    }

    /**
     * Constructs a new ConcreteChromosome by copying an existing chromosome.
     *
     * @param other the chromosome to copy
     */
    public TestChromosome(TestChromosome other) {
        super(other);
        this.statements = new ArrayList<>(other.statements);
        this.fitness = other.fitness;
    }
    /**
     * Constructs a TestChromosome from a list of statements.
     *
     * @param statements the statements for this chromosome
     */
    public TestChromosome(List<Statement> statements) {
        super(); // Use identity mutation and crossover for simplicity
        this.statements = new ArrayList<>(statements);
        this.fitness = 0.0; // Default fitness value
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public TestChromosome copy() {
        return new TestChromosome(this);
    }

    /**
     * Executes the test case represented by this chromosome and collects branch trace data.
     *
     * @return a map of branch IDs to branch distances
     */
    @Override
    public Map<Integer, Double> call() {
        BranchTracer branchTracer = BranchTracer.getInstance();
        branchTracer.clear(); // Clear previous branch traces

        for (Statement statement : statements) {
            try {
                statement.run(); // Execute each statement
            } catch (Exception e) {
                // Handle execution errors gracefully to avoid disrupting the algorithm
                e.printStackTrace();
            }
        }

        Map<Integer, Double> distances = branchTracer.getDistances();
        setFitness(calculateFitnessFromBranchCoverage(distances));
        return distances;
    }

    /**
     * Calculates the fitness based on branch coverage.
     *
     * @param distances the map of branch distances
     * @return the calculated fitness value
     */
    private double calculateFitnessFromBranchCoverage(Map<Integer, Double> distances) {
        double fitness = 0.0;
        for (Double distance : distances.values()) {
            fitness += 1.0 / (1.0 + distance); // Higher coverage reduces distance, increases fitness
        }
        return fitness;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        TestChromosome that = (TestChromosome) other;
        return statements.equals(that.statements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(statements);
    }

    /**
     * Adds a statement to this chromosome.
     *
     * @param statement the statement to add
     */
    public void addStatement(Statement statement) {
        if (statements.size() < 50) {
            statements.add(statement);
        } else {
            throw new IllegalStateException("Chromosome cannot have more than 50 statements.");
        }
    }

    /**
     * Removes a statement at the specified index.
     *
     * @param index the index of the statement to remove
     */
    public void removeStatement(int index) {
        if (index >= 0 && index < statements.size()) {
            statements.remove(index);
        } else {
            throw new IndexOutOfBoundsException("Invalid statement index: " + index);
        }
    }

    /**
     * Modifies a statement at the specified index.
     *
     * @param index     the index of the statement to modify
     * @param statement the new statement
     */
    public void modifyStatement(int index, Statement statement) {
        if (index >= 0 && index < statements.size()) {
            statements.set(index, statement);
        } else {
            throw new IndexOutOfBoundsException("Invalid statement index: " + index);
        }
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
        return new ArrayList<>(statements); // Return a copy to avoid external modification
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TestChromosome self() {
        return this;
    }
}
