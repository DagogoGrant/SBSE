package de.uni_passau.fim.se2.sbse.neat.chromosomes;

/**
 * Represents a connection gene that is part of every NEAT chromosome.
 */
public class ConnectionGene {
    private final NeuronGene sourceNeuronGene;
    private final NeuronGene targetNeuronGene;
    private final double weight;
    private final boolean enabled;
    private final int innovationNumber;

    /**
     * Creates a new connection gene with the given source and target neuron, weight, enabled flag, and innovation number.
     *
     * @param sourceNeuronGene The source neuron of the connection.
     * @param targetNeuronGene The target neuron of the connection.
     * @param weight           The weight of the connection.
     * @param enabled          Whether the connection is enabled.
     * @param innovationNumber The innovation number of the connection serving as identifier.
     */
    public ConnectionGene(NeuronGene sourceNeuronGene, NeuronGene targetNeuronGene, double weight, boolean enabled, int innovationNumber) {
        this.sourceNeuronGene = sourceNeuronGene;
        this.targetNeuronGene = targetNeuronGene;
        this.weight = weight;
        this.enabled = enabled;
        this.innovationNumber = innovationNumber;
    }

    public NeuronGene getSourceNeuron() {
        return sourceNeuronGene;
    }

    public NeuronGene getTargetNeuron() {
        return targetNeuronGene;
    }

    public double getWeight() {
        return weight;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public int getInnovationNumber() {
        return innovationNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConnectionGene)) return false;
        ConnectionGene that = (ConnectionGene) o;
        return innovationNumber == that.innovationNumber;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(innovationNumber);
    }
}

