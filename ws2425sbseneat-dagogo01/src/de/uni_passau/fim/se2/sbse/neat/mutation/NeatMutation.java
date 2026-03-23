package de.uni_passau.fim.se2.sbse.neat.mutation;

import de.uni_passau.fim.se2.sbse.neat.algorithms.innovations.Innovation;
import de.uni_passau.fim.se2.sbse.neat.chromosomes.*;

import java.util.*;

import static java.util.Objects.requireNonNull;

/**
 * Implements the mutation operator for the Neat algorithm, which applies four types of mutations based on probabilities:
 * 1. Add a new neuron to the network.
 * 2. Add a new connection to the network.
 * 3. Mutate the weights of the connections in the network.
 * 4. Toggle the enabled status of a connection in the network.
 */
public class NeatMutation implements Mutation<NetworkChromosome> {

    /**
     * The random number generator to use.
     */
    private final Random random;

    /**
     * The list of innovations that occurred so far in the search.
     * Since Neat applies mutations that change the structure of the network,
     * the set of innovations must be updated appropriately.
     */
    private final Set<Innovation> innovations;

    private static final double ADD_NEURON_PROBABILITY = 0.03;
    private static final double ADD_CONNECTION_PROBABILITY = 0.05;
    private static final double MUTATE_WEIGHTS_PROBABILITY = 0.8;
    private static final double TOGGLE_CONNECTION_PROBABILITY = 0.1;

    /**
     * Constructs a new NeatMutation with the given random number generator and the list of innovations that occurred so far in the search.
     *
     * @param innovations The list of innovations that occurred so far in the search.
     * @param random      The random number generator.
     */
    public NeatMutation(Set<Innovation> innovations, Random random) {
        this.innovations = requireNonNull(innovations);
        this.random = requireNonNull(random);
    }

    /**
     * Applies mutation to the given network chromosome.
     * If a structural mutation is applied, no further non-structural mutations are applied.
     * Otherwise, the weights of the connections are mutated and/or the enabled status of a connection is toggled.
     *
     * @param parent The parent chromosome to mutate.
     * @return The mutated parent chromosome.
     */
    @Override
    public NetworkChromosome apply(NetworkChromosome parent) {
        // Try structural mutations first
        if (random.nextDouble() < ADD_NEURON_PROBABILITY) {
            return addNeuron(parent);
        }
        if (random.nextDouble() < ADD_CONNECTION_PROBABILITY) {
            return addConnection(parent);
        }

        // Apply non-structural mutations
        NetworkChromosome mutated = parent;
        if (random.nextDouble() < MUTATE_WEIGHTS_PROBABILITY) {
            mutated = mutateWeights(mutated);
        }
        if (random.nextDouble() < TOGGLE_CONNECTION_PROBABILITY) {
            mutated = toggleConnection(mutated);
        }
        return mutated;
    }

    /**
     * Adds a hidden neuron to the given network chromosome by splitting an existing connection.
     * The connection to be split is chosen randomly from the list of connections in the network chromosome.
     * The connection is disabled and two new connections are added to the network chromosome:
     * One connection with a weight of 1.0 from the source neuron of the split connection to the new hidden neuron,
     * and one connection with the weight of the split connection from the new hidden neuron to the target neuron of the split connection.
     * <p>
     * Since this mutation changes the structure of the network,
     * novel innovations for the new connections must be created if the same mutation has not occurred before.
     * If the same innovation has occurred before, the corresponding innovation numbers must be reused.
     *
     * @param parent The network chromosome to which the new neuron and connections will be added.
     * @return The mutated network chromosome.
     */
    public NetworkChromosome addNeuron(NetworkChromosome parent) {
        List<ConnectionGene> connections = parent.getConnections();
        if (connections.isEmpty()) {
            return new NetworkChromosome(
                    new HashMap<>(parent.getLayers()),
                    new ArrayList<>(connections)
            );
        }

        // Select a random enabled connection to split
        List<ConnectionGene> enabledConnections = connections.stream()
                .filter(ConnectionGene::getEnabled)
                .toList();
        if (enabledConnections.isEmpty()) {
            return new NetworkChromosome(
                    new HashMap<>(parent.getLayers()),
                    new ArrayList<>(connections)
            );
        }

        ConnectionGene connectionToSplit = enabledConnections.get(random.nextInt(enabledConnections.size()));
        NeuronGene sourceNeuron = connectionToSplit.getSourceNeuron();
        NeuronGene targetNeuron = connectionToSplit.getTargetNeuron();

        // Create new hidden neuron
        double newNeuronDepth = (getLayerDepth(parent, sourceNeuron) + getLayerDepth(parent, targetNeuron)) / 2.0;
        NeuronGene newNeuron = new NeuronGene(
                getNextNeuronId(parent),
                ActivationFunction.SIGMOID,
                NeuronType.HIDDEN
        );

        // Create new connections
        Map<Double, List<NeuronGene>> newLayers = new HashMap<>(parent.getLayers());
        newLayers.computeIfAbsent(newNeuronDepth, k -> new ArrayList<>()).add(newNeuron);

        List<ConnectionGene> newConnections = new ArrayList<>(connections);
        // Disable original connection
        newConnections.remove(connectionToSplit);
        newConnections.add(new ConnectionGene(
                connectionToSplit.getSourceNeuron(),
                connectionToSplit.getTargetNeuron(),
                connectionToSplit.getWeight(),
                false,
                connectionToSplit.getInnovationNumber()
        ));

        // Add new connections
        int innovationNumber1 = getNextInnovationNumber(sourceNeuron.getId(), newNeuron.getId());
        int innovationNumber2 = getNextInnovationNumber(newNeuron.getId(), targetNeuron.getId());

        newConnections.add(new ConnectionGene(sourceNeuron, newNeuron, 1.0, true, innovationNumber1));
        newConnections.add(new ConnectionGene(newNeuron, targetNeuron, connectionToSplit.getWeight(), true, innovationNumber2));

        return new NetworkChromosome(newLayers, newConnections);
    }

    /**
     * Adds a connection to the given network chromosome.
     * The source neuron of the connection is chosen randomly from the list of neurons in the network chromosome,
     * excluding output neurons.
     * The target neuron of the connection is chosen randomly from the list of neurons in the network chromosome,
     * excluding input and bias neurons.
     * The connection is added to the network chromosome with a random weight between -1.0 and 1.0.
     * The connection must not be recurrent.
     * <p>
     * Since this mutation changes the structure of the network,
     * novel innovations for the new connection must be created if the same mutation has not occurred before.
     * If the same innovation has occurred before, the corresponding innovation number must be reused.
     *
     * @param parent The network chromosome to which the new connection will be added.
     * @return The mutated network chromosome.
     */
    public NetworkChromosome addConnection(NetworkChromosome parent) {
        Map<Double, List<NeuronGene>> layers = parent.getLayers();
        List<ConnectionGene> connections = parent.getConnections();

        // Get all possible source and target neurons
        List<NeuronGene> possibleSources = new ArrayList<>();
        List<NeuronGene> possibleTargets = new ArrayList<>();

        // Collect all valid source and target neurons first
        for (Map.Entry<Double, List<NeuronGene>> entry : layers.entrySet()) {
            for (NeuronGene neuron : entry.getValue()) {
                if (neuron.getNeuronType() != NeuronType.OUTPUT) {
                    possibleSources.add(neuron);
                }
                if (neuron.getNeuronType() != NeuronType.INPUT && 
                    neuron.getNeuronType() != NeuronType.BIAS) {
                    possibleTargets.add(neuron);
                }
            }
        }

        // Create a list of all possible valid connections
        List<Pair<NeuronGene, NeuronGene>> validPairs = new ArrayList<>();
        for (NeuronGene source : possibleSources) {
            for (NeuronGene target : possibleTargets) {
                // Check if connection would be valid (not recurrent and doesn't exist)
                if (getLayerDepth(parent, source) < getLayerDepth(parent, target)) {
                    boolean connectionExists = connections.stream()
                        .anyMatch(c -> c.getSourceNeuron().equals(source) &&
                                c.getTargetNeuron().equals(target));
                    if (!connectionExists) {
                        validPairs.add(new Pair<>(source, target));
                    }
                }
            }
        }

        // If there are valid connections possible, add one
        if (!validPairs.isEmpty()) {
            Pair<NeuronGene, NeuronGene> chosen = validPairs.get(random.nextInt(validPairs.size()));
            NeuronGene source = chosen.getFirst();
            NeuronGene target = chosen.getSecond();

            // Create new connection
            int innovationNumber = getNextInnovationNumber(source.getId(), target.getId());
            ConnectionGene newConnection = new ConnectionGene(
                source,
                target,
                random.nextDouble() * 2.0 - 1.0,
                true,
                innovationNumber
            );

            List<ConnectionGene> newConnections = new ArrayList<>(connections);
            newConnections.add(newConnection);
            return new NetworkChromosome(new HashMap<>(layers), newConnections);
        }

        // If no valid connections are possible, return unchanged
        return new NetworkChromosome(new HashMap<>(layers), new ArrayList<>(connections));
    }

    /**
     * Mutates the weights of the connections in the given network chromosome.
     * The weight is mutated by adding gaussian noise to every weight in the network chromosome.
     *
     * @param parent The network chromosome to mutate.
     * @return The mutated network chromosome.
     */
    public NetworkChromosome mutateWeights(NetworkChromosome parent) {
        List<ConnectionGene> newConnections = new ArrayList<>();
        for (ConnectionGene connection : parent.getConnections()) {
            double newWeight = connection.getWeight();
            if (random.nextDouble() < 0.1) {
                // 10% chance of assigning a new random weight
                newWeight = random.nextDouble() * 4.0 - 2.0;
            } else {
                // 90% chance of perturbing the weight
                newWeight += random.nextGaussian() * 0.1;
            }
            newWeight = Math.max(-2.0, Math.min(2.0, newWeight));
            newConnections.add(new ConnectionGene(
                    connection.getSourceNeuron(),
                    connection.getTargetNeuron(),
                    newWeight,
                    connection.getEnabled(),
                    connection.getInnovationNumber()
            ));
        }
        return new NetworkChromosome(new HashMap<>(parent.getLayers()), newConnections);
    }

    /**
     * Toggles the enabled status of a random connection in the given network chromosome.
     *
     * @param parent The network chromosome to mutate.
     * @return The mutated network chromosome.
     */
    public NetworkChromosome toggleConnection(NetworkChromosome parent) {
        List<ConnectionGene> connections = parent.getConnections();
        if (connections.isEmpty()) {
            return new NetworkChromosome(
                    new HashMap<>(parent.getLayers()),
                    new ArrayList<>(connections)
            );
        }

        int index = random.nextInt(connections.size());
        ConnectionGene connection = connections.get(index);

        List<ConnectionGene> newConnections = new ArrayList<>(connections);
        newConnections.set(index, new ConnectionGene(
                connection.getSourceNeuron(),
                connection.getTargetNeuron(),
                connection.getWeight(),
                !connection.getEnabled(),
                connection.getInnovationNumber()
        ));

        return new NetworkChromosome(new HashMap<>(parent.getLayers()), newConnections);
    }

    private double getLayerDepth(NetworkChromosome network, NeuronGene neuron) {
        // First check if neuron exists in any layer
        for (Map.Entry<Double, List<NeuronGene>> entry : network.getLayers().entrySet()) {
            if (entry.getValue().contains(neuron)) {
                return entry.getKey();
            }
        }

        // If not found in layers, determine appropriate layer based on neuron type
        if (neuron.getNeuronType() == NeuronType.INPUT || neuron.getNeuronType() == NeuronType.BIAS) {
            return NetworkChromosome.INPUT_LAYER;
        } else if (neuron.getNeuronType() == NeuronType.OUTPUT) {
            return NetworkChromosome.OUTPUT_LAYER;
        }

        // For hidden neurons, calculate layer based on connections
        List<ConnectionGene> incomingConnections = network.getConnections().stream()
                .filter(conn -> conn.getTargetNeuron().equals(neuron))
                .toList();

        List<ConnectionGene> outgoingConnections = network.getConnections().stream()
                .filter(conn -> conn.getSourceNeuron().equals(neuron))
                .toList();

        double maxInputLayer = NetworkChromosome.INPUT_LAYER;
        double minOutputLayer = NetworkChromosome.OUTPUT_LAYER;

        // Process incoming connections
        if (!incomingConnections.isEmpty()) {
            for (ConnectionGene conn : incomingConnections) {
                NeuronGene source = conn.getSourceNeuron();
                if (source.getNeuronType() == NeuronType.INPUT || source.getNeuronType() == NeuronType.BIAS) {
                    maxInputLayer = Math.max(maxInputLayer, NetworkChromosome.INPUT_LAYER);
                } else if (source.getNeuronType() == NeuronType.OUTPUT) {
                    maxInputLayer = Math.max(maxInputLayer, NetworkChromosome.OUTPUT_LAYER);
                } else {
                    maxInputLayer = Math.max(maxInputLayer, NetworkChromosome.INPUT_LAYER + 1.0);
                }
            }
        }

        // Process outgoing connections
        if (!outgoingConnections.isEmpty()) {
            for (ConnectionGene conn : outgoingConnections) {
                NeuronGene target = conn.getTargetNeuron();
                if (target.getNeuronType() == NeuronType.OUTPUT) {
                    minOutputLayer = Math.min(minOutputLayer, NetworkChromosome.OUTPUT_LAYER);
                } else if (target.getNeuronType() == NeuronType.INPUT || target.getNeuronType() == NeuronType.BIAS) {
                    minOutputLayer = Math.min(minOutputLayer, NetworkChromosome.INPUT_LAYER);
                } else {
                    minOutputLayer = Math.min(minOutputLayer, NetworkChromosome.OUTPUT_LAYER - 1.0);
                }
            }
        }

        // If no connections or invalid ordering, place in middle layer
        if (incomingConnections.isEmpty() && outgoingConnections.isEmpty() || maxInputLayer >= minOutputLayer) {
            return (NetworkChromosome.INPUT_LAYER + NetworkChromosome.OUTPUT_LAYER) / 2.0;
        }

        // Place the neuron in a layer between its inputs and outputs
        return maxInputLayer + (minOutputLayer - maxInputLayer) / 2.0;
    }

    private int getNextNeuronId(NetworkChromosome network) {
        return network.getLayers().values().stream()
                .flatMap(List::stream)
                .mapToInt(NeuronGene::getId)
                .max()
                .orElse(0) + 1;
    }

    private int getNextInnovationNumber(int sourceId, int targetId) {
        EdgeInnovation newInnovation = new EdgeInnovation(sourceId, targetId, 0);

        // Check if this innovation already exists
        for (Innovation innovation : innovations) {
            if (innovation instanceof EdgeInnovation && innovation.equals(newInnovation)) {
                return ((EdgeInnovation) innovation).getInnovationNumber();
            }
        }

        // Create new innovation
        int newInnovationNumber = innovations.stream()
                .filter(i -> i instanceof EdgeInnovation)
                .mapToInt(i -> ((EdgeInnovation) i).getInnovationNumber())
                .max()
                .orElse(18) + 1;

        innovations.add(new EdgeInnovation(sourceId, targetId, newInnovationNumber));
        return newInnovationNumber;
    }

    // Helper class for pairing neurons
    private static class Pair<T, U> {
        private final T first;
        private final U second;

        public Pair(T first, U second) {
            this.first = first;
            this.second = second;
        }

        public T getFirst() {
            return first;
        }

        public U getSecond() {
            return second;
        }
    }
}

