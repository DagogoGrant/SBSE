package de.uni_passau.fim.se2.se.test_prioritisation.algorithms;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.Encoding;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.EncodingGenerator;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions.StoppingCondition;

import java.util.Random;

public final class SimulatedAnnealing<E extends Encoding<E>> implements SearchAlgorithm<E> {

    private final StoppingCondition stoppingCondition;
    private final EncodingGenerator<E> encodingGenerator;
    private final FitnessFunction<E> energyFunction;
    private final Random random;

    private double initialTemperature;
    private double coolingRate = 0.997;  // Slower cooling rate for more exploration
    private final int degreesOfFreedom;

    public SimulatedAnnealing(
            final StoppingCondition stoppingCondition,
            final EncodingGenerator<E> encodingGenerator,
            final FitnessFunction<E> energyFunction,
            final int degreesOfFreedom,
            final Random random) {

        if (stoppingCondition == null || encodingGenerator == null || energyFunction == null || random == null) {
            throw new IllegalArgumentException("Arguments cannot be null.");
        }

        this.stoppingCondition = stoppingCondition;
        this.encodingGenerator = encodingGenerator;
        this.energyFunction = energyFunction;
        this.degreesOfFreedom = degreesOfFreedom;
        this.random = random;
        this.initialTemperature = estimateInitialTemperature(100);  // Increased iterations for better estimation
    }

    /**
     * Estimates the initial temperature using a random walk approach.
     *
     * @param iterations the number of iterations for the random walk
     * @return the estimated initial temperature
     */
    public double estimateInitialTemperature(int iterations) {
        // Generate initial solution
        E currentSolution = encodingGenerator.get();
        double currentEnergy = energyFunction.applyAsDouble(currentSolution);
        double totalEnergyChange = 0.0;

        // Perform a random walk for the specified number of iterations
        for (int i = 0; i < iterations; i++) {
            E newSolution = currentSolution.deepCopy().mutate();
            double newEnergy = energyFunction.applyAsDouble(newSolution);

            // Accumulate the energy difference
            totalEnergyChange += Math.abs(newEnergy - currentEnergy);

            // Update current solution for next iteration
            currentSolution = newSolution;
            currentEnergy = newEnergy;
        }

        // Calculate average energy change
        double averageEnergyChange = totalEnergyChange / iterations;

        // Calculate initial temperature based on desired acceptance probability
        double p0 = 0.6;  // Increased initial acceptance probability
        return -averageEnergyChange / Math.log(p0);
    }

    @Override
    public E findSolution() {
        stoppingCondition.notifySearchStarted();

        E currentSolution = encodingGenerator.get();
        double currentEnergy = energyFunction.applyAsDouble(currentSolution);
        E bestSolution = currentSolution.deepCopy();
        double bestEnergy = currentEnergy;

        double temperature = initialTemperature;
        int iteration = 0;

        while (!stoppingCondition.searchMustStop() && temperature > 1e-5) {
            E newSolution = currentSolution.deepCopy();

            // Apply diversified mutation strategies multiple times
            for (int i = 0; i < degreesOfFreedom; i++) {
                int mutationType = random.nextInt(3);
                switch (mutationType) {
                    case 0 -> newSolution = newSolution.swapMutation();
                    case 1 -> newSolution = newSolution.scrambleMutation();
                    case 2 -> newSolution = newSolution.inversionMutation();
                }
            }

            double newEnergy = energyFunction.applyAsDouble(newSolution);

            if (acceptanceProbability(currentEnergy, newEnergy, temperature) > random.nextDouble()) {
                currentSolution = newSolution;
                currentEnergy = newEnergy;

                if (newEnergy < bestEnergy) {
                    bestSolution = newSolution.deepCopy();
                    bestEnergy = newEnergy;
                }
            }

            // Update temperature with slower cooling
            temperature *= coolingRate;

            stoppingCondition.notifyFitnessEvaluation();
            iteration++;
        }

        return bestSolution;
    }

    /**
     * Calculates the acceptance probability for moving to a new solution.
     * 
     * @param currentEnergy the energy of the current solution
     * @param newEnergy the energy of the new solution
     * @param temperature the current temperature
     * @return the probability of accepting the new solution
     */
    public double acceptanceProbability(double currentEnergy, double newEnergy, double temperature) {
        if (newEnergy < currentEnergy) {
            return 1.0;  // Always accept a better solution
        }
        return Math.exp((currentEnergy - newEnergy) / temperature);  // Probability for worse solutions
    }

    @Override
    public StoppingCondition getStoppingCondition() {
        return stoppingCondition;
    }
}
