package de.uni_passau.fim.se2.sbse.neat.chromosomes;

import java.util.*;

import static java.util.Objects.requireNonNull;

/**
 * Represents a network chromosome in the NEAT algorithm.
 */
public class NetworkChromosome implements Agent {

    public static final double INPUT_LAYER = 0;
    public static final double OUTPUT_LAYER = 1;

    private final Map<Double, List<NeuronGene>> layers;
    private final List<ConnectionGene> connections;
    private double fitness;
    private double adjustedFitness;  // New field for adjusted fitness

    /**
     * Creates a new network chromosome with the given layers and connections.
     *
     * @param layers      The layers of the network.
     * @param connections The connections of the network.
     */
    public NetworkChromosome(Map<Double, List<NeuronGene>> layers, List<ConnectionGene> connections) {
        this.layers = new HashMap<>(layers);
        this.connections = new ArrayList<>(connections);
        this.fitness = 0.0;
    }

    public Map<Double, List<NeuronGene>> getLayers() {
        Map<Double, List<NeuronGene>> clonedLayers = new HashMap<>();
        for (Map.Entry<Double, List<NeuronGene>> entry : layers.entrySet()) {
            clonedLayers.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return clonedLayers;
    }
    

    public List<ConnectionGene> getConnections() {
        return connections;
    }

    @Override
    public List<Double> getOutput(List<Double> state) {
        validateInput(state);
        Map<NeuronGene, Double> neuronValues = initializeNeuronValues(state);
        processLayers(neuronValues);
        return collectOutputs(neuronValues);
    }

    private void validateInput(List<Double> state) {
        int inputNeuronCount = countNeuronsOfType(NeuronType.INPUT);
        if (state == null || state.size() != inputNeuronCount) {
            throw new IllegalArgumentException("Invalid input state size. Expected: " + inputNeuronCount + ", Got: " +
                    (state == null ? "null" : state.size()));
        }
    }

    private Map<NeuronGene, Double> initializeNeuronValues(List<Double> state) {
        Map<NeuronGene, Double> neuronValues = new HashMap<>();
        int inputIndex = 0;
        for (NeuronGene neuron : layers.get(INPUT_LAYER)) {
            if (neuron.getNeuronType() == NeuronType.INPUT) {
                neuronValues.put(neuron, state.get(inputIndex++));
            } else if (neuron.getNeuronType() == NeuronType.BIAS) {
                neuronValues.put(neuron, 1.0);
            }
        }
        return neuronValues;
    }

    private void processLayers(Map<NeuronGene, Double> neuronValues) {
        List<Double> sortedLayers = new ArrayList<>(layers.keySet());
        Collections.sort(sortedLayers);

        for (double layerDepth : sortedLayers) {
            if (layerDepth == INPUT_LAYER) continue;
            for (NeuronGene neuron : layers.get(layerDepth)) {
                double sum = calculateNeuronInput(neuron, neuronValues);
                neuronValues.put(neuron, neuron.activate(sum));
            }
        }
    }

    private double calculateNeuronInput(NeuronGene neuron, Map<NeuronGene, Double> neuronValues) {
        return connections.stream()
                .filter(conn -> conn.getEnabled() && conn.getTargetNeuron().equals(neuron))
                .mapToDouble(conn -> neuronValues.getOrDefault(conn.getSourceNeuron(), 0.0) * conn.getWeight())
                .sum();
    }

    private List<Double> collectOutputs(Map<NeuronGene, Double> neuronValues) {
        return layers.get(OUTPUT_LAYER).stream()
                .map(neuronValues::get)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    private int countNeuronsOfType(NeuronType type) {
        return (int) layers.get(INPUT_LAYER).stream()
                .filter(n -> n.getNeuronType() == type)
                .count();
    }
    
    public double getAdjustedFitness() {
        return adjustedFitness;
    }

    public void setAdjustedFitness(double adjustedFitness) {
        this.adjustedFitness = adjustedFitness;
    }

    @Override
    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    @Override
    public double getFitness() {
        return fitness;
    }

}

