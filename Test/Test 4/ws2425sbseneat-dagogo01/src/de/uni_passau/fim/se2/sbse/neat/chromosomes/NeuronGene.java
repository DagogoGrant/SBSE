package de.uni_passau.fim.se2.sbse.neat.chromosomes;

/**
 * Represents a neuron gene that is part of every NEAT chromosome.
 * A neuron gene has a unique ID, activation function, neuron type, and a value that can be updated during propagation.
 */
public class NeuronGene {

    private final int id; // Unique ID of the neuron within the network.
    private final ActivationFunction activationFunction; // Activation function of the neuron.
    private final NeuronType neuronType; // Type of the neuron (INPUT, HIDDEN, OUTPUT, BIAS).
    private double value; // The current value of the neuron during propagation.

    /**
     * Creates a new neuron gene with the given ID, activation function, and neuron type.
     *
     * @param id                 The ID of the neuron.
     * @param activationFunction The activation function of the neuron.
     * @param neuronType         The type of the neuron (INPUT, HIDDEN, OUTPUT, BIAS).
     */
    public NeuronGene(int id, ActivationFunction activationFunction, NeuronType neuronType) {
        this.id = id;
        this.activationFunction = activationFunction;
        this.neuronType = neuronType;
        this.value = 0.0; // Default initial value.
    }

    /**
     * Returns the unique ID of the neuron.
     *
     * @return The ID of the neuron.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the type of the neuron (INPUT, HIDDEN, OUTPUT, BIAS).
     *
     * @return The neuron type.
     */
    public NeuronType getNeuronType() {
        return neuronType;
    }

    /**
     * Returns the activation function of the neuron.
     *
     * @return The activation function.
     */
    public ActivationFunction getActivationFunction() {
        return activationFunction;
    }

    /**
     * Sets the current value of the neuron.
     *
     * @param value The new value of the neuron.
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Returns the current value of the neuron.
     *
     * @return The value of the neuron.
     */
    public double getValue() {
        return value;
    }

    /**
     * Applies the activation function to the given input and returns the result.
     *
     * @param input The input value.
     * @return The activated value.
     */
    public double activate(double input) {
        switch (activationFunction) {
            case SIGMOID:
                return 1.0 / (1.0 + Math.exp(-input)); // Outputs between 0 and 1.
            case TANH:
                return Math.tanh(input); // Outputs between -1 and 1.
            case NONE:
            default:
                return input; // No activation.
        }
    }

    /**
     * Generates a string representation of the neuron gene for debugging purposes.
     *
     * @return A string representation of the neuron gene.
     */
    @Override
    public String toString() {
        return "NeuronGene{" +
                "id=" + id +
                ", activationFunction=" + activationFunction +
                ", neuronType=" + neuronType +
                ", value=" + value +
                '}';
    }

    /**
     * Compares this neuron gene with another object for equality based on their IDs.
     *
     * @param obj The object to compare to.
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        NeuronGene that = (NeuronGene) obj;
        return id == that.id;
    }

    /**
     * Computes the hash code of the neuron gene based on its ID.
     *
     * @return The hash code of the neuron gene.
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
