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
     * @param neuronType         The type of the neuron (INPUT, OUTPUT, HIDDEN, BIAS).
     */
    public NeuronGene(int id, ActivationFunction activationFunction, NeuronType neuronType) {
        if (activationFunction == null || neuronType == null) {
            throw new IllegalArgumentException("ActivationFunction and NeuronType must not be null.");
        }
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
    public String toString() {
        return "NeuronGene{" +
                "id=" + id +
                ", activationFunction=" + activationFunction +
                ", neuronType=" + neuronType +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        NeuronGene that = (NeuronGene) obj;
        return id == that.id &&
                activationFunction == that.activationFunction &&
                neuronType == that.neuronType;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + activationFunction.hashCode();
        result = 31 * result + neuronType.hashCode();
        return result;
    }
}
