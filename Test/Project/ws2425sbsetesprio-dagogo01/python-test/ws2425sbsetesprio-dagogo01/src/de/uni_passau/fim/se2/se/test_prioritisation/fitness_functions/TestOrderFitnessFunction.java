package de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;

/**
 * A fitness function implementation for test orders based on a coverage matrix.
 */
public class TestOrderFitnessFunction implements FitnessFunction<TestOrder> {

    private final boolean[][] matrix;

    /**
     * Constructs a TestOrderFitnessFunction with the given coverage matrix.
     *
     * @param matrix The coverage matrix, where {@code matrix[i][j]} indicates
     *               whether test case i covers feature j.
     */
    public TestOrderFitnessFunction(boolean[][] matrix) {
        this.matrix = matrix;
    }

    /**
     * Calculates the fitness value as a double for a given TestOrder object.
     *
     * @param order The TestOrder to evaluate.
     * @return The calculated fitness value.
     */
    @Override
    public double applyAsDouble(TestOrder order) {
        int fitness = 0;
        int[] positions = order.getPositions();

        for (int i = 0; i < positions.length; i++) {
            for (int j = i + 1; j < positions.length; j++) {
                if (matrix[positions[i]][positions[j]]) {
                    fitness++;
                }
            }
        }
        return fitness;
    }

    /**
     * Maximises the fitness value for a given TestOrder object.
     *
     * @param order The TestOrder to evaluate.
     * @return The calculated fitness value for maximisation.
     */
    @Override
    public double maximise(TestOrder order) {
        return applyAsDouble(order);
    }

    /**
     * Minimises the fitness value for a given TestOrder object.
     *
     * @param order The TestOrder to evaluate.
     * @return The calculated fitness value for minimisation.
     */
    @Override
    public double minimise(TestOrder order) {
        return -applyAsDouble(order);
    }
}
