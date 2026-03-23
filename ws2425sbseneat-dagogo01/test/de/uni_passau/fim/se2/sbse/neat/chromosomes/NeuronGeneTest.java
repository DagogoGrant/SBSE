package de.uni_passau.fim.se2.sbse.neat.chromosomes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NeuronGeneTest {
    private NeuronGene neuronSigmoid;
    private NeuronGene neuronTanh;
    private NeuronGene neuronNone;

    @BeforeEach
    void setUp() {
        neuronSigmoid = new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.HIDDEN);
        neuronTanh = new NeuronGene(2, ActivationFunction.TANH, NeuronType.HIDDEN);
        neuronNone = new NeuronGene(3, ActivationFunction.NONE, NeuronType.INPUT);
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(1, neuronSigmoid.getId());
        assertEquals(NeuronType.HIDDEN, neuronSigmoid.getNeuronType());
        
        assertEquals(2, neuronTanh.getId());
        assertEquals(NeuronType.HIDDEN, neuronTanh.getNeuronType());
        
        assertEquals(3, neuronNone.getId());
        assertEquals(NeuronType.INPUT, neuronNone.getNeuronType());
    }

    @Test
    void testSetValueAndGetValue() {
        neuronSigmoid.setValue(0.5);
        assertEquals(0.5, neuronSigmoid.getValue(), 0.0001);

        neuronTanh.setValue(-1.5);
        assertEquals(-1.5, neuronTanh.getValue(), 0.0001);
    }

    @Test
    void testActivateWithSigmoid() {
        double input = 0.0;
        double output = neuronSigmoid.activate(input);
        assertEquals(0.5, output, 0.0001);

        input = 1.0;
        output = neuronSigmoid.activate(input);
        assertTrue(output > 0.5 && output < 1.0);
    }

    @Test
    void testActivateWithTanh() {
        double input = 0.0;
        double output = neuronTanh.activate(input);
        assertEquals(0.0, output, 0.0001);

        input = 1.0;
        output = neuronTanh.activate(input);
        assertTrue(output > 0.0 && output < 1.0);
    }

    @Test
    void testActivateWithNone() {
        double input = 1.5;
        double output = neuronNone.activate(input);
        assertEquals(1.5, output, 0.0001);

        input = -2.0;
        output = neuronNone.activate(input);
        assertEquals(-2.0, output, 0.0001);
    }

    @Test
    void testEqualsAndHashCode() {
        NeuronGene neuron1 = new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT);
        NeuronGene neuron2 = new NeuronGene(1, ActivationFunction.TANH, NeuronType.HIDDEN);
        NeuronGene neuron3 = new NeuronGene(2, ActivationFunction.NONE, NeuronType.OUTPUT);

        // Test equals
        assertEquals(neuronSigmoid, neuron1);  // Same ID should be equal
        assertNotEquals(neuronSigmoid, neuron3);  // Different ID should not be equal
        assertNotEquals(neuronSigmoid, null);  // Null check
        assertNotEquals(neuronSigmoid, new Object());  // Type check

        // Test hashCode
        assertEquals(neuronSigmoid.hashCode(), neuron1.hashCode());
        assertNotEquals(neuronSigmoid.hashCode(), neuron3.hashCode());
    }
}
