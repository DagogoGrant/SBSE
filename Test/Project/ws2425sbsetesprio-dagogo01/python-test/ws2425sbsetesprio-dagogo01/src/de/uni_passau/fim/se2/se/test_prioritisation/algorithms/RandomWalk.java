package de.uni_passau.fim.se2.se.test_prioritisation.algorithms;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.Encoding;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.EncodingGenerator;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions.StoppingCondition;

public class RandomWalk<E extends Encoding<E>> extends SearchAlgorithm<E> {
    private final EncodingGenerator<E> generator;

    public RandomWalk(StoppingCondition stoppingCondition, EncodingGenerator<E> generator, FitnessFunction<E> fitnessFunction) {
        super(stoppingCondition, fitnessFunction);
        this.generator = generator;
    }

    @Override
    public E findSolution() {
        E current = generator.generate();
        double currentFitness = getFitnessFunction().applyAsDouble(current);

        while (!getStoppingCondition().isFinished()) {
            E neighbor = generator.generate();
            double neighborFitness = getFitnessFunction().applyAsDouble(neighbor);

            if (neighborFitness > currentFitness) {
                current = neighbor;
                currentFitness = neighborFitness;
            }

            getStoppingCondition().notifyFitnessEvaluation();
        }

        return current;
    }
}
