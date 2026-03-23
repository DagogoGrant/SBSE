package de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;

/**
 * A fitness function that minimizes the size of the test suite by penalizing 
 * chromosomes with more selected test cases. The function computes a normalized 
 * fitness value between 0.0 and 1.0, with smaller values being more desirable.
 */
public class SizeFitnessFunction implements MinimizingFitnessFunction<TestSuiteChromosome> {

    private final double sizeWeight; // Weight for penalizing size

    /**
     * Constructor to initialize the size fitness function with a specific weight.
     *
     * @param sizeWeight the weight for penalizing size (0.0 to 1.0)
     * @throws IllegalArgumentException if the sizeWeight is outside the range [0.0, 1.0]
     */
    public SizeFitnessFunction(double sizeWeight) {
        if (sizeWeight < 0.0 || sizeWeight > 1.0) {
            throw new IllegalArgumentException("Size weight must be between 0.0 and 1.0.");
        }
        this.sizeWeight = sizeWeight;
    }

    @Override
    public double applyAsDouble(TestSuiteChromosome chromosome) {
        if (chromosome == null || chromosome.getTestCases() == null) {
            throw new IllegalArgumentException("Chromosome and its test cases cannot be null.");
        }

        boolean[] testCases = chromosome.getTestCases();
        if (testCases.length == 0) {
            throw new IllegalArgumentException("Chromosome must contain at least one test case.");
        }

        int selectedCount = 0;

        // Count the number of selected test cases
        for (boolean testCase : testCases) {
            if (testCase) {
                selectedCount++;
            }
        }

        // Calculate the normalized size
        double normalizedSize = (double) selectedCount / testCases.length;

        // Compute the weighted fitness and cap at 1.0
        double fitness = Math.min(1.0, sizeWeight * normalizedSize);

        // Debugging logs
        System.out.println("Debugging SizeFitnessFunction:");
        System.out.println("- Total Test Cases: " + testCases.length);
        System.out.println("- Selected Test Cases: " + selectedCount);
        System.out.println("- Normalized Size: " + normalizedSize);
        System.out.println("- Size Weight: " + sizeWeight);
        System.out.println("- Final Fitness (Capped at 1.0): " + fitness);

        return fitness;
    }

    /**
     * Returns the weight used for penalizing size.
     *
     * @return the size weight
     */
    public double getSizeWeight() {
        return sizeWeight;
    }
}
