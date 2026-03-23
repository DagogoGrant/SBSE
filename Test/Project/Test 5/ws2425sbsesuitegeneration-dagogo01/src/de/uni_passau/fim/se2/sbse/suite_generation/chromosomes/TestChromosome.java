package de.uni_passau.fim.se2.sbse.suite_generation.chromosomes;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;
import de.uni_passau.fim.se2.sbse.suite_generation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.BranchTracer;
import de.uni_passau.fim.se2.sbse.suite_generation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.ConstructorStatement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Represents a test case chromosome consisting of a sequence of statements.
 */
public class TestChromosome extends Chromosome<TestChromosome> {
    private static final int MAX_STATEMENTS = 50;

    private final List<Statement> statements;
    private double fitness; // Fitness value for this chromosome

    /**
     * Constructs a TestChromosome with the given mutation and crossover operators.
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
     * Copy constructor for creating a deep copy of the chromosome.
     *
     * @param other the chromosome to copy
     */
    public TestChromosome(TestChromosome other) {
        super(other);
        this.statements = new ArrayList<>(other.statements); // Deep copy of statements
        this.fitness = other.fitness; // Copy fitness value
    }

    @Override
    public TestChromosome copy() {
        return new TestChromosome(this);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof TestChromosome)) return false;
        TestChromosome that = (TestChromosome) other;
        return statements.equals(that.statements);
    }

    @Override
    public int hashCode() {
        return statements.hashCode();
    }

    @Override
    public Map<Integer, Double> call() throws RuntimeException {
        BranchTracer branchTracer = BranchTracer.getInstance();
        branchTracer.clear(); // Clear previous trace data
        for (Statement statement : statements) {
            try {
                statement.run();
            } catch (Exception e) {
                System.err.println("Error executing statement: " + statement);
                e.printStackTrace();
            }
        }
        Map<Integer, Double> distances = branchTracer.getDistances();
        setFitness(calculateFitnessFromBranchCoverage(distances)); // Update fitness based on branch coverage
        return distances;
    }

    private double calculateFitnessFromBranchCoverage(Map<Integer, Double> distances) {
        // Calculate fitness as inversely proportional to the branch distance
        double fitness = 0.0;
        for (Double distance : distances.values()) {
            fitness += 1.0 / (1.0 + distance); // Higher coverage -> Higher fitness
        }
        return fitness;
    }

    @Override
    public List<Statement> getStatements() {
        return Collections.unmodifiableList(statements);
    }

    /**
     * Adds a statement to the chromosome, ensuring constraints are met.
     *
     * @param statement the statement to add
     * @throws IllegalArgumentException if the first statement is not a constructor call
     * @throws IllegalStateException    if the chromosome exceeds the maximum number of statements
     */
    public void addStatement(Statement statement) {
        if (statements.isEmpty() && !(statement instanceof ConstructorStatement)) {
            throw new IllegalArgumentException("The first statement must be a constructor call.");
        }
        if (statements.size() >= MAX_STATEMENTS) {
            throw new IllegalStateException("Cannot add more than " + MAX_STATEMENTS + " statements.");
        }
        statements.add(statement);
    }

    /**
     * Removes a statement by its index.
     *
     * @param id the index of the statement to remove
     */
    public void removeStatement(int id) {
        if (id >= 0 && id < statements.size()) {
            statements.remove(id);
        } else {
            throw new IndexOutOfBoundsException("Invalid statement index: " + id);
        }
    }

    /**
     * Modifies a statement at the specified index.
     *
     * @param id        the index of the statement to modify
     * @param statement the new statement
     */
    public void modifyStatement(int id, Statement statement) {
        if (id >= 0 && id < statements.size()) {
            statements.set(id, statement);
        } else {
            throw new IndexOutOfBoundsException("Invalid statement index: " + id);
        }
    }

    public void setStatements(List<Statement> statements) {
        if (statements.size() > 50) {
            throw new IllegalArgumentException("A chromosome cannot have more than 50 statements.");
        }
        this.statements.clear();
        this.statements.addAll(statements);
    }

    @Override
    public TestChromosome self() {
        return this;
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
     * Retrieves the fitness value for this chromosome.
     *
     * @return the fitness value
     */
    public double getFitness() {
        return fitness;
    }
}
