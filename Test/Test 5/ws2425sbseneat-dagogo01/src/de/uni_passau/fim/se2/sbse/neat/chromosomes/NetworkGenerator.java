package de.uni_passau.fim.se2.sbse.neat.chromosomes;

import de.uni_passau.fim.se2.sbse.neat.algorithms.innovations.Innovation;
import java.util.*;
import static java.util.Objects.requireNonNull;

/**
 * Creates fully connected feed-forward neural networks consisting of one input and one output layer.
 */
public class NetworkGenerator {

    /**
     * The number of desired input neurons.
     */
    private final int inputSize;

    /**
     * The number of desired output neurons.
     */
    private final int outputSize;

    /**
     * The random number generator.
     */
    private final Random random;

    /**
     * The set of innovations that occurred so far in the search.
     * Novel innovations created during the generation of the network must be added to this set.
     */
    private final Set<Innovation> innovations;

    private static final int INITIAL_INNOVATION_NUMBER = 19; // Start from 19 as per test expectation

    /**
     * Creates a new network generator.
     *
     * @param innovations The set of innovations that occurred so far in the search.
     * @param inputSize   The number of desired input neurons.
     * @param outputSize  The number of desired output neurons.
     * @param random      The random number generator.
     * @throws NullPointerException if the random number generator is {@code null}.
     */
    public NetworkGenerator(Set<Innovation> innovations, int inputSize, int outputSize, Random random) {
        this.innovations = requireNonNull(innovations);
        this.inputSize = inputSize;
        this.outputSize = outputSize;
        this.random = requireNonNull(random);

        // Initialize innovation numbers if not already present
        if (innovations.isEmpty()) {
            initializeInnovations();
        }
    }

    /**
     * Generates a new fully connected feed-forward network chromosome.
     *
     * @return a new network chromosome.
     */
    public NetworkChromosome generate() {
        Map<Double, List<NeuronGene>> layers = createLayers();
        List<ConnectionGene> connections = createConnections(layers);
        return new NetworkChromosome(layers, connections);
    }

    private Map<Double, List<NeuronGene>> createLayers() {
        Map<Double, List<NeuronGene>> layers = new HashMap<>();
        layers.put(NetworkChromosome.INPUT_LAYER, createInputLayer());
        layers.put(NetworkChromosome.OUTPUT_LAYER, createOutputLayer());
        return layers;
    }

    private List<NeuronGene> createInputLayer() {
        List<NeuronGene> inputNeurons = new ArrayList<>();
        for (int i = 0; i < inputSize; i++) {
            inputNeurons.add(new NeuronGene(i, ActivationFunction.TANH, NeuronType.INPUT));
        }
        // Add bias neuron
        inputNeurons.add(new NeuronGene(inputSize, ActivationFunction.NONE, NeuronType.BIAS));
        return inputNeurons;
    }

    private List<NeuronGene> createOutputLayer() {
        List<NeuronGene> outputNeurons = new ArrayList<>();
        for (int i = 0; i < outputSize; i++) {
            outputNeurons.add(new NeuronGene(inputSize + 1 + i, ActivationFunction.TANH, NeuronType.OUTPUT));
        }
        return outputNeurons;
    }

    private List<ConnectionGene> createConnections(Map<Double, List<NeuronGene>> layers) {
        List<ConnectionGene> connections = new ArrayList<>();
        List<NeuronGene> inputNeurons = layers.get(NetworkChromosome.INPUT_LAYER);
        List<NeuronGene> outputNeurons = layers.get(NetworkChromosome.OUTPUT_LAYER);

        for (NeuronGene inputNeuron : inputNeurons) {
            for (NeuronGene outputNeuron : outputNeurons) {
                int innovationNumber = findInnovationNumber(inputNeuron.getId(), outputNeuron.getId());
                double weight = random.nextDouble() * 2 - 1; // Random weight between -1 and 1
                connections.add(new ConnectionGene(inputNeuron, outputNeuron, weight, true, innovationNumber));
            }
        }
        return connections;
    }

    private void initializeInnovations() {
        int innovationNumber = INITIAL_INNOVATION_NUMBER;
        for (int i = 0; i < inputSize + 1; i++) { // +1 for bias
            for (int j = 0; j < outputSize; j++) {
                int sourceId = i;
                int targetId = inputSize + 1 + j; // Output neurons start after inputs + bias
                innovations.add(new EdgeInnovation(sourceId, targetId, innovationNumber++));
            }
        }
    }

    public int findInnovationNumber(int sourceId, int targetId) {
        for (Innovation innovation : innovations) {
            if (innovation instanceof EdgeInnovation) {
                EdgeInnovation connInnovation = (EdgeInnovation) innovation;
                if (connInnovation.getSourceNeuronId() == sourceId &&
                        connInnovation.getTargetNeuronId() == targetId) {
                    return connInnovation.getInnovationNumber();
                }
            }
        }
        throw new IllegalStateException("Innovation not found for connection: " + sourceId + "-" + targetId);
    }
}

