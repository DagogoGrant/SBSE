package de.uni_passau.fim.se2.sbse.neat.algorithms;

import de.uni_passau.fim.se2.sbse.neat.chromosomes.NetworkChromosome;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a group of similar chromosomes, also known as a "species" in NEAT.
 * This class is responsible for managing the members (chromosomes), 
 * calculating their average fitness, and determining the representative chromosome.
 */
public class PopulationGroup {

    // List of chromosomes that belong to this population group (species).
    private final List<NetworkChromosome> members = new ArrayList<>();

    // The representative chromosome of this species. Used to compare new chromosomes for compatibility.
    protected NetworkChromosome representative;

    // The average fitness of all chromosomes in this species, updated after evaluation.
    private double averageFitness;

    /**
     * Constructor to initialize the population group with a given representative chromosome.
     * The representative is automatically added as the first member of the group.
     *
     * @param representative The initial representative chromosome for this species.
     */
    public PopulationGroup(NetworkChromosome representative) {
        this.representative = representative;
        this.members.add(representative);  // Add the representative as the first member.
    }

    /**
     * Adds a new chromosome to this population group.
     * This method is called when a new chromosome is found to be compatible with this species.
     *
     * @param member The new chromosome to be added to the group.
     */
    public void addMember(NetworkChromosome member) {
        members.add(member);  // Add the chromosome to the list of members.
    }

    /**
     * Calculates and updates the average fitness of all chromosomes in this population group.
     * This method should be called after all members have been evaluated.
     */
    public void calculateAverageFitness() {
        // Use a stream to calculate the average fitness of all members.
        // If there are no members, the average fitness defaults to 0.0.
        averageFitness = members.stream().mapToDouble(NetworkChromosome::getFitness).average().orElse(0.0);
    }

    /**
     * Returns the list of all chromosomes in this population group.
     * Note: This returns a direct reference to the list, allowing external modification.
     * Be careful when using it.
     *
     * @return A list of NetworkChromosome objects that are members of this group.
     */
    public List<NetworkChromosome> getMembers() {
        return members;  // Return the list of members.
    }

    /**
     * Returns the average fitness of the population group.
     * The average fitness is calculated based on all members after they have been evaluated.
     *
     * @return The average fitness value of the group.
     */
    public double getAverageFitness() {
        return averageFitness;
    }

    /**
     * Clears all members from the population group.
     * This method is typically called at the start of a new generation to reset the group.
     * The representative chromosome is not retained after clearing.
     */
    public void clear() {
        members.clear();  // Remove all members from the list.
    }
}
