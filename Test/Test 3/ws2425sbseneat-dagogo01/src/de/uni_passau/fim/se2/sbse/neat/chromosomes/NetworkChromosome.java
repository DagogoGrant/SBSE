package de.uni_passau.fim.se2.sbse.neat.chromosomes;

import java.util.*;

import static java.util.Objects.requireNonNull;

/**
 * Represents a network chromosome in the NEAT algorithm.
 */
public class NetworkChromosome implements Agent {

    public static final double INPUT_LAYER = 0;
    public static final double OUTPUT_LAYER = 1;

    /**
     * Maps the layer number to a list of neurons in that layer, with zero representing the input layer and one the output layer.
     * All hidden layers between the input and output layer are represented by values between zero and one.
     * For instance, if a new neuron gets added between the input and output layer, it might get the layer number 0.5.
     */
    private final Map<Double, List<NeuronGene>> layers;

    /**
     * Hosts all connections of the network.
     */
    private final List<ConnectionGene> connections;

    /**
     * The fitness of this network chromosome.
     */
    private double fitness;

    /**
     * Creates a new network chromosome with the given layers and connections.
     *
     * @param layers      The layers of the network.
     * @param connections The connections of the network.
     */
    public NetworkChromosome(Map<Double, List<NeuronGene>> layers, List<ConnectionGene> connections) {
        this.layers = new HashMap<>(requireNonNull(layers));
        this.connections = new ArrayList<>(requireNonNull(connections));
        validateNetwork();
    }

    private void validateNetwork() {
        if (!layers.containsKey(INPUT_LAYER) || !layers.containsKey(OUTPUT_LAYER)) {
            throw new IllegalArgumentException("Network must have input and output layers");
        }
    }

    public Map<Double, List<NeuronGene>> getLayers() {
        return new HashMap<>(layers);
    }

    public List<ConnectionGene> getConnections() {
        return new ArrayList<>(connections);
    }

    @Override
    public List<Double> getOutput(List<Double> state) {
        Map<Integer, Double> neuronOutputs = new HashMap<>();
    
        // Set input values, handling potential size mismatch
        List<NeuronGene> inputNeurons = layers.get(INPUT_LAYER);
        for (int i = 0; i < inputNeurons.size(); i++) {
            if (i < state.size()) {
                neuronOutputs.put(inputNeurons.get(i).getId(), state.get(i));
            } else {
                // If there are more input neurons than state values, set remaining to 0
                neuronOutputs.put(inputNeurons.get(i).getId(), 0.0);
            }
        }
    
        // Process hidden and output layers
        for (double layerDepth : new TreeSet<>(layers.keySet())) {
            if (layerDepth == INPUT_LAYER) continue;
    
            for (NeuronGene neuron : layers.get(layerDepth)) {
                double sum = 0.0;
                for (ConnectionGene connection : connections) {
                    if (connection.getEnabled() && connection.getTargetNeuron().getId() == neuron.getId()) {
                        Double sourceOutput = neuronOutputs.get(connection.getSourceNeuron().getId());
                        if (sourceOutput != null) {
                            sum += connection.getWeight() * sourceOutput;
                        }
                    }
                }
                double output = activateNeuron(neuron, sum);
                neuronOutputs.put(neuron.getId(), output);
            }
        }
    
        // Collect output values
        List<Double> outputs = new ArrayList<>();
        for (NeuronGene outputNeuron : layers.get(OUTPUT_LAYER)) {
            outputs.add(neuronOutputs.get(outputNeuron.getId()));
        }
    
        return outputs;
    }
    private double activateNeuron(NeuronGene neuron, double input) {
        switch (neuron.getActivationFunction()) {
            case SIGMOID:
                return 1 / (1 + Math.exp(-input));
            case TANH:
                return Math.tanh(input);
            case NONE:
            default:
                return input;
        }
    }

    @Override
    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    @Override
    public double getFitness() {
        return fitness;
    }

    @Override
    public String toString() {
        return "NetworkChromosome{" +
                "layers=" + layers +
                ", connections=" + connections +
                ", fitness=" + fitness +
                '}';
    }
}