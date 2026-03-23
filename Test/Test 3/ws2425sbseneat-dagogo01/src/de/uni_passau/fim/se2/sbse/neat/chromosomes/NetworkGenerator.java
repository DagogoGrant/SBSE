package de.uni_passau.fim.se2.sbse.neat.chromosomes;

import de.uni_passau.fim.se2.sbse.neat.algorithms.innovations.Innovation;
import de.uni_passau.fim.se2.sbse.neat.algorithms.innovations.ConcreteInnovation;

import java.util.*;

import static java.util.Objects.requireNonNull;

public class NetworkGenerator {

    private final int inputSize;
    private final int outputSize;
    private final Random random;
    private final Set<Innovation> innovations;
    private int nextInnovationNumber;

    public NetworkGenerator(Set<Innovation> innovations, int inputSize, int outputSize, Random random) {
        this.innovations = requireNonNull(innovations);
        this.inputSize = inputSize;
        this.outputSize = outputSize;
        this.random = requireNonNull(random);
        this.nextInnovationNumber = innovations.size() + 1;
    }

    public NetworkChromosome generate() {
        Map<Double, List<NeuronGene>> layers = new HashMap<>();
        List<ConnectionGene> connections = new ArrayList<>();

        // Generate input neurons (including bias)
        List<NeuronGene> inputNeurons = new ArrayList<>();
        for (int i = 0; i < inputSize; i++) {
            inputNeurons.add(new NeuronGene(i, ActivationFunction.NONE, NeuronType.INPUT));
        }
        // Add bias neuron
        inputNeurons.add(new NeuronGene(inputSize, ActivationFunction.NONE, NeuronType.BIAS));
        layers.put(NetworkChromosome.INPUT_LAYER, inputNeurons);

        // Generate output neurons
        List<NeuronGene> outputNeurons = new ArrayList<>();
        for (int i = 0; i < outputSize; i++) {
            outputNeurons.add(new NeuronGene(inputSize + 1 + i, ActivationFunction.SIGMOID, NeuronType.OUTPUT));
        }
        layers.put(NetworkChromosome.OUTPUT_LAYER, outputNeurons);

        // Generate connections
        for (NeuronGene inputNeuron : inputNeurons) {
            for (NeuronGene outputNeuron : outputNeurons) {
                int innovationNumber = findOrCreateInnovation(inputNeuron.getId(), outputNeuron.getId());

                ConnectionGene connection = new ConnectionGene(
                    inputNeuron,
                    outputNeuron,
                    random.nextDouble() * 2 - 1, // Random weight between -1 and 1
                    true,
                    innovationNumber
                );
                connections.add(connection);
            }
        }

        return new NetworkChromosome(layers, connections);
    }

    private int findOrCreateInnovation(int sourceId, int targetId) {
        for (Innovation innovation : innovations) {
            if (innovation instanceof ConcreteInnovation) {
                ConcreteInnovation concreteInnovation = (ConcreteInnovation) innovation;
                if (concreteInnovation.getSourceNeuronId() == sourceId && concreteInnovation.getTargetNeuronId() == targetId) {
                    return concreteInnovation.getInnovationNumber();
                }
            }
        }
        int innovationNumber = nextInnovationNumber++;
        ConcreteInnovation newInnovation = new ConcreteInnovation(sourceId, targetId, innovationNumber);
        innovations.add(newInnovation);
        return innovationNumber;
    }
}

