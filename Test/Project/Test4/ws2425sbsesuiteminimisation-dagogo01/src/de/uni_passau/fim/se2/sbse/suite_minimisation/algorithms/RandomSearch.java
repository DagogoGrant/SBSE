package de.uni_passau.fim.se2.sbse.suite_minimisation.algorithms;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosomeGenerator;

import de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_minimisation.stopping_conditions.StoppingCondition;


import java.util.ArrayList;
import java.util.List;

public class RandomSearch implements GeneticAlgorithm<TestSuiteChromosome> {

    private final TestSuiteChromosomeGenerator generator;
    private final FitnessFunction<TestSuiteChromosome> sizeFitnessFunction;
    private final FitnessFunction<TestSuiteChromosome> coverageFitnessFunction;
    private final StoppingCondition stoppingCondition;

    public RandomSearch(TestSuiteChromosomeGenerator generator,
                        FitnessFunction<TestSuiteChromosome> sizeFitnessFunction,
                        FitnessFunction<TestSuiteChromosome> coverageFitnessFunction,
                        StoppingCondition stoppingCondition) {
        this.generator = generator;
        this.sizeFitnessFunction = sizeFitnessFunction;
        this.coverageFitnessFunction = coverageFitnessFunction;
        this.stoppingCondition = stoppingCondition;
    }

    @Override
    public List<TestSuiteChromosome> findSolution() {
        List<TestSuiteChromosome> paretoFront = new ArrayList<>();
    
        stoppingCondition.notifySearchStarted();
    
        while (!stoppingCondition.searchMustStop()) {
            TestSuiteChromosome candidate = generator.get();
            
            // Null check for the generated candidate
            if (candidate == null) {
                throw new IllegalStateException("Generated chromosome is null.");
            }
    
            stoppingCondition.notifyFitnessEvaluation();
    
            double candidateSizeFitness = sizeFitnessFunction.applyAsDouble(candidate);
            double candidateCoverageFitness = coverageFitnessFunction.applyAsDouble(candidate);
    
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
    
            if (!isDominated) {
                paretoFront.removeAll(dominatedSolutions);
                paretoFront.add(candidate);
            }
        }
    
        return paretoFront;
    }
    

    @Override
    public StoppingCondition getStoppingCondition() {
        return stoppingCondition;
    }

    private boolean dominates(double size1, double coverage1, double size2, double coverage2) {
        return (size1 <= size2 && coverage1 >= coverage2) &&
               (size1 < size2 || coverage1 > coverage2);
    }
}
