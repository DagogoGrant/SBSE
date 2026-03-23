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
     */
    private final Map<Double, List<NeuronGene>> layers;

    /**
     * Hosts all connections of the network.
     */
    private final List<ConnectionGene> connections;

    /**
     * The fitness of the chromosome.
     */
    private double fitness;

    /**
     * Creates a new network chromosome with the given layers and connections.
     *
     * @param layers      The layers of the network.
     * @param connections The connections of the network.
     */
    public NetworkChromosome(Map<Double, List<NeuronGene>> layers, List<ConnectionGene> connections) {
        if (!layers.containsKey(INPUT_LAYER) || !layers.containsKey(OUTPUT_LAYER)) {
            throw new IllegalArgumentException("Layers must include both input and output layers.");
        }
        this.layers = requireNonNull(layers, "Layers cannot be null");
        this.connections = requireNonNull(connections, "Connections cannot be null");
        this.fitness = 0.0;
    }

    /**
     * Returns the layers of the network.
     *
     * @return The layers of the network.
     */
    public Map<Double, List<NeuronGene>> getLayers() {
        return layers;
    }

    /**
     * Returns the connections of the network.
     *
     * @return The connections of the network.
     */
    public List<ConnectionGene> getConnections() {
        return connections;
    }

    /**
     * Activates the network with the given state and returns the output.
     *
     * @param state The input state to the network.
     * @return The output of the network.
     */
    @Override
    public List<Double> getOutput(List<Double> state) {
        // Get the input layer neurons
        List<NeuronGene> inputLayer = layers.get(INPUT_LAYER);

        // Debug log the size of the input layer and the state
        System.out.println("Input layer size: " + inputLayer.size());
        System.out.println("State size: " + state.size());

        // Check if the sizes match
        if (state.size() != inputLayer.size()) {
            throw new IllegalArgumentException(
                "State size (" + state.size() + ") must match the number of input neurons (" + inputLayer.size() + ")."
            );
        }

        // Store the activations of each neuron
        Map<Integer, Double> activations = new HashMap<>();

        // Initialize input layer activations
        for (int i = 0; i < inputLayer.size(); i++) {
            activations.put(inputLayer.get(i).getId(), state.get(i));
        }

        // Process each layer in order (hidden layers -> output layer)
        List<Double> outputValues = new ArrayList<>();
        layers.keySet().stream().sorted().forEach(layer -> {
            List<NeuronGene> neurons = layers.get(layer);
            for (NeuronGene neuron : neurons) {
                // Skip input layer, process only hidden and output layers
                if (layer != INPUT_LAYER) {
                    double sum = 0.0;

                    // Sum weighted inputs from enabled connections
                    for (ConnectionGene connection : connections) {
                        if (connection.getTargetNeuron().getId() == neuron.getId() && connection.getEnabled()) {
                            int sourceId = connection.getSourceNeuron().getId();
                            double weight = connection.getWeight();
                            sum += activations.getOrDefault(sourceId, 0.0) * weight;
                        }
                    }

                    // Handle bias neurons
                    if (neuron.getNeuronType() == NeuronType.BIAS) {
                        activations.put(neuron.getId(), 1.0); // Bias neurons always have activation = 1.0
                    } else {
                        // Apply activation function
                        double activation = applyActivationFunction(neuron.getActivationFunction(), sum);
                        activations.put(neuron.getId(), activation);
                    }
                }
            }

            // If this is the output layer, collect the outputs
            if (layer == OUTPUT_LAYER) {
                for (NeuronGene outputNeuron : neurons) {
                    outputValues.add(activations.get(outputNeuron.getId()));
                }
            }
        });

        // Debugging output
        System.out.println("Output activations: " + outputValues);

        return outputValues;
    }

    /**
     * Sets the fitness of the chromosome.
     *
     * @param fitness The fitness of the chromosome.
     */
    @Override
    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    /**
     * Returns the fitness of the chromosome.
     *
     * @return The fitness of the chromosome.
     */
    @Override
    public double getFitness() {
        return fitness;
    }

    /**
     * Applies the given activation function to the input value.
     *
     * @param activationFunction The activation function.
     * @param value              The input value.
     * @return The output value after applying the activation function.
     */
    private double applyActivationFunction(ActivationFunction activationFunction, double value) {
        switch (activationFunction) {
            case NONE:
                return value;
            case SIGMOID:
                return 1.0 / (1.0 + Math.exp(-value));
            case TANH:
                return Math.tanh(value);
            default:
                throw new IllegalArgumentException("Unknown activation function: " + activationFunction);
        }
    }
}
