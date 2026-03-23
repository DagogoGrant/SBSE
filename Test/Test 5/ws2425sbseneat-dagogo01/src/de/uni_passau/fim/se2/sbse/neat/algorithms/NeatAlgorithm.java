package de.uni_passau.fim.se2.sbse.neat.algorithms;

import de.uni_passau.fim.se2.sbse.neat.chromosomes.*;
import de.uni_passau.fim.se2.sbse.neat.algorithms.innovations.Innovation;
import de.uni_passau.fim.se2.sbse.neat.environments.Environment;
import de.uni_passau.fim.se2.sbse.neat.crossover.NeatCrossover;
import de.uni_passau.fim.se2.sbse.neat.mutation.NeatMutation;
import de.uni_passau.fim.se2.sbse.neat.utils.Randomness;

import java.util.*;
import java.util.stream.Collectors;

/**
 * NeatAlgorithm class implements the Neuroevolution interface.
 * This class is responsible for running the NEAT (NeuroEvolution of Augmenting Topologies) algorithm.
 * It evolves a population of neural networks over multiple generations to solve a given task.
 */
public class NeatAlgorithm implements Neuroevolution {
    private final int populationSize;       // Size of the population for each generation
    private final int maxGenerations;       // Maximum number of generations to evolve
    private final Random random;            // Random generator for stochastic operations
    private final Set<Innovation> innovations;  // Set to keep track of all innovations in the population
    private final List<PopulationGroup> species; // List of species (groups of compatible chromosomes)
    private final Environment environment;  // The environment where chromosomes are evaluated
    private int currentGeneration = 0;      // Current generation counter

    // Parameters for compatibility, survival, and mutation rates
    private static final double COMPATIBILITY_THRESHOLD = 4.0;
    private static final double SURVIVAL_THRESHOLD = 0.2;
    private static final double WEIGHT_MUTATION_RATE = 0.8;
    private static final double ADD_CONNECTION_RATE = 0.05;
    private static final double ADD_NEURON_RATE = 0.03;

    // Operators for crossover and mutation
    private final NeatCrossover crossover;
    private final NeatMutation mutation;

    /**
     * Constructor to initialize the NeatAlgorithm.
     *
     * @param populationSize The number of chromosomes in the population.
     * @param maxGenerations The maximum number of generations for the algorithm to run.
     * @param environment    The environment in which chromosomes are evaluated.
     */
    public NeatAlgorithm(int populationSize, int maxGenerations, Environment environment) {
        this.populationSize = populationSize;
        this.maxGenerations = maxGenerations;
        this.environment = environment;
        this.random = Randomness.random();
        this.innovations = new HashSet<>();
        this.species = new ArrayList<>();
        this.crossover = new NeatCrossover(random);
        this.mutation = new NeatMutation(innovations, random);
    }

    /**
     * Returns the current generation of the neuroevolution algorithm.
     *
     * @return The current generation of the neuroevolution algorithm.
     */

     @Override
     public int getGeneration() {
         // Return the current generation count of the algorithm.
         return currentGeneration;
     }
     
     @Override
     public Agent solve(Environment environment) {
         // Step 1: Initialize the population with randomly generated chromosomes.
         initializePopulation();
     
         NetworkChromosome bestChromosome = null;  // Keep track of the best chromosome found.
         double bestFitness = Double.NEGATIVE_INFINITY;  // Start with the lowest possible fitness.
     
         // Step 2: Iterate through generations until the maximum is reached.
         while (currentGeneration < maxGenerations) {
             // Evaluate the fitness of the entire population.
             evaluatePopulation();
     
             // Find the best chromosome of the current generation.
             NetworkChromosome currentBest = getBestChromosome();
             if (currentBest.getFitness() > bestFitness) {
                 bestFitness = currentBest.getFitness();  // Update the best fitness.
                 bestChromosome = currentBest;  // Keep a reference to the best chromosome.
     
                 // Check if the environment considers this chromosome as a solution.
                 if (environment.solved(bestChromosome)) {
                     break;  // Stop the loop early if a solution is found.
                 }
             }
     
             // Step 3: Create the next generation by evolving the current population.
             createNextGeneration();
             currentGeneration++;  // Move to the next generation.
         }
     
         // Return the best chromosome found, or the current best if none solved the task.
         return bestChromosome != null ? bestChromosome : getBestChromosome();
     }
     
     private void initializePopulation() {
         // Generate an initial population using a NetworkGenerator.
         NetworkGenerator generator = new NetworkGenerator(
             innovations,
             environment.stateSize(),    // Number of input neurons based on the environment's state size.
             environment.actionInputSize(),  // Number of output neurons based on the environment's action size.
             random
         );
     
         // Populate the initial species with randomly generated chromosomes.
         for (int i = 0; i < populationSize; i++) {
             NetworkChromosome chromosome = generator.generate();
             assignToSpecies(chromosome);  // Assign the generated chromosome to a species.
         }
     }
     
     private void assignToSpecies(NetworkChromosome chromosome) {
         // Check each existing species to see if the new chromosome is compatible.
         for (PopulationGroup group : species) {
             if (compatibilityDistance(chromosome, group.representative) < COMPATIBILITY_THRESHOLD) {
                 group.addMember(chromosome);  // Add the chromosome to the compatible species.
                 return;
             }
         }
     
         // If no compatible species was found, create a new one for this chromosome.
         species.add(new PopulationGroup(chromosome));
     }
     
     private double compatibilityDistance(NetworkChromosome genome1, NetworkChromosome genome2) {
         // Calculate the genetic distance between two chromosomes to determine compatibility.
     
         List<ConnectionGene> connections1 = genome1.getConnections();
         List<ConnectionGene> connections2 = genome2.getConnections();
     
         // Map each connection by its innovation number for quick lookup.
         Map<Integer, ConnectionGene> innovationMap1 = connections1.stream()
             .collect(Collectors.toMap(
                 ConnectionGene::getInnovationNumber,
                 c -> c,
                 (existing, replacement) -> existing  // Handle conflicts by keeping the first entry.
             ));
     
         Map<Integer, ConnectionGene> innovationMap2 = connections2.stream()
             .collect(Collectors.toMap(
                 ConnectionGene::getInnovationNumber,
                 c -> c,
                 (existing, replacement) -> existing
             ));
     
         int disjoint = 0;  // Count the number of disjoint genes (present in one genome but not the other).
         double weightDiff = 0;  // Sum of weight differences for matching genes.
         int matching = 0;  // Count the number of matching genes.
     
         // Create a set of all innovation numbers present in either genome.
         Set<Integer> allInnovations = new HashSet<>();
         allInnovations.addAll(innovationMap1.keySet());
         allInnovations.addAll(innovationMap2.keySet());
     
         // Find the maximum innovation number in both genomes for calculating excess genes.
         int maxInnovation1 = connections1.stream().mapToInt(ConnectionGene::getInnovationNumber).max().orElse(0);
         int maxInnovation2 = connections2.stream().mapToInt(ConnectionGene::getInnovationNumber).max().orElse(0);
     
         for (int innovation : allInnovations) {
             ConnectionGene gene1 = innovationMap1.get(innovation);
             ConnectionGene gene2 = innovationMap2.get(innovation);
     
             if (gene1 != null && gene2 != null) {
                 // Both genomes have this connection, so calculate the weight difference.
                 matching++;
                 weightDiff += Math.abs(gene1.getWeight() - gene2.getWeight());
             } else {
                 disjoint++;  // The connection is present in only one genome.
             }
         }
     
         // Normalize the distance by the size of the larger genome.
         int N = Math.max(connections1.size(), connections2.size());
         N = N < 20 ? 1 : N;  // Avoid division by a small number to prevent inflated values.
     
         double avgWeightDiff = matching == 0 ? 0 : weightDiff / matching;
         int excess = Math.abs(maxInnovation1 - maxInnovation2);
     
         // Return the compatibility distance using the formula: (excess + disjoint) / N + average weight difference.
         return (excess + disjoint) / (double) N + avgWeightDiff;
     }
     
     private void evaluatePopulation() {
         // Evaluate the fitness of each member in all species.
         for (PopulationGroup group : species) {
             for (NetworkChromosome member : group.getMembers()) {
                 double fitness = environment.evaluate(member);  // Use the environment to calculate fitness.
                 member.setFitness(fitness);  // Assign the calculated fitness to the member.
             }
             group.calculateAverageFitness();  // Update the average fitness for the species.
         }
     }
     

     private void createNextGeneration() {
        // Calculate the total adjusted fitness for all species.
        double totalAdjustedFitness = species.stream()
                .mapToDouble(s -> {
                    double speciesSize = s.getMembers().size();
                    // Adjust the fitness of each member by dividing by the size of the species.
                    s.getMembers().forEach(m -> m.setFitness(m.getFitness() / speciesSize));
                    return s.getAverageFitness();  // Return the average fitness for this species.
                })
                .sum();  // Sum the adjusted fitness of all species.
    
        List<NetworkChromosome> newPopulation = new ArrayList<>();
    
        // Elitism: Keep the best chromosome from each species if the species is large enough.
        for (PopulationGroup group : species) {
            if (group.getMembers().size() >= 5) {
                NetworkChromosome best = group.getMembers().stream()
                        .max(Comparator.comparingDouble(NetworkChromosome::getFitness))
                        .get();  // Find the member with the highest fitness.
                newPopulation.add(best);  // Add the best member to the new population.
            }
        }
    
        // Fill the rest of the population until the population size is reached.
        while (newPopulation.size() < populationSize) {
            // Select a species based on its fitness.
            PopulationGroup selectedGroup = selectSpecies(totalAdjustedFitness);
            NetworkChromosome child;
    
            // Perform crossover with an 80% chance, otherwise copy a single parent.
            if (random.nextDouble() < 0.8) {
                NetworkChromosome parent1 = selectParent(selectedGroup);
                NetworkChromosome parent2 = selectParent(selectedGroup);
                child = crossover.apply(parent1, parent2);  // Create a child using crossover.
            } else {
                child = selectParent(selectedGroup);  // Use a single parent without crossover.
            }
    
            // Apply mutations with specified probabilities.
            if (random.nextDouble() < WEIGHT_MUTATION_RATE) {
                child = mutation.mutateWeights(child);  // Mutate the weights of the child.
            }
            if (random.nextDouble() < ADD_CONNECTION_RATE) {
                child = mutation.addConnection(child);  // Add a new connection.
            }
            if (random.nextDouble() < ADD_NEURON_RATE) {
                child = mutation.addNeuron(child);  // Add a new neuron.
            }
    
            newPopulation.add(child);  // Add the newly created child to the population.
        }
    
        // Clear members of all species to prepare for the next generation.
        species.forEach(PopulationGroup::clear);
    
        // Reassign the new population to the appropriate species.
        newPopulation.forEach(this::assignToSpecies);
    
        // Remove any species that no longer have members.
        species.removeIf(s -> s.getMembers().isEmpty());
    
        // Ensure a minimum number of species exists by adding a random member to a new species if necessary.
        if (species.size() < 5) {
            NetworkChromosome randomMember = newPopulation.get(random.nextInt(newPopulation.size()));
            species.add(new PopulationGroup(randomMember));
        }
    }
    
    private NetworkChromosome selectParent(PopulationGroup group) {
        // Select a parent chromosome from the given species using a mini-tournament selection.
    
        List<NetworkChromosome> members = group.getMembers();
        NetworkChromosome best = null;
        double bestFitness = Double.NEGATIVE_INFINITY;
    
        // Tournament selection: randomly pick 3 members and select the one with the highest fitness.
        for (int i = 0; i < 3; i++) {
            NetworkChromosome contestant = members.get(random.nextInt(members.size()));
            if (contestant.getFitness() > bestFitness) {
                best = contestant;  // Update the best chromosome found in this tournament.
                bestFitness = contestant.getFitness();
            }
        }
    
        // Return the chromosome with the best fitness from the tournament.
        return best;
    }
    

    private PopulationGroup getBestSpecies() {
        // Find the species with the highest average fitness.
        return species.stream()
                .max(Comparator.comparingDouble(PopulationGroup::getAverageFitness))  // Compare species by their average fitness.
                .orElseThrow(() -> new IllegalStateException("No species available"));  // Throw an exception if no species exist.
    }
    
    private NetworkChromosome getBestChromosome() {
        // Find the best (fittest) chromosome across all species.
        return species.stream()
                .flatMap(s -> s.getMembers().stream())  // Flatten the list of species into a stream of all their members.
                .max(Comparator.comparingDouble(NetworkChromosome::getFitness))  // Compare chromosomes by their fitness.
                .orElseThrow(() -> new IllegalStateException("No chromosomes in population"));  // Throw an exception if no chromosomes are found.
    }
    
    private PopulationGroup selectSpecies(double totalFitness) {
        // Select a species based on its relative fitness using a fitness-proportionate selection method (roulette wheel selection).
        double dart = random.nextDouble() * totalFitness;  // Generate a random "dart" between 0 and the total fitness.
        double sum = 0;
    
        for (PopulationGroup s : species) {
            sum += s.getAverageFitness();  // Accumulate the fitness of each species.
            if (sum > dart) {
                return s;  // Return the species where the accumulated fitness exceeds the dart value.
            }
        }
    
        // Return the last species as a fallback. This case rarely occurs due to the way the dart value is chosen.
        return species.get(species.size() - 1);
    }
}
    