package de.uni_passau.fim.se2.sbse.neat.chromosomes;

/**
 * Represents a connection gene that is part of every NEAT chromosome.
 */
public class ConnectionGene {

    private final NeuronGene sourceNeuronGene;
    private final NeuronGene targetNeuronGene;
    private double weight;
    private boolean enabled;
    private final int innovationNumber;

    /**
     * Creates a new connection gene with the given source and target neuron, weight, enabled flag, and innovation number.
     *
     * @param sourceNeuronGene The source neuron of the connection.
     * @param targetNeuronGene The target neuron of the connection.
     * @param weight           The weight of the connection.
     * @param enabled          Whether the connection is enabled.
     * @param innovationNumber The innovation number of the connection serving as an identifier.
     */
    public ConnectionGene(NeuronGene sourceNeuronGene, NeuronGene targetNeuronGene, double weight, boolean enabled, int innovationNumber) {
        if (sourceNeuronGene == null || targetNeuronGene == null) {
            throw new IllegalArgumentException("Source and target neurons must not be null.");
        }
        if (sourceNeuronGene.getId() == targetNeuronGene.getId()) {
            throw new IllegalArgumentException("Source and target neurons cannot be the same.");
        }

        this.sourceNeuronGene = sourceNeuronGene;
        this.targetNeuronGene = targetNeuronGene;
        this.weight = weight;
        this.enabled = enabled;
        this.innovationNumber = innovationNumber;
    }

    /**
     * Returns the source neuron of the connection.
     *
     * @return The source neuron of the connection.
     */
    public NeuronGene getSourceNeuron() {
        return sourceNeuronGene;
    }

    /**
     * Returns the target neuron of the connection.
     *
     * @return The target neuron of the connection.
     */
    public NeuronGene getTargetNeuron() {
        return targetNeuronGene;
    }

    /**
     * Returns the weight of the connection.
     *
     * @return The weight of the connection.
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Updates the weight of the connection.
     *
     * @param weight The new weight of the connection.
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * Returns whether the connection is enabled.
     *
     * @return True if the connection is enabled, false otherwise.
     */
    public boolean getEnabled() {
        return enabled;
    }

    /**
     * Toggles the enabled status of the connection.
     *
     * @param enabled The new enabled status.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Returns the innovation number of the connection.
     *
     * @return The innovation number of the connection.
     */
    public int getInnovationNumber() {
        return innovationNumber;
    }

    @Override
    public String toString() {
        return "ConnectionGene{" +
                "sourceNeuronGene=" + sourceNeuronGene.getId() +
                ", targetNeuronGene=" + targetNeuronGene.getId() +
                ", weight=" + weight +
                ", enabled=" + enabled +
                ", innovationNumber=" + innovationNumber +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ConnectionGene that = (ConnectionGene) obj;
        return Double.compare(that.weight, weight) == 0 &&
                enabled == that.enabled &&
                innovationNumber == that.innovationNumber &&
                sourceNeuronGene.equals(that.sourceNeuronGene) &&
                targetNeuronGene.equals(that.targetNeuronGene);
    }

    @Override
    public int hashCode() {
        int result = sourceNeuronGene.hashCode();
        result = 31 * result + targetNeuronGene.hashCode();
        result = 31 * result + Double.hashCode(weight);
        result = 31 * result + Boolean.hashCode(enabled);
        result = 31 * result + innovationNumber;
        return result;
    }
}
