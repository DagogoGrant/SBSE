package de.uni_passau.fim.se2.sbse.neat.algorithms.innovations;

public class ConcreteInnovation implements Innovation {
    private final int sourceNeuronId;
    private final int targetNeuronId;
    private final int innovationNumber;

    public ConcreteInnovation(int sourceNeuronId, int targetNeuronId, int innovationNumber) {
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
        return 31 * sourceNeuronId + targetNeuronId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ConcreteInnovation that = (ConcreteInnovation) obj;
        return sourceNeuronId == that.sourceNeuronId && targetNeuronId == that.targetNeuronId;
    }
}

