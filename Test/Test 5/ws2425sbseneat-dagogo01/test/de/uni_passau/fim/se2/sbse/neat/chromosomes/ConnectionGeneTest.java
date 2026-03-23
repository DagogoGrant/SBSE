package de.uni_passau.fim.se2.sbse.neat.chromosomes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConnectionGeneTest {
    private NeuronGene sourceNeuron;
    private NeuronGene targetNeuron;
    private ConnectionGene connection;

    @BeforeEach
    void setUp() {
        sourceNeuron = new NeuronGene(1, ActivationFunction.NONE, NeuronType.INPUT);
        targetNeuron = new NeuronGene(2, ActivationFunction.SIGMOID, NeuronType.OUTPUT);
        connection = new ConnectionGene(sourceNeuron, targetNeuron, 0.5, true, 1);
    }

    @Test
    void testConstructorAndGetters() {
        // Verify that the constructor initializes all fields correctly
        assertEquals(sourceNeuron, connection.getSourceNeuron());
        assertEquals(targetNeuron, connection.getTargetNeuron());
        assertEquals(0.5, connection.getWeight(), 0.0001);
        assertTrue(connection.getEnabled());
        assertEquals(1, connection.getInnovationNumber());
    }

    @Test
    void testEqualsAndHashCode() {
        // Create new connections with the same and different innovation numbers
        ConnectionGene connection2 = new ConnectionGene(sourceNeuron, targetNeuron, -0.5, false, 1);
        ConnectionGene connection3 = new ConnectionGene(sourceNeuron, targetNeuron, 0.5, true, 2);

        // Test equality based on the innovation number
        assertEquals(connection, connection2);  // Same innovation number, should be equal
        assertNotEquals(connection, connection3);  // Different innovation number, should not be equal
        assertNotEquals(connection, null);  // Should not be equal to null
        assertNotEquals(connection, new Object());  // Should not be equal to an object of a different class

        // Test hashCode consistency
        assertEquals(connection.hashCode(), connection2.hashCode());
        assertNotEquals(connection.hashCode(), connection3.hashCode());
    }

    @Test
    void testDifferentSourceTargetNeurons() {
        // Create new neurons for testing different source and target combinations
        NeuronGene differentSource = new NeuronGene(3, ActivationFunction.NONE, NeuronType.INPUT);
        NeuronGene differentTarget = new NeuronGene(4, ActivationFunction.SIGMOID, NeuronType.OUTPUT);

        // Create connections with different source or target neurons
        ConnectionGene connection2 = new ConnectionGene(differentSource, targetNeuron, 0.5, true, 2);
        ConnectionGene connection3 = new ConnectionGene(sourceNeuron, differentTarget, 0.5, true, 3);

        // Ensure that connections with different neurons are not equal
        assertNotEquals(connection, connection2);
        assertNotEquals(connection, connection3);
    }

    @Test
    void testDisabledConnection() {
        // Create a connection with the enabled flag set to false
        ConnectionGene disabledConnection = new ConnectionGene(sourceNeuron, targetNeuron, 0.5, false, 1);

        // Ensure the enabled flag is set correctly
        assertFalse(disabledConnection.getEnabled());
    }
}
