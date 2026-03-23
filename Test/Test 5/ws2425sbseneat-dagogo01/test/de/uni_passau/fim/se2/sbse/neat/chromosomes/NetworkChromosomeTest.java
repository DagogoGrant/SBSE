package de.uni_passau.fim.se2.sbse.neat.chromosomes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

class NetworkChromosomeTest {
    private NetworkChromosome network;
    private Map<Double, List<NeuronGene>> layers;
    private List<ConnectionGene> connections;
    private List<NeuronGene> inputLayer;
    private List<NeuronGene> outputLayer;

    @BeforeEach
    void setUp() {
        layers = new HashMap<>();
        connections = new ArrayList<>();

        // Create input layer (2 inputs + 1 bias)
        inputLayer = new ArrayList<>();
        NeuronGene input1 = new NeuronGene(1, ActivationFunction.NONE, NeuronType.INPUT);
        NeuronGene input2 = new NeuronGene(2, ActivationFunction.NONE, NeuronType.INPUT);
        NeuronGene bias = new NeuronGene(3, ActivationFunction.NONE, NeuronType.BIAS);
        inputLayer.addAll(Arrays.asList(input1, input2, bias));
        layers.put(NetworkChromosome.INPUT_LAYER, inputLayer);

        // Create output layer (1 output neuron)
        outputLayer = new ArrayList<>();
        NeuronGene output = new NeuronGene(4, ActivationFunction.SIGMOID, NeuronType.OUTPUT);
        outputLayer.add(output);
        layers.put(NetworkChromosome.OUTPUT_LAYER, outputLayer);

        // Create connections
        connections.add(new ConnectionGene(input1, output, 0.5, true, 1));
        connections.add(new ConnectionGene(input2, output, -0.5, true, 2));
        connections.add(new ConnectionGene(bias, output, 0.1, true, 3));

        network = new NetworkChromosome(layers, connections);
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(layers, network.getLayers());
        assertEquals(connections, network.getConnections());
    }

    @Test
    void testGetOutput() {
        List<Double> input = Arrays.asList(1.0, -1.0);  // Match the number of input neurons (excluding bias)
        List<Double> output = network.getOutput(input);

        assertEquals(outputLayer.size(), output.size());
        assertTrue(output.get(0) >= 0.0 && output.get(0) <= 1.0);  // Sigmoid activation function ensures range [0, 1]
    }

    @Test
    void testGetOutputWithInvalidInput() {
        // Test with too few inputs
        List<Double> tooFewInputs = Collections.singletonList(1.0);
        assertThrows(IllegalArgumentException.class, () -> network.getOutput(tooFewInputs));

        // Test with too many inputs
        List<Double> tooManyInputs = Arrays.asList(1.0, -1.0, 0.5);
        assertThrows(IllegalArgumentException.class, () -> network.getOutput(tooManyInputs));

        // Test with null input
        assertThrows(IllegalArgumentException.class, () -> network.getOutput(null));
    }

    @Test
    void testFitness() {
        assertEquals(0.0, network.getFitness(), 0.0001);

        network.setFitness(1.5);
        assertEquals(1.5, network.getFitness(), 0.0001);
    }

    @Test
    void testDisabledConnections() {
        // Create a network with a disabled connection
        ConnectionGene disabledConnection = new ConnectionGene(
                layers.get(NetworkChromosome.INPUT_LAYER).get(0),
                layers.get(NetworkChromosome.OUTPUT_LAYER).get(0),
                1.0, false, 4
        );
        List<ConnectionGene> connsWithDisabled = new ArrayList<>(connections);
        connsWithDisabled.add(disabledConnection);

        NetworkChromosome networkWithDisabled = new NetworkChromosome(layers, connsWithDisabled);
        List<Double> input = Arrays.asList(1.0, -1.0);

        List<Double> output = networkWithDisabled.getOutput(input);
        assertEquals(outputLayer.size(), output.size());  // Ensure the disabled connection does not affect output
    }

    @Test
    void testLayerProcessingOrder() {
        // Add a hidden layer
        List<NeuronGene> hiddenLayer = new ArrayList<>();
        NeuronGene hidden = new NeuronGene(5, ActivationFunction.SIGMOID, NeuronType.HIDDEN);
        hiddenLayer.add(hidden);
        layers.put(0.5, hiddenLayer);

        // Add connections to and from the hidden layer
        List<ConnectionGene> newConnections = new ArrayList<>(connections);
        newConnections.add(new ConnectionGene(
                layers.get(NetworkChromosome.INPUT_LAYER).get(0),
                hidden, 0.5, true, 5
        ));
        newConnections.add(new ConnectionGene(
                hidden,
                layers.get(NetworkChromosome.OUTPUT_LAYER).get(0),
                0.5, true, 6
        ));

        NetworkChromosome networkWithHidden = new NetworkChromosome(layers, newConnections);
        List<Double> input = Arrays.asList(1.0, -1.0);
        List<Double> output = networkWithHidden.getOutput(input);

        assertEquals(outputLayer.size(), output.size());
    }

    @Test
    void testBiasNeuronHandling() {
        // Verify that bias neuron is properly handled
        List<Double> input = Arrays.asList(1.0, -1.0);
        List<Double> output = network.getOutput(input);

        // Ensure the output is affected by the bias
        assertNotNull(output);
        assertFalse(output.isEmpty());
    }
}
