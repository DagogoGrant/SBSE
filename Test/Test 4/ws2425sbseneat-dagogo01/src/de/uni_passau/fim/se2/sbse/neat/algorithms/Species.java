package de.uni_passau.fim.se2.sbse.neat.algorithms;

import de.uni_passau.fim.se2.sbse.neat.chromosomes.NetworkChromosome;
import java.util.ArrayList;
import java.util.List;

public class Species {
    private final List<NetworkChromosome> members = new ArrayList<>();
    protected NetworkChromosome representative;
    private double averageFitness;

    public Species(NetworkChromosome representative) {
        this.representative = representative;
        this.members.add(representative);
    }

    public void addMember(NetworkChromosome member) {
        members.add(member);
    }

    public void calculateAverageFitness() {
        averageFitness = members.stream().mapToDouble(NetworkChromosome::getFitness).average().orElse(0.0);
    }

    public List<NetworkChromosome> getMembers() {
        return members;
    }

    public double getAverageFitness() {
        return averageFitness;
    }

    public void clear() {
        members.clear();
    }
    public NetworkChromosome getRepresentative() {
        return representative;
    }
}