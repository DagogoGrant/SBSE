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
    private double crowdingDistance;
    /**
     * Constructs a new test chromosome with the given mutation and crossover operators.
     *
     * @param mutation  the mutation operator
     * @param crossover the crossover operator
     */
    public TestChromosome(final Mutation<TestChromosome> mutation, final Crossover<TestChromosome> crossover) {
        super(mutation, crossover);
        this.statements = new ArrayList<>();
    }

    /**
     * Constructs a new test chromosome with the given mutation operator, crossover operator, and list of statements.
     *
     * @param mutation    the mutation operator
     * @param crossover   the crossover operator
     * @param statements  the list of statements to initialize the chromosome with
     */
    public TestChromosome(final Mutation<TestChromosome> mutation, final Crossover<TestChromosome> crossover, final List<Statement> statements) {
        super(mutation, crossover);
        this.statements = new ArrayList<>(statements); // Ensure a new list is created
    }

    /**
     * Constructs a new test chromosome by copying the given chromosome.
     *
     * @param other the chromosome to copy
     */
    public TestChromosome(final TestChromosome other) {
        super(other);
        this.statements = new ArrayList<>(other.statements);
        this.crowdingDistance = other.crowdingDistance;  // Ensure this field is copied correctly
        this.density = other.density;  // Also copy density field
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
            statement.run(); // Execute each statement
        }

        return tracer.getDistances(); // Return branch distances
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Statement> getStatements() {
        return statements;
    }

    @Override
    public TestChromosome self() {
        return this;
    }
    
        public double getCrowdingDistance() {
            return crowdingDistance;
        }
    
        public void setCrowdingDistance(double crowdingDistance) {
            this.crowdingDistance = crowdingDistance;
        }
        private double density;

public void setDensity(double density) {
    this.density = density;
}

public double getDensity() {
    return density;
}

    }

