package de.uni_passau.fim.se2.sbse.neat.chromosomes;

/**
 * Represents a neuron gene that is part of every NEAT chromosome.
 */
public class NeuronGene {
    private final int id;
    private final ActivationFunction activationFunction;
    private final NeuronType neuronType;
    private double value;

    /**
     * Creates a new neuron with the given ID and activation function.
     *
     * @param id                 The ID of the neuron.
     * @param activationFunction The activation function of the neuron.
     * @param neuronType         The type of the neuron.
     */
    public NeuronGene(int id, ActivationFunction activationFunction, NeuronType neuronType) {
        this.id = id;
        this.activationFunction = activationFunction;
        this.neuronType = neuronType;
        this.value = 0.0;
    }

    public int getId() {
        return id;
    }

    public NeuronType getNeuronType() {
        return neuronType;
    }

    /**
     * Sets the value of the neuron.
     *
     * @param value The value to set.
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Gets the current value of the neuron.
     *
     * @return The current value of the neuron.
     */
    public double getValue() {
        return value;
    }

    /**
     * Applies the activation function to the given input.
     *
     * @param input The input value.
     * @return The result of applying the activation function.
     */
    public double activate(double input) {
        switch (activationFunction) {
            case SIGMOID:
                return 1.0 / (1.0 + Math.exp(-input));
            case TANH:
                return Math.tanh(input);
            case NONE:
            default:
                return input;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NeuronGene)) return false;
        NeuronGene that = (NeuronGene) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}

