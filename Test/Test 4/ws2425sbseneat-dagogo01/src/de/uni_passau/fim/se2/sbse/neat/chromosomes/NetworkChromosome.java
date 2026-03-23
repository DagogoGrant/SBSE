package de.uni_passau.fim.se2.sbse.neat.chromosomes;

import java.util.*;

/**
 * Represents a network chromosome in the NEAT algorithm.
 * This class manages layers of neurons and their connections and performs forward propagation.
 */
public class NetworkChromosome implements Agent {

    public static final double INPUT_LAYER = 0.0;
    public static final double OUTPUT_LAYER = 1.0;

    private final Map<Double, List<NeuronGene>> layers;
    private final List<ConnectionGene> connections;
    private double fitness;

    /**
     * Constructs a new network chromosome with the given layers and connections.
     *
     * @param layers      A map representing layers of neurons.
     * @param connections A list of connections between neurons.
     */
    public NetworkChromosome(Map<Double, List<NeuronGene>> layers, List<ConnectionGene> connections) {
        this.layers = new HashMap<>(Objects.requireNonNull(layers));
        this.connections = new ArrayList<>(Objects.requireNonNull(connections));
        validateNetwork();
    }

    /**
     * Ensures the network has both input and output layers.
     */
    private void validateNetwork() {
        if (!layers.containsKey(INPUT_LAYER) || !layers.containsKey(OUTPUT_LAYER)) {
            throw new IllegalArgumentException("The network must have both input and output layers.");
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
        validateInput(state);
        resetNeuronValues();
        initializeInputLayer(state);
        propagateNeuronValues();
        return extractOutputValues();
    }

    /**
     * Validates that the input state matches the number of input neurons.
     *
     * @param state The input state to be validated.
     */
    private void validateInput(List<Double> state) {
        int inputNeuronCount = (int) layers.get(INPUT_LAYER).stream()
                .filter(neuron -> neuron.getNeuronType() == NeuronType.INPUT)
                .count();

        if (state == null || state.size() != inputNeuronCount) {
            throw new IllegalArgumentException("Invalid input state size. Expected: " + inputNeuronCount);
        }
    }

    /**
     * Resets all neuron values to zero before processing.
     */
    private void resetNeuronValues() {
        layers.values().forEach(layer -> layer.forEach(neuron -> neuron.setValue(0.0)));
    }

    /**
     * Initializes the input layer neurons with the provided state.
     *
     * @param state The input state values.
     */
    private void initializeInputLayer(List<Double> state) {
        List<NeuronGene> inputNeurons = layers.get(INPUT_LAYER);
        for (int i = 0; i < state.size(); i++) {
            inputNeurons.get(i).setValue(state.get(i));
        }

        // Set the bias neuron to 1.0, if present
        inputNeurons.stream()
                .filter(neuron -> neuron.getNeuronType() == NeuronType.BIAS)
                .forEach(neuron -> neuron.setValue(1.0));
    }

    /**
     * Propagates values through the network from the input layer to the output layer.
     */
    private void propagateNeuronValues() {
        List<Double> sortedLayers = new ArrayList<>(layers.keySet());
        Collections.sort(sortedLayers);

        for (double layerDepth : sortedLayers) {
            if (layerDepth == INPUT_LAYER) continue;

            for (NeuronGene neuron : layers.get(layerDepth)) {
                double sum = connections.stream()
                        .filter(conn -> conn.getEnabled() && conn.getTargetNeuron().equals(neuron))
                        .mapToDouble(conn -> conn.getSourceNeuron().getValue() * conn.getWeight())
                        .sum();

                neuron.setValue(neuron.activate(sum));
            }
        }
    }

    /**
     * Extracts and returns the output values from the output layer neurons.
     *
     * @return A list of output values.
     */
    private List<Double> extractOutputValues() {
        List<NeuronGene> outputNeurons = layers.get(OUTPUT_LAYER);
        List<Double> outputValues = new ArrayList<>();
        outputNeurons.forEach(neuron -> outputValues.add(neuron.getValue()));
        return outputValues;
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
