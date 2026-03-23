package de.uni_passau.fim.se2.sbse.suite_minimisation.algorithms;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.selection.BinaryTournamentSelection;
import de.uni_passau.fim.se2.sbse.suite_minimisation.stopping_conditions.StoppingCondition;
import de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions.FitnessFunction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


/**
 * NSGA-II implementation for multi-objective optimization.
 */
public class NSGA2 implements GeneticAlgorithm<TestSuiteChromosome> {

    private final StoppingCondition stoppingCondition;
    private final Mutation<TestSuiteChromosome> mutation;
    private final Crossover<TestSuiteChromosome> crossover;
    private final BinaryTournamentSelection<TestSuiteChromosome> selection;
    private final FitnessFunction<TestSuiteChromosome> sizeFitnessFunction;
    private final FitnessFunction<TestSuiteChromosome> coverageFitnessFunction;
    private final int populationSize;

    private List<TestSuiteChromosome> population;

    public NSGA2(
            StoppingCondition stoppingCondition,
            Mutation<TestSuiteChromosome> mutation,
            Crossover<TestSuiteChromosome> crossover,
            BinaryTournamentSelection<TestSuiteChromosome> selection,
            FitnessFunction<TestSuiteChromosome> sizeFitnessFunction,
            FitnessFunction<TestSuiteChromosome> coverageFitnessFunction,
            int populationSize) {
        this.stoppingCondition = stoppingCondition;
        this.mutation = mutation;
        this.crossover = crossover;
        this.selection = selection;
        this.sizeFitnessFunction = sizeFitnessFunction;
        this.coverageFitnessFunction = coverageFitnessFunction;
        this.populationSize = populationSize;

        initializePopulation(); // Initialize the starting population
    }

    /**
     * Initializes the starting population with random chromosomes.
     */
    private void initializePopulation() {
        population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            TestSuiteChromosome chromosome = new TestSuiteChromosome(null, null, populationSize);
            population.add(chromosome);
        }
    }
    


    @Override
    public List<TestSuiteChromosome> findSolution() {
        notifySearchStarted();

        while (!stoppingCondition.searchMustStop()) {
            List<TestSuiteChromosome> offspringPopulation = new ArrayList<>();

            // Generate offspring through crossover and mutation
            for (int i = 0; i < populationSize; i += 2) {
                TestSuiteChromosome parent1 = selection.apply(population);
                TestSuiteChromosome parent2 = selection.apply(population);

                var offspringPair = crossover.apply(parent1, parent2);
                offspringPopulation.add(mutation.apply(offspringPair.getFst()));
                offspringPopulation.add(mutation.apply(offspringPair.getSnd()));
            }

            // Combine parent and offspring populations
            List<TestSuiteChromosome> combinedPopulation = new ArrayList<>(population);
            combinedPopulation.addAll(offspringPopulation);

            // Sort based on dominance
            combinedPopulation.sort(Comparator
                    .comparingDouble(sizeFitnessFunction::applyAsDouble)
                    .thenComparingDouble(coverageFitnessFunction::applyAsDouble));

            // Select the best individuals for the next generation
            population = combinedPopulation.subList(0, populationSize);

            notifyFitnessEvaluation(offspringPopulation.size());
        }

        return population; // Final Pareto front
    }

    @Override
    public StoppingCondition getStoppingCondition() {
        return stoppingCondition;
    }
}
