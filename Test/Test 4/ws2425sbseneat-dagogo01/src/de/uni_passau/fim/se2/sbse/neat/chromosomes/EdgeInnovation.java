package de.uni_passau.fim.se2.sbse.neat.chromosomes;

import de.uni_passau.fim.se2.sbse.neat.algorithms.innovations.Innovation;
import java.util.Objects;

/**
 * Represents an innovation for an edge (connection) in a NEAT neural network.
 */
public class EdgeInnovation implements Innovation {
    private final int sourceNeuronId;
    private final int targetNeuronId;
    private final int innovationNumber;

    /**
     * Constructs a new EdgeInnovation with the specified source neuron, target neuron, and innovation number.
     *
     * @param sourceNeuronId   The ID of the source neuron.
     * @param targetNeuronId   The ID of the target neuron.
     * @param innovationNumber The unique innovation number for this edge.
     */
    public EdgeInnovation(int sourceNeuronId, int targetNeuronId, int innovationNumber) {
        this.sourceNeuronId = sourceNeuronId;
        this.targetNeuronId = targetNeuronId;
        this.innovationNumber = innovationNumber;
    }

    public int getSourceNeuronId() {
        return sourceNeuronId;
    }

    public int getTargetNeuronId() {
        return targetNeuronId;
    }

    public int getInnovationNumber() {
        return innovationNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceNeuronId, targetNeuronId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof EdgeInnovation)) return false;
        EdgeInnovation that = (EdgeInnovation) obj;
        return sourceNeuronId == that.sourceNeuronId && targetNeuronId == that.targetNeuronId;
    }

    @Override
    public String toString() {
        return "EdgeInnovation{" +
                "sourceNeuronId=" + sourceNeuronId +
                ", targetNeuronId=" + targetNeuronId +
                ", innovationNumber=" + innovationNumber +
                '}';
    }
}
