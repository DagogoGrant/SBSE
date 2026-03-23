package de.uni_passau.fim.se2.sbse.suite_minimisation.algorithms;
import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosomeGenerator;
import de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_minimisation.stopping_conditions.StoppingCondition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
        Map<TestSuiteChromosome, double[]> fitnessMap = new HashMap<>();

        stoppingCondition.notifySearchStarted();

        while (!stoppingCondition.searchMustStop()) {
            TestSuiteChromosome candidate = generator.get();
            if (candidate == null) {
                throw new IllegalStateException("Chromosome generator produced a null candidate.");
            }

            stoppingCondition.notifyFitnessEvaluation();

            double candidateSizeFitness = sizeFitnessFunction.applyAsDouble(candidate);
            double candidateCoverageFitness = coverageFitnessFunction.applyAsDouble(candidate);

            if (Double.isNaN(candidateSizeFitness) || Double.isNaN(candidateCoverageFitness)) {
                throw new IllegalStateException("Fitness functions returned NaN values.");
            }

            fitnessMap.put(candidate, new double[]{candidateSizeFitness, candidateCoverageFitness});

            // Find dominated solutions
            List<TestSuiteChromosome> dominatedSolutions = paretoFront.stream()
                .filter(solution -> dominates(candidateSizeFitness, candidateCoverageFitness,
                                              fitnessMap.get(solution)[0], fitnessMap.get(solution)[1]))
                .toList();

            // Check if candidate is dominated
            boolean isDominated = paretoFront.stream()
                .anyMatch(solution -> dominates(fitnessMap.get(solution)[0], fitnessMap.get(solution)[1],
                                                candidateSizeFitness, candidateCoverageFitness));

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
