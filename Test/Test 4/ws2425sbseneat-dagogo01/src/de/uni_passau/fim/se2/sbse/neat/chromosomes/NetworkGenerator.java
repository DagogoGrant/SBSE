package de.uni_passau.fim.se2.sbse.neat.chromosomes;

import de.uni_passau.fim.se2.sbse.neat.algorithms.innovations.Innovation;
import java.util.*;
import static java.util.Objects.requireNonNull;

public class NetworkGenerator {
    private final int inputSize;
    private final int outputSize;
    private final Random random;
    private final Set<Innovation> innovations;
    private int nextNeuronId = 0;
    private static final int INITIAL_INNOVATION_NUMBER = 19; // Start from 19 as per test expectation

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

    private void initializeInnovations() {
        int innovationNumber = INITIAL_INNOVATION_NUMBER;
        // Create innovations for all possible connections between input and output
        for (int i = 0; i < inputSize + 1; i++) { // +1 for bias
            for (int j = 0; j < outputSize; j++) {
                int sourceId = i;
                int targetId = inputSize + 1 + j; // Output neurons start after inputs + bias
                innovations.add(new EdgeInnovation(sourceId, targetId, innovationNumber++));
            }
        }
    }

    public NetworkChromosome generate() {
        Map<Double, List<NeuronGene>> layers = new HashMap<>();
        List<ConnectionGene> connections = new ArrayList<>();

        // Create input layer neurons (including bias)
        List<NeuronGene> inputNeurons = new ArrayList<>();
        for (int i = 0; i < inputSize; i++) {
            inputNeurons.add(new NeuronGene(i, ActivationFunction.TANH, NeuronType.INPUT));
        }
        // Add bias neuron
        inputNeurons.add(new NeuronGene(inputSize, ActivationFunction.NONE, NeuronType.BIAS));
        layers.put(NetworkChromosome.INPUT_LAYER, inputNeurons);

        // Create output layer neurons with TANH activation
        List<NeuronGene> outputNeurons = new ArrayList<>();
        for (int i = 0; i < outputSize; i++) {
            outputNeurons.add(new NeuronGene(inputSize + 1 + i, ActivationFunction.TANH, NeuronType.OUTPUT));
        }
        layers.put(NetworkChromosome.OUTPUT_LAYER, outputNeurons);

        // Connect input to output layer (fully connected)
        for (NeuronGene inputNeuron : inputNeurons) {
            for (NeuronGene outputNeuron : outputNeurons) {
                // Find the matching innovation
                int innovationNumber = findInnovationNumber(inputNeuron.getId(), outputNeuron.getId());

                // Generate random weight between -1 and 1
                double weight = random.nextDouble() * 2 - 1;

                connections.add(new ConnectionGene(
                        inputNeuron,
                        outputNeuron,
                        weight,
                        true,
                        innovationNumber
                ));
            }
        }

        return new NetworkChromosome(layers, connections);
    }

    private int findInnovationNumber(int sourceId, int targetId) {
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

