package de.uni_passau.fim.se2.sbse.neat.chromosomes;
import de.uni_passau.fim.se2.sbse.neat.algorithms.innovations.Innovation;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class NetworkGeneratorTest {
    private NetworkGenerator generator;
    private Set<Innovation> innovations;
    private Random random;

    @BeforeEach
    void setUp() {
        innovations = new HashSet<>();
        random = new Random(42);  // Use a fixed seed for predictable results
        generator = new NetworkGenerator(innovations, 2, 1, random);  // 2 input neurons and 1 output neuron
    }

    @Test
    void testGenerateCreatesNetworkChromosome() {
        NetworkChromosome network = generator.generate();
        
        // Check that the network is not null
        assertNotNull(network);

        // Verify that layers are created correctly
        Map<Double, List<NeuronGene>> layers = network.getLayers();
        assertEquals(2, layers.size());  // INPUT and OUTPUT layers

        // Verify input layer
        List<NeuronGene> inputLayer = layers.get(NetworkChromosome.INPUT_LAYER);
        assertEquals(3, inputLayer.size());  // 2 input neurons + 1 bias neuron

        // Verify output layer
        List<NeuronGene> outputLayer = layers.get(NetworkChromosome.OUTPUT_LAYER);
        assertEquals(1, outputLayer.size());  // 1 output neuron

        // Verify connections
        List<ConnectionGene> connections = network.getConnections();
        assertEquals(3, connections.size());  // Each input (including bias) is connected to the output
    }

    @Test
    void testGenerateCreatesRandomWeights() {
        NetworkChromosome network = generator.generate();
        List<ConnectionGene> connections = network.getConnections();

        for (ConnectionGene connection : connections) {
            assertTrue(connection.getWeight() >= -1.0 && connection.getWeight() <= 1.0, 
                       "Weight should be between -1 and 1");
        }
    }

    @Test
    void testFindInnovationNumber() {
        NetworkGenerator generatorWithInnovations = new NetworkGenerator(innovations, 2, 1, random);
        
        // Ensure innovations were initialized
        assertFalse(innovations.isEmpty());

        EdgeInnovation expectedInnovation = (EdgeInnovation) innovations.iterator().next();
        int foundInnovationNumber = generatorWithInnovations.findInnovationNumber(
                expectedInnovation.getSourceNeuronId(), expectedInnovation.getTargetNeuronId()
        );

        assertEquals(expectedInnovation.getInnovationNumber(), foundInnovationNumber);
    }

    @Test
    void testCreateInputLayer() {
        Map<Double, List<NeuronGene>> layers = generator.generate().getLayers();
        List<NeuronGene> inputLayer = layers.get(NetworkChromosome.INPUT_LAYER);

        assertNotNull(inputLayer);
        assertEquals(3, inputLayer.size());  // 2 input neurons + 1 bias neuron
        assertEquals(NeuronType.INPUT, inputLayer.get(0).getNeuronType());
        assertEquals(NeuronType.BIAS, inputLayer.get(2).getNeuronType());
    }

    @Test
    void testCreateOutputLayer() {
        Map<Double, List<NeuronGene>> layers = generator.generate().getLayers();
        List<NeuronGene> outputLayer = layers.get(NetworkChromosome.OUTPUT_LAYER);

        assertNotNull(outputLayer);
        assertEquals(1, outputLayer.size());  // 1 output neuron
        assertEquals(NeuronType.OUTPUT, outputLayer.get(0).getNeuronType());
    }

    @Test
    void testCreateConnections() {
        NetworkChromosome network = generator.generate();
        List<ConnectionGene> connections = network.getConnections();

        // Ensure there are connections for each input neuron (including bias) to each output neuron
        assertEquals(3, connections.size());

        for (ConnectionGene connection : connections) {
            assertNotNull(connection.getSourceNeuron());
            assertNotNull(connection.getTargetNeuron());
            assertTrue(connection.getEnabled(), "Connections should be enabled by default");
        }
    }

    @Test
    void testInitializeInnovations() {
        NetworkGenerator generatorWithInnovations = new NetworkGenerator(new HashSet<>(), 2, 1, random);

        assertFalse(generatorWithInnovations.generate().getConnections().isEmpty(),
                "Innovations should be initialized and connections should exist");
    }

    @Test
    void testFindInnovationNumberThrowsExceptionWhenNotFound() {
        NetworkGenerator generatorWithEmptyInnovations = new NetworkGenerator(new HashSet<>(), 2, 1, random);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            generatorWithEmptyInnovations.findInnovationNumber(99, 100);
        });

        assertTrue(exception.getMessage().contains("Innovation not found"));
    }
}
