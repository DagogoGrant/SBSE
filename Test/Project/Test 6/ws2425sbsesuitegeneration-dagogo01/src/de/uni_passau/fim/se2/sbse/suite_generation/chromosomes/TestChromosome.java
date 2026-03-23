package de.uni_passau.fim.se2.sbse.suite_generation.chromosomes;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;
import de.uni_passau.fim.se2.sbse.suite_generation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.BranchTracer;
import de.uni_passau.fim.se2.sbse.suite_generation.mutation.Mutation;

/**
 * Represents a test chromosome that consists of a sequence of statements.
 * A test chromosome is designed to be mutated and crossed over to evolve toward
 * maximizing branch coverage of the Class Under Test (CUT).
 *
 * This class extends Chromosome and incorporates mutation and crossover strategies.
 */
public class TestChromosome extends Chromosome<TestChromosome> {

    private List<Statement> statements;  // Ensure compatibility with Chromosome
    private double density;

    public TestChromosome(
        final Mutation<TestChromosome> mutation,
        final Crossover<TestChromosome> crossover,
        final List<Statement> statements
    ) {
        super(mutation, crossover);
        if (statements == null || statements.isEmpty()) {
            throw new IllegalArgumentException("Statements must not be null or empty");
        }
        this.statements = statements;
    }

    @Override
    public Map<Integer, Double> call() {
        BranchTracer tracer = BranchTracer.getInstance();
        tracer.clear();

        for (Statement statement : statements) {
            try {
                statement.run();
            } catch (Exception e) {
                throw new RuntimeException("Failed to execute statement: " + statement.toString(), e);
            }
        }

        return tracer.getDistances();
    }

    @Override
    public List<Statement> getStatements() {
        return List.copyOf(statements);
    }

    @Override
    public TestChromosome copy() {
        return new TestChromosome(getMutation(), getCrossover(), List.copyOf(statements));
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        TestChromosome that = (TestChromosome) other;
        return getStatements().equals(that.getStatements()) &&
               getMutation().equals(that.getMutation()) &&
               getCrossover().equals(that.getCrossover());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMutation(), getCrossover(), getStatements());
    }

    @Override
    public TestChromosome self() {
        return this;
    }

    public double getDensity() {
        return density;
    }

    public void setDensity(double density) {
        this.density = density;
    }

    public void addStatement(Statement statement) {
        Objects.requireNonNull(statement, "Statement cannot be null");
        this.statements.add(statement);
    }

    public boolean removeStatement(Statement statement) {
        return this.statements.remove(statement);
    }

    public int getStatementCount() {
        return this.statements.size();
    }
}
