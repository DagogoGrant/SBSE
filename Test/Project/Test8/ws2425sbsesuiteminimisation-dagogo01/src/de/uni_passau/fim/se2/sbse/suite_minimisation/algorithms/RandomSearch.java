package de.uni_passau.fim.se2.sbse.suite_minimisation.algorithms;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosomeGenerator;
import de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_minimisation.stopping_conditions.StoppingCondition;

import java.util.ArrayList;
import java.util.List;

/**
 * Random Search Algorithm for Test Suite Minimisation.
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
    public List<TestSuiteChromosome> findSolution() {
        List<TestSuiteChromosome> paretoFront = new ArrayList<>();
        stoppingCondition.notifySearchStarted();
    
        while (!stoppingCondition.searchMustStop()) {
            TestSuiteChromosome candidate = generator.get();
            double candidateSizeFitness = sizeFitnessFunction.applyAsDouble(candidate);
            double candidateCoverageFitness = coverageFitnessFunction.applyAsDouble(candidate);
    
            System.out.printf("Generated Candidate: Size = %.6f, Coverage = %.6f%n", candidateSizeFitness, candidateCoverageFitness);
    
            boolean isDominated = false;
            List<TestSuiteChromosome> dominatedSolutions = new ArrayList<>();
    
            for (TestSuiteChromosome solution : paretoFront) {
                double existingSizeFitness = sizeFitnessFunction.applyAsDouble(solution);
                double existingCoverageFitness = coverageFitnessFunction.applyAsDouble(solution);
    
                System.out.printf("Existing in Pareto Front: Size = %.6f, Coverage = %.6f%n", existingSizeFitness, existingCoverageFitness);
    
                if (dominates(existingSizeFitness, existingCoverageFitness, candidateSizeFitness, candidateCoverageFitness)) {
                    System.out.println("Candidate is dominated. Skipping addition.");
                    isDominated = true;
                    break;
                } else if (dominates(candidateSizeFitness, candidateCoverageFitness, existingSizeFitness, existingCoverageFitness)) {
                    System.out.println("Candidate dominates an existing solution. Removing the dominated solution.");
                    dominatedSolutions.add(solution);
                }
            }
    
            if (!isDominated) {
                paretoFront.removeAll(dominatedSolutions);
                paretoFront.add(candidate);
                System.out.println("Candidate added to Pareto Front.");
            }
    
            stoppingCondition.notifyFitnessEvaluation();
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
