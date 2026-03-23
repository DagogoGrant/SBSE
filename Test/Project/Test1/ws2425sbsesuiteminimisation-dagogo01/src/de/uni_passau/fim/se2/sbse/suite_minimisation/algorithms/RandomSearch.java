package de.uni_passau.fim.se2.sbse.suite_minimisation.algorithms;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosomeGenerator;
import de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_minimisation.stopping_conditions.StoppingCondition;

import java.util.ArrayList;
import java.util.List;

/**
 * Random Search Algorithm for Test Suite Minimisation.
 * This algorithm generates random solutions and maintains a Pareto front of non-dominated solutions.
 */
public class RandomSearch implements GeneticAlgorithm<TestSuiteChromosome> {

    private final TestSuiteChromosomeGenerator generator;
    private final FitnessFunction<TestSuiteChromosome> sizeFitnessFunction;
    private final FitnessFunction<TestSuiteChromosome> coverageFitnessFunction;
    private final StoppingCondition stoppingCondition;

    /**
     * Constructs a RandomSearch algorithm instance.
     *
     * @param generator              the chromosome generator
     * @param sizeFitnessFunction    the fitness function to minimize size
     * @param coverageFitnessFunction the fitness function to maximize coverage
     * @param stoppingCondition      the stopping condition
     */
    public RandomSearch(TestSuiteChromosomeGenerator generator,
                        FitnessFunction<TestSuiteChromosome> sizeFitnessFunction,
                        FitnessFunction<TestSuiteChromosome> coverageFitnessFunction,
                        StoppingCondition stoppingCondition) {
        this.generator = generator;
        this.sizeFitnessFunction = sizeFitnessFunction;
        this.coverageFitnessFunction = coverageFitnessFunction;
        this.stoppingCondition = stoppingCondition;
    }

    /**
     * Executes the Random Search algorithm to find the Pareto front.
     *
     * @return the Pareto front after the search
     */
    @Override
    public List<TestSuiteChromosome> findSolution() {
        List<TestSuiteChromosome> paretoFront = new ArrayList<>();

        // Notify the stopping condition that the search has started
        stoppingCondition.notifySearchStarted();

        while (!stoppingCondition.searchMustStop()) {
            // Generate a random candidate solution
            TestSuiteChromosome candidate = generator.get();

            // Notify the stopping condition of a fitness evaluation
            stoppingCondition.notifyFitnessEvaluation();

            // Evaluate the candidate's fitness
            double candidateSizeFitness = sizeFitnessFunction.applyAsDouble(candidate);
            double candidateCoverageFitness = coverageFitnessFunction.applyAsDouble(candidate);

            // Check if the candidate is dominated or dominates existing solutions
            boolean isDominated = false;
            List<TestSuiteChromosome> dominatedSolutions = new ArrayList<>();

            for (TestSuiteChromosome solution : paretoFront) {
                double existingSizeFitness = sizeFitnessFunction.applyAsDouble(solution);
                double existingCoverageFitness = coverageFitnessFunction.applyAsDouble(solution);

                if (dominates(existingSizeFitness, existingCoverageFitness, candidateSizeFitness, candidateCoverageFitness)) {
                    isDominated = true;
                    break;
                } else if (dominates(candidateSizeFitness, candidateCoverageFitness, existingSizeFitness, existingCoverageFitness)) {
                    dominatedSolutions.add(solution);
                }
            }

            // Update the Pareto front if the candidate is not dominated
            if (!isDominated) {
                paretoFront.removeAll(dominatedSolutions);
                paretoFront.add(candidate);
            }
        }

        return paretoFront;
    }

    /**
     * Provides access to the stopping condition used by this algorithm.
     *
     * @return the stopping condition
     */
    @Override
    public StoppingCondition getStoppingCondition() {
        return stoppingCondition;
    }

    /**
     * Determines if one solution dominates another based on fitness values.
     *
     * @param size1       size fitness of solution 1
     * @param coverage1   coverage fitness of solution 1
     * @param size2       size fitness of solution 2
     * @param coverage2   coverage fitness of solution 2
     * @return true if solution 1 dominates solution 2
     */
    private boolean dominates(double size1, double coverage1, double size2, double coverage2) {
        return (size1 <= size2 && coverage1 >= coverage2) &&
                (size1 < size2 || coverage1 > coverage2);
    }
}
