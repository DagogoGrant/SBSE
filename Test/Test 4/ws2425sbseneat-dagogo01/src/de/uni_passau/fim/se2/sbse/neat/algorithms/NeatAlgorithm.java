package de.uni_passau.fim.se2.sbse.neat.algorithms;

import de.uni_passau.fim.se2.sbse.neat.chromosomes.*;
import de.uni_passau.fim.se2.sbse.neat.algorithms.innovations.Innovation;
import de.uni_passau.fim.se2.sbse.neat.environments.Environment;
import de.uni_passau.fim.se2.sbse.neat.crossover.NeatCrossover;
import de.uni_passau.fim.se2.sbse.neat.mutation.NeatMutation;

import java.util.*;
import java.util.stream.Collectors;

public class NeatAlgorithm implements Neuroevolution {

    private final int populationSize;
    private final int maxGenerations;
    private final Random random;
    private final Set<Innovation> innovations = new HashSet<>();
    private final List<Species> species = new ArrayList<>();
    private final NeatCrossover crossover;
    private final NeatMutation mutation;
    private int currentGeneration = 0;
    private List<NetworkChromosome> population;

    private static final double COMPATIBILITY_THRESHOLD = 4.0;
    private static final double WEIGHT_MUTATION_RATE = 0.8;
    private static final double ADD_CONNECTION_RATE = 0.05;
    private static final double ADD_NEURON_RATE = 0.03;
    private static final double SURVIVAL_THRESHOLD = 0.2;

    public NeatAlgorithm(int populationSize, int maxGenerations, Random random) {
        this.populationSize = populationSize;
        this.maxGenerations = maxGenerations;
        this.random = random;
        this.crossover = new NeatCrossover(random);
        this.mutation = new NeatMutation(innovations, random);
    }

    @Override
    public int getGeneration() {
        return currentGeneration;
    }

    @Override
    public Agent solve(Environment environment) {
        initializePopulation(environment);

        NetworkChromosome bestChromosome = null;
        double bestFitness = Double.NEGATIVE_INFINITY;

        while (currentGeneration < maxGenerations) {
            evaluatePopulation(environment);

            NetworkChromosome currentBest = getBestChromosome();
            if (currentBest.getFitness() > bestFitness) {
                bestFitness = currentBest.getFitness();
                bestChromosome = currentBest;

                if (environment.solved(bestChromosome)) {
                    System.out.println("Solution found at generation " + currentGeneration);
                    break;
                }
            }

            evolve();
            currentGeneration++;
        }

        return bestChromosome;
    }

    private void initializePopulation(Environment environment) {
        NetworkGenerator generator = new NetworkGenerator(
                innovations, environment.stateSize(), environment.actionInputSize(), random
        );

        population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            NetworkChromosome chromosome = generator.generate();
            assignToSpecies(chromosome);
        }
    }

    private void assignToSpecies(NetworkChromosome chromosome) {
        for (Species s : species) {
            if (compatibilityDistance(chromosome, s.getRepresentative()) < COMPATIBILITY_THRESHOLD) {
                s.addMember(chromosome);
                return;
            }
        }
        species.add(new Species(chromosome));
    }

    private double compatibilityDistance(NetworkChromosome genome1, NetworkChromosome genome2) {
        Map<Integer, ConnectionGene> innovations1 = getInnovationMap(genome1);
        Map<Integer, ConnectionGene> innovations2 = getInnovationMap(genome2);

        int disjoint = 0;
        double weightDiff = 0;
        int matching = 0;

        Set<Integer> allInnovations = new HashSet<>();
        allInnovations.addAll(innovations1.keySet());
        allInnovations.addAll(innovations2.keySet());

        for (int innovation : allInnovations) {
            ConnectionGene gene1 = innovations1.get(innovation);
            ConnectionGene gene2 = innovations2.get(innovation);

            if (gene1 != null && gene2 != null) {
                matching++;
                weightDiff += Math.abs(gene1.getWeight() - gene2.getWeight());
            } else {
                disjoint++;
            }
        }

        int N = Math.max(innovations1.size(), innovations2.size());
        double avgWeightDiff = matching == 0 ? 0 : weightDiff / matching;

        return (disjoint / (double) N) + avgWeightDiff;
    }

    private Map<Integer, ConnectionGene> getInnovationMap(NetworkChromosome genome) {
        return genome.getConnections().stream()
                .collect(Collectors.toMap(ConnectionGene::getInnovationNumber, c -> c));
    }

    private void evaluatePopulation(Environment environment) {
        for (Species s : species) {
            for (NetworkChromosome member : s.getMembers()) {
                double fitness = environment.evaluate(member);
                member.setFitness(fitness);
            }
            s.calculateAverageFitness();
        }
    }

    private void evolve() {
        List<NetworkChromosome> newPopulation = new ArrayList<>();

        for (Species s : species) {
            s.getMembers().sort(Comparator.comparingDouble(NetworkChromosome::getFitness).reversed());
            int survivors = (int) Math.ceil(s.getMembers().size() * SURVIVAL_THRESHOLD);

            newPopulation.addAll(s.getMembers().subList(0, survivors));
        }

        while (newPopulation.size() < populationSize) {
            Species s = selectSpecies();
            NetworkChromosome child;

            if (random.nextDouble() < 0.8) {
                NetworkChromosome parent1 = selectParent(s);
                NetworkChromosome parent2 = selectParent(s);
                child = crossover.apply(parent1, parent2);
            } else {
                child = selectParent(s);
            }

            if (random.nextDouble() < WEIGHT_MUTATION_RATE) {
                child = mutation.mutateWeights(child);
            }
            if (random.nextDouble() < ADD_CONNECTION_RATE) {
                child = mutation.addConnection(child);
            }
            if (random.nextDouble() < ADD_NEURON_RATE) {
                child = mutation.addNeuron(child);
            }

            assignToSpecies(child);
        }

        population = newPopulation;
    }

    private Species selectSpecies() {
        double totalFitness = species.stream().mapToDouble(Species::getAverageFitness).sum();
        double dart = random.nextDouble() * totalFitness;
        double sum = 0;

        for (Species s : species) {
            sum += s.getAverageFitness();
            if (sum > dart) {
                return s;
            }
        }
        return species.get(species.size() - 1);
    }

    private NetworkChromosome selectParent(Species s) {
        List<NetworkChromosome> members = s.getMembers();
        return members.get(random.nextInt(members.size()));
    }

    private NetworkChromosome getBestChromosome() {
        return species.stream()
                .flatMap(s -> s.getMembers().stream())
                .max(Comparator.comparingDouble(NetworkChromosome::getFitness))
                .orElseThrow(() -> new IllegalStateException("No chromosomes found"));
    }
}
