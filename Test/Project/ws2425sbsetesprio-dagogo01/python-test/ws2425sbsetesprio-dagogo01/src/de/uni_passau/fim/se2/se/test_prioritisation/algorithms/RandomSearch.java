package de.uni_passau.fim.se2.se.test_prioritisation.algorithms;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.Encoding;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.EncodingGenerator;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions.StoppingCondition;

public class RandomSearch<E extends Encoding<E>> extends SearchAlgorithm<E> {

    private final EncodingGenerator<E> generator;

    public RandomSearch(StoppingCondition stoppingCondition, EncodingGenerator<E> generator, FitnessFunction<E> fitnessFunction) {
        super(stoppingCondition, fitnessFunction);
        this.generator = generator;
    }

    @Override
    public E findSolution() {
        getStoppingCondition().notifySearchStarted();
        E bestSolution = null;
        double bestFitness = Double.NEGATIVE_INFINITY;

        while (!getStoppingCondition().searchMustStop()) {
            E candidate = generator.generate();
            double fitness = getFitnessFunction().applyAsDouble(candidate);
            getStoppingCondition().notifyFitnessEvaluation();

            if (fitness > bestFitness) {
                bestSolution = candidate;
                bestFitness = fitness;
            }
        }

        return bestSolution;
    }
}
