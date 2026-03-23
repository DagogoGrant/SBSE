package de.uni_passau.fim.se2.sbse.neat.chromosomes;

/**
 * Represents a neuron gene that is part of every NEAT chromosome.
 */
public class NeuronGene {
    private final int id;
    private final ActivationFunction activationFunction;
    private final NeuronType neuronType;

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
    }

    /**
     * Returns the ID of the neuron.
     *
     * @return The ID of the neuron.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the type of the neuron.
     *
     * @return The type of the neuron.
     */
    public NeuronType getNeuronType() {
        return neuronType;
    }

    /**
     * Returns the activation function of the neuron.
     *
     * @return The activation function of the neuron.
     */
    public ActivationFunction getActivationFunction() {
        return activationFunction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NeuronGene that = (NeuronGene) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return "NeuronGene{" +
                "id=" + id +
                ", activationFunction=" + activationFunction +
                ", neuronType=" + neuronType +
                '}';
    }
}