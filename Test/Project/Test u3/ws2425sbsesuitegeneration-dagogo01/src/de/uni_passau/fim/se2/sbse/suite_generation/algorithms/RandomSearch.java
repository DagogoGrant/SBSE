package de.uni_passau.fim.se2.sbse.suite_generation.algorithms;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.ChromosomeGenerator;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestChromosome;
import de.uni_passau.fim.se2.sbse.suite_generation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.IBranch;
import de.uni_passau.fim.se2.sbse.suite_generation.stopping_conditions.StoppingCondition;

import java.util.*;

public class RandomSearch implements GeneticAlgorithm<TestChromosome> {
    private final ChromosomeGenerator<TestChromosome> generator;
    private final Map<IBranch, FitnessFunction<TestChromosome>> branchToFitness;
    private final Set<IBranch> uncoveredBranches;
    private final Map<IBranch, TestChromosome> archive;
    private final StoppingCondition stoppingCondition;

    public RandomSearch(ChromosomeGenerator<TestChromosome> generator,
                        Map<IBranch, FitnessFunction<TestChromosome>> branchToFitness,
                        StoppingCondition stoppingCondition) {
        this.generator = generator;
        this.branchToFitness = branchToFitness;
        this.stoppingCondition = stoppingCondition;
        this.uncoveredBranches = new HashSet<>(branchToFitness.keySet());
        this.archive = new HashMap<>();
    }

    @Override
    public List<TestChromosome> findSolution() {
        stoppingCondition.notifySearchStarted();
        double bestFitness = Double.MAX_VALUE;

        while (!stoppingCondition.searchMustStop() && !uncoveredBranches.isEmpty()) {
            TestChromosome chromosome = generator.get();
            double fitness = evaluateFitness(chromosome);
            updateArchive(chromosome, fitness);

            if (fitness < bestFitness) {
                bestFitness = fitness;
            }

            stoppingCondition.notifyFitnessEvaluation();
        }

        return new ArrayList<>(archive.values());
    }
    private double evaluateFitness(TestChromosome chromosome) {
        double fitness = 0.0;
        for (IBranch branch : uncoveredBranches) {
            double distance = branchToFitness.get(branch).applyAsDouble(chromosome);
            fitness += 1 / (1 + distance); // Smaller distance increases fitness
        }
        return fitness;
    }
    
    

    private void updateArchive(TestChromosome chromosome, double fitness) {
        for (IBranch branch : new HashSet<>(uncoveredBranches)) {
            double branchFitness = branchToFitness.get(branch).applyAsDouble(chromosome);
            if (branchFitness == 0.0 || branchFitness < evaluateFitness(archive.getOrDefault(branch, chromosome))) {
                archive.put(branch, chromosome);
                uncoveredBranches.remove(branch);
            }
        }
    }
    
    @Override
    public StoppingCondition getStoppingCondition() {
        return stoppingCondition;
    }
}
