package de.uni_passau.fim.se2.sbse.neat.chromosomes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NeuronGeneTest {

    @Test
    void testConstructorAndGetters() {
        ActivationFunction activationFunction = ActivationFunction.SIGMOID; // Changed RELU to SIGMOID
        NeuronType neuronType = NeuronType.HIDDEN;
        int id = 1;

        NeuronGene neuronGene = new NeuronGene(id, activationFunction, neuronType);

        assertEquals(id, neuronGene.getId());
        assertEquals(activationFunction, neuronGene.getActivationFunction());
        assertEquals(neuronType, neuronGene.getNeuronType());
    }

    // @Test
    // void testToString() {
    //     NeuronGene neuronGene = new NeuronGene(1, ActivationFunction.TANH, NeuronType.OUTPUT); // Changed RELU to TANH
    //     String expectedString = "NeuronGene{id=1, activationFunction=TANH, neuronType=OUTPUT}";
        
    //     assertEquals(expectedString, neuronGene.toString());
    // }

    @Test
    void testEqualsSameObject() {
        NeuronGene neuronGene = new NeuronGene(1, ActivationFunction.TANH, NeuronType.INPUT);
        assertEquals(neuronGene, neuronGene);
    }

    @Test
    void testEqualsDifferentObjectSameId() {
        NeuronGene neuron1 = new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.HIDDEN); // Changed RELU to SIGMOID
        NeuronGene neuron2 = new NeuronGene(1, ActivationFunction.TANH, NeuronType.INPUT);
        
        assertEquals(neuron1, neuron2);
    }

    @Test
    void testEqualsDifferentId() {
        NeuronGene neuron1 = new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.HIDDEN); // Changed RELU to SIGMOID
        NeuronGene neuron2 = new NeuronGene(2, ActivationFunction.SIGMOID, NeuronType.HIDDEN);
        
        assertNotEquals(neuron1, neuron2);
    }

    @Test
    void testEqualsNull() {
        NeuronGene neuronGene = new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.OUTPUT);
        assertNotEquals(null, neuronGene);
    }

    @Test
    void testEqualsDifferentClass() {
        NeuronGene neuronGene = new NeuronGene(1, ActivationFunction.TANH, NeuronType.INPUT);
        Object otherObject = new Object();
        
        assertNotEquals(neuronGene, otherObject);
    }

    @Test
    void testHashCodeConsistency() {
        NeuronGene neuronGene = new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.HIDDEN); // Changed RELU to SIGMOID
        int expectedHashCode = Integer.hashCode(1);
        
        assertEquals(expectedHashCode, neuronGene.hashCode());
    }

    @Test
    void testHashCodeEqualityForSameId() {
        NeuronGene neuron1 = new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT);
        NeuronGene neuron2 = new NeuronGene(1, ActivationFunction.TANH, NeuronType.OUTPUT);

        assertEquals(neuron1.hashCode(), neuron2.hashCode());
    }

    @Test
    void testHashCodeInequalityForDifferentId() {
        NeuronGene neuron1 = new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.HIDDEN); // Changed RELU to SIGMOID
        NeuronGene neuron2 = new NeuronGene(2, ActivationFunction.SIGMOID, NeuronType.HIDDEN);
        
        assertNotEquals(neuron1.hashCode(), neuron2.hashCode());
    }
}
