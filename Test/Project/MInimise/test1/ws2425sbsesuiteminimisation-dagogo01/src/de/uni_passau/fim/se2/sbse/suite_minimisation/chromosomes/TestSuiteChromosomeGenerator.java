package de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes;

import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Randomness;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.*;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.*;

public class TestSuiteChromosomeGenerator implements ChromosomeGenerator<TestSuiteChromosome> {

    private final int numTestCases;
    private final Mutation<TestSuiteChromosome> mutation;
    private final Crossover<TestSuiteChromosome> crossover;

    public TestSuiteChromosomeGenerator(int numTestCases, 
                                        Mutation<TestSuiteChromosome> mutation, 
                                        Crossover<TestSuiteChromosome> crossover) {
        this.numTestCases = numTestCases;
        this.mutation = mutation;
        this.crossover = crossover;
    }

    @Override
    public TestSuiteChromosome get() {
        boolean[] genes = new boolean[numTestCases];
        for (int i = 0; i < numTestCases; i++) {
            genes[i] = Randomness.random().nextBoolean();
        }
        return new TestSuiteChromosome(genes, mutation, crossover);
    }
}

