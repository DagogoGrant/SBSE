package de.uni_passau.fim.se2.sbse.suite_minimisation.utils;

import de.uni_passau.fim.se2.sbse.suite_minimisation.algorithms.*;
import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.*;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.*;
import de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions.*;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.*;
import de.uni_passau.fim.se2.sbse.suite_minimisation.selection.*;
import de.uni_passau.fim.se2.sbse.suite_minimisation.stopping_conditions.*;

import java.util.Random;

public class AlgorithmBuilder {

    private final Random random;
    private final StoppingCondition stoppingCondition;
    private final boolean[][] coverageMatrix;
    private final int numberTestCases;
    private final int numberLines;
    private final MinimizingFitnessFunction<? extends Chromosome<?>> sizeFF;
    private final MaximizingFitnessFunction<? extends Chromosome<?>> coverageFF;

    public AlgorithmBuilder(final Random random,
                            final StoppingCondition stoppingCondition,
                            final boolean[][] coverageMatrix) {
        if (coverageMatrix == null || coverageMatrix.length == 0 || coverageMatrix[0].length == 0) {
            throw new IllegalArgumentException("Coverage matrix must be non-empty.");
        }
        this.random = random;
        this.stoppingCondition = stoppingCondition;
        this.coverageMatrix = coverageMatrix;
        this.numberTestCases = coverageMatrix.length;
        this.numberLines = coverageMatrix[0].length;
        this.sizeFF = makeTestSuiteSizeFitnessFunction();
        this.coverageFF = makeTestSuiteCoverageFitnessFunction();
    }

    private MinimizingFitnessFunction<TestSuiteChromosome> makeTestSuiteSizeFitnessFunction() {
        double sizeWeight = 0.0001; // Default weight
        return new SizeFitnessFunction(sizeWeight);
    }

    private MaximizingFitnessFunction<TestSuiteChromosome> makeTestSuiteCoverageFitnessFunction() {
        double weightCoefficient = 0.9999; // Default weight
        return new CoverageFitnessFunction(coverageMatrix, weightCoefficient);
    }

    public MinimizingFitnessFunction<? extends Chromosome<?>> getSizeFF() {
        return sizeFF;
    }

    public MaximizingFitnessFunction<? extends Chromosome<?>> getCoverageFF() {
        return coverageFF;
    }

    public GeneticAlgorithm<? extends Chromosome<?>> buildAlgorithm(final SearchAlgorithmType algorithm) {
        return switch (algorithm) {
            case RANDOM_SEARCH -> buildRandomSearch();
            case NSGA_II -> buildNSGA2(0.0001, 0.9999, 0.1); // Pass sizeWeight, weightCoefficient, mutationProbability
        };
    }

    private GeneticAlgorithm<TestSuiteChromosome> buildNSGA2(double sizeWeight, double weightCoefficient, double mutationProbability) {
        SizeFitnessFunction sizeFitnessFunction = new SizeFitnessFunction(sizeWeight);
        CoverageFitnessFunction coverageFitnessFunction = new CoverageFitnessFunction(coverageMatrix, weightCoefficient);

        Mutation<TestSuiteChromosome> mutation = new TestSuiteMutation(mutationProbability);
        Crossover<TestSuiteChromosome> crossover = new TestSuiteCrossover();
        BinaryTournamentSelection<TestSuiteChromosome> selection = new BinaryTournamentSelection<>(
            sizeFitnessFunction.comparator(), random
        );

        TestSuiteChromosomeGenerator generator = new TestSuiteChromosomeGenerator(
            numberTestCases,
            new CoverageTracker(TestSuiteChromosome.class, TestSuiteChromosomeGenerator.class),
            mutation,
            crossover,
            random
        );

        return new NSGA2(
            stoppingCondition,
            mutation,
            crossover,
            selection,
            sizeFitnessFunction,
            coverageFitnessFunction,
            100, // Population size
            generator
        );
    }

    private GeneticAlgorithm<TestSuiteChromosome> buildRandomSearch() {
        Mutation<TestSuiteChromosome> mutation = new TestSuiteMutation(0.1);
        Crossover<TestSuiteChromosome> crossover = new TestSuiteCrossover();

        CoverageTracker coverageTracker = new CoverageTracker(
            TestSuiteChromosome.class,
            TestSuiteChromosomeGenerator.class
        );

        TestSuiteChromosomeGenerator generator = new TestSuiteChromosomeGenerator(
            numberTestCases,
            coverageTracker,
            mutation,
            crossover,
            random
        );

        double sizeWeight = 0.0001;
        SizeFitnessFunction sizeFitnessFunction = new SizeFitnessFunction(sizeWeight);
        double weightCoefficient = 0.9999;
        CoverageFitnessFunction coverageFitnessFunction = new CoverageFitnessFunction(coverageMatrix, weightCoefficient);

        return new RandomSearch(
            generator,
            sizeFitnessFunction,
            coverageFitnessFunction,
            stoppingCondition
        );
    }
}
