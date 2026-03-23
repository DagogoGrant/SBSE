package de.uni_passau.fim.se2.sbse.neat.crossover;

import de.uni_passau.fim.se2.sbse.neat.chromosomes.NetworkChromosome;
import de.uni_passau.fim.se2.sbse.neat.chromosomes.ConnectionGene;
import de.uni_passau.fim.se2.sbse.neat.chromosomes.NeuronGene;

import java.util.*;
import static java.util.Objects.requireNonNull;

/**
 * A NEAT crossover operation that combines two parent chromosomes to produce a child chromosome.
 */
public class NeatCrossover implements Crossover<NetworkChromosome> {

    private final Random random;

    /**
     * Constructs a new NEAT crossover operator.
     *
     * @param random The random number generator to use for crossover.
     */
    public NeatCrossover(Random random) {
        this.random = requireNonNull(random);
    }

    /**
     * Applies the NEAT crossover operation to two parent chromosomes.
     * Genes are inherited from both parents based on their fitness and innovation numbers.
     *
     * @param parent1 The first parent chromosome.
     * @param parent2 The second parent chromosome.
     * @return A new child chromosome resulting from the crossover.
     */
    @Override
    public NetworkChromosome apply(NetworkChromosome parent1, NetworkChromosome parent2) {
        if (parent2.getFitness() > parent1.getFitness()) {
            NetworkChromosome temp = parent1;
            parent1 = parent2;
            parent2 = temp;
        }

        // Create maps of connection genes by innovation number for both parents
        Map<Integer, ConnectionGene> parent1Connections = createConnectionMap(parent1);
        Map<Integer, ConnectionGene> parent2Connections = createConnectionMap(parent2);

        // Use layers from the fitter parent
        Map<Double, List<NeuronGene>> childLayers = cloneLayers(parent1.getLayers());

        // Combine connections
        List<ConnectionGene> childConnections = combineConnections(parent1Connections, parent2Connections);

        return new NetworkChromosome(childLayers, childConnections);
    }

    /**
     * Creates a map of innovation numbers to connection genes from a given chromosome.
     *
     * @param chromosome The chromosome to extract connections from.
     * @return A map of innovation numbers to connection genes.
     */
    private Map<Integer, ConnectionGene> createConnectionMap(NetworkChromosome chromosome) {
        Map<Integer, ConnectionGene> connectionMap = new HashMap<>();
        for (ConnectionGene connection : chromosome.getConnections()) {
            connectionMap.put(connection.getInnovationNumber(), connection);
        }
        return connectionMap;
    }

    /**
     * Clones the layers from the fitter parent to avoid modifying the original structure.
     *
     * @param layers The layers to clone.
     * @return A new map containing cloned layers.
     */
    private Map<Double, List<NeuronGene>> cloneLayers(Map<Double, List<NeuronGene>> layers) {
        Map<Double, List<NeuronGene>> clonedLayers = new HashMap<>();
        for (Map.Entry<Double, List<NeuronGene>> entry : layers.entrySet()) {
            clonedLayers.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return clonedLayers;
    }

    /**
     * Combines connections from both parents based on matching, disjoint, and excess genes.
     *
     * @param parent1Connections The connection map of the fitter parent.
     * @param parent2Connections The connection map of the less fit parent.
     * @return A list of connection genes for the child chromosome.
     */
    private List<ConnectionGene> combineConnections(Map<Integer, ConnectionGene> parent1Connections,
                                                   Map<Integer, ConnectionGene> parent2Connections) {
        Set<Integer> allInnovations = new HashSet<>();
        allInnovations.addAll(parent1Connections.keySet());
        allInnovations.addAll(parent2Connections.keySet());

        List<ConnectionGene> childConnections = new ArrayList<>();
        for (Integer innovation : allInnovations) {
            ConnectionGene conn1 = parent1Connections.get(innovation);
            ConnectionGene conn2 = parent2Connections.get(innovation);

            if (conn1 != null && conn2 != null) {
                // Matching gene: inherit randomly with interpolated weight
                childConnections.add(inheritMatchingGene(conn1, conn2));
            } else if (conn1 != null) {
                // Disjoint/excess gene from fitter parent: inherit with slight mutation
                childConnections.add(mutateConnection(conn1));
            }
            // Disjoint/excess genes from less fit parent are ignored
        }

        return childConnections;
    }

    /**
     * Inherits a matching gene with an interpolated weight between the two parents.
     *
     * @param conn1 The connection gene from the first parent.
     * @param conn2 The connection gene from the second parent.
     * @return A new connection gene with an interpolated weight.
     */
    private ConnectionGene inheritMatchingGene(ConnectionGene conn1, ConnectionGene conn2) {
        double newWeight = conn1.getWeight() + (conn2.getWeight() - conn1.getWeight()) * random.nextDouble();
        return new ConnectionGene(
                conn1.getSourceNeuron(),
                conn1.getTargetNeuron(),
                newWeight,
                conn1.getEnabled(),
                conn1.getInnovationNumber()
        );
    }

    /**
     * Mutates the weight of a connection gene from the fitter parent.
     *
     * @param connection The connection gene to mutate.
     * @return A new connection gene with a mutated weight.
     */
    private ConnectionGene mutateConnection(ConnectionGene connection) {
        double mutatedWeight = connection.getWeight() * (1 + (random.nextGaussian() * 0.1));
        return new ConnectionGene(
                connection.getSourceNeuron(),
                connection.getTargetNeuron(),
                mutatedWeight,
                connection.getEnabled(),
                connection.getInnovationNumber()
        );
    }
}
