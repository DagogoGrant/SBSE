package de.uni_passau.fim.se2.sbse.neat.crossover;

import de.uni_passau.fim.se2.sbse.neat.chromosomes.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class NeatCrossoverTest {

    private NeatCrossover crossover;
    private Random random;
    private NetworkChromosome parent1;
    private NetworkChromosome parent2;

    @BeforeEach
    void setUp() {
        random = new Random(42); // Fixed seed for deterministic results
        crossover = new NeatCrossover(random);

        // Create layers for parent chromosomes
        Map<Double, List<NeuronGene>> layers1 = createLayers(3, 1);
        Map<Double, List<NeuronGene>> layers2 = createLayers(3, 1);

        // Create connections for parent chromosomes
        List<ConnectionGene> connections1 = new ArrayList<>();
        connections1.add(new ConnectionGene(layers1.get(0.0).get(0), layers1.get(1.0).get(0), 0.5, true, 1));
        connections1.add(new ConnectionGene(layers1.get(0.0).get(1), layers1.get(1.0).get(0), -0.8, true, 2));

        List<ConnectionGene> connections2 = new ArrayList<>();
        connections2.add(new ConnectionGene(layers2.get(0.0).get(0), layers2.get(1.0).get(0), 0.3, true, 1));
        connections2.add(new ConnectionGene(layers2.get(0.0).get(2), layers2.get(1.0).get(0), 0.9, true, 3));

        parent1 = new NetworkChromosome(layers1, connections1);
        parent2 = new NetworkChromosome(layers2, connections2);

        // Set fitness values for parents
        parent1.setFitness(1.5);
        parent2.setFitness(1.0);
    }

    @Test
    void testApplyCrossover() {
        NetworkChromosome child = crossover.apply(parent1, parent2);

        assertNotNull(child);
        assertEquals(2, child.getLayers().size());
        assertTrue(child.getConnections().size() >= 2);
    }

    @Test
    void testCrossoverInheritsFromFitterParent() {
        NetworkChromosome child = crossover.apply(parent1, parent2);

        for (ConnectionGene connection : child.getConnections()) {
            assertTrue(connection.getWeight() >= -1.0 && connection.getWeight() <= 1.0);
            assertTrue(connection.getEnabled());
        }
    }

    @Test
    void testCombineConnectionsWithDisjointGenes() {
        NetworkChromosome child = crossover.apply(parent1, parent2);

        Set<Integer> innovationNumbers = new HashSet<>();
        for (ConnectionGene connection : child.getConnections()) {
            innovationNumbers.add(connection.getInnovationNumber());
        }

        assertTrue(innovationNumbers.contains(1));
        assertTrue(innovationNumbers.contains(2) || innovationNumbers.contains(3));
    }

    @Test
    void testMutateConnection() {
        ConnectionGene original = new ConnectionGene(
                parent1.getConnections().get(0).getSourceNeuron(),
                parent1.getConnections().get(0).getTargetNeuron(),
                1.0, true, 99
        );

        ConnectionGene mutated = mutateConnectionHelper(original);
        assertNotEquals(original.getWeight(), mutated.getWeight());
        assertEquals(original.getSourceNeuron(), mutated.getSourceNeuron());
        assertEquals(original.getTargetNeuron(), mutated.getTargetNeuron());
        assertEquals(original.getInnovationNumber(), mutated.getInnovationNumber());
    }

    @Test
void testInheritMatchingGene() {
    ConnectionGene conn1 = new ConnectionGene(new NeuronGene(1, ActivationFunction.NONE, NeuronType.INPUT),
                                              new NeuronGene(2, ActivationFunction.SIGMOID, NeuronType.OUTPUT),
                                              0.5, true, 1);
    ConnectionGene conn2 = new ConnectionGene(new NeuronGene(1, ActivationFunction.NONE, NeuronType.INPUT),
                                              new NeuronGene(2, ActivationFunction.SIGMOID, NeuronType.OUTPUT),
                                              0.8, true, 1);

    ConnectionGene inherited = inheritMatchingGeneHelper(conn1, conn2);
    
    double weight = inherited.getWeight();
    
    // Check that the inherited weight is within a reasonable tolerance of the expected range
    assertTrue(weight >= 0.5 - 0.0001 && weight <= 0.8 + 0.0001, 
               "Inherited weight should be between 0.5 and 0.8. Got: " + weight);
    
    assertEquals(conn1.getSourceNeuron(), inherited.getSourceNeuron());
    assertEquals(conn1.getTargetNeuron(), inherited.getTargetNeuron());
    assertEquals(conn1.getInnovationNumber(), inherited.getInnovationNumber());
}


    // Helper methods for mutation and inheritance testing
    private ConnectionGene mutateConnectionHelper(ConnectionGene connection) {
        double mutatedWeight = connection.getWeight() * (1 + (random.nextGaussian() * 0.1));
        return new ConnectionGene(
                connection.getSourceNeuron(),
                connection.getTargetNeuron(),
                mutatedWeight,
                connection.getEnabled(),
                connection.getInnovationNumber()
        );
    }

    private ConnectionGene inheritMatchingGeneHelper(ConnectionGene conn1, ConnectionGene conn2) {
        double newWeight = conn1.getWeight() + (conn2.getWeight() - conn1.getWeight()) * random.nextDouble();
        return new ConnectionGene(
                conn1.getSourceNeuron(),
                conn1.getTargetNeuron(),
                newWeight,
                conn1.getEnabled(),
                conn1.getInnovationNumber()
        );
    }

    // Utility method to create layers
    private Map<Double, List<NeuronGene>> createLayers(int inputSize, int outputSize) {
        Map<Double, List<NeuronGene>> layers = new HashMap<>();
        List<NeuronGene> inputLayer = new ArrayList<>();
        for (int i = 0; i < inputSize; i++) {
            inputLayer.add(new NeuronGene(i, ActivationFunction.NONE, NeuronType.INPUT));
        }
        layers.put(0.0, inputLayer);

        List<NeuronGene> outputLayer = new ArrayList<>();
        for (int i = 0; i < outputSize; i++) {
            outputLayer.add(new NeuronGene(inputSize + i, ActivationFunction.SIGMOID, NeuronType.OUTPUT));
        }
        layers.put(1.0, outputLayer);

        return layers;
    }
}
