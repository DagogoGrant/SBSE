package de.uni_passau.fim.se2.sbse.neat.chromosomes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionGeneTest {

    @Test
    void testConstructorAndGetters() {
        NeuronGene source = new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT);
        NeuronGene target = new NeuronGene(2, ActivationFunction.TANH, NeuronType.OUTPUT);
        double weight = 0.5;
        boolean enabled = true;
        int innovationNumber = 10;

        ConnectionGene connectionGene = new ConnectionGene(source, target, weight, enabled, innovationNumber);

        assertEquals(source, connectionGene.getSourceNeuron());
        assertEquals(target, connectionGene.getTargetNeuron());
        assertEquals(weight, connectionGene.getWeight());
        assertEquals(enabled, connectionGene.getEnabled());
        assertEquals(innovationNumber, connectionGene.getInnovationNumber());
    }

    @Test
    void testSetWeight() {
        NeuronGene source = new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT);
        NeuronGene target = new NeuronGene(2, ActivationFunction.TANH, NeuronType.OUTPUT);
        ConnectionGene connectionGene = new ConnectionGene(source, target, 0.5, true, 10);

        connectionGene.setWeight(1.2);
        assertEquals(1.2, connectionGene.getWeight());
    }

    @Test
    void testSetEnabled() {
        NeuronGene source = new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT);
        NeuronGene target = new NeuronGene(2, ActivationFunction.TANH, NeuronType.OUTPUT);
        ConnectionGene connectionGene = new ConnectionGene(source, target, 0.5, true, 10);

        connectionGene.setEnabled(false);
        assertFalse(connectionGene.getEnabled());

        connectionGene.setEnabled(true);
        assertTrue(connectionGene.getEnabled());
    }

    @Test
    void testEqualsSameObject() {
        NeuronGene source = new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT);
        NeuronGene target = new NeuronGene(2, ActivationFunction.TANH, NeuronType.OUTPUT);
        ConnectionGene connectionGene = new ConnectionGene(source, target, 0.5, true, 10);

        assertEquals(connectionGene, connectionGene);
    }

    @Test
    void testEqualsDifferentObjectSameInnovationNumber() {
        NeuronGene source1 = new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT);
        NeuronGene target1 = new NeuronGene(2, ActivationFunction.TANH, NeuronType.OUTPUT);
        ConnectionGene connection1 = new ConnectionGene(source1, target1, 0.5, true, 10);

        NeuronGene source2 = new NeuronGene(3, ActivationFunction.TANH, NeuronType.HIDDEN);
        NeuronGene target2 = new NeuronGene(4, ActivationFunction.SIGMOID, NeuronType.OUTPUT);
        ConnectionGene connection2 = new ConnectionGene(source2, target2, 0.8, false, 10);

        assertEquals(connection1, connection2); // Same innovation number -> should be equal
    }

    @Test
    void testEqualsDifferentInnovationNumber() {
        NeuronGene source1 = new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT);
        NeuronGene target1 = new NeuronGene(2, ActivationFunction.TANH, NeuronType.OUTPUT);
        ConnectionGene connection1 = new ConnectionGene(source1, target1, 0.5, true, 10);

        NeuronGene source2 = new NeuronGene(3, ActivationFunction.TANH, NeuronType.HIDDEN);
        NeuronGene target2 = new NeuronGene(4, ActivationFunction.SIGMOID, NeuronType.OUTPUT);
        ConnectionGene connection2 = new ConnectionGene(source2, target2, 0.8, false, 11);

        assertNotEquals(connection1, connection2); // Different innovation number -> should not be equal
    }

    @Test
    void testEqualsNull() {
        NeuronGene source = new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT);
        NeuronGene target = new NeuronGene(2, ActivationFunction.TANH, NeuronType.OUTPUT);
        ConnectionGene connectionGene = new ConnectionGene(source, target, 0.5, true, 10);

        assertNotEquals(null, connectionGene);
    }

    @Test
    void testEqualsDifferentClass() {
        NeuronGene source = new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT);
        NeuronGene target = new NeuronGene(2, ActivationFunction.TANH, NeuronType.OUTPUT);
        ConnectionGene connectionGene = new ConnectionGene(source, target, 0.5, true, 10);
        Object otherObject = new Object();

        assertNotEquals(connectionGene, otherObject);
    }

    @Test
    void testHashCodeConsistency() {
        NeuronGene source = new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT);
        NeuronGene target = new NeuronGene(2, ActivationFunction.TANH, NeuronType.OUTPUT);
        ConnectionGene connectionGene = new ConnectionGene(source, target, 0.5, true, 10);
        int expectedHashCode = Integer.hashCode(10);

        assertEquals(expectedHashCode, connectionGene.hashCode());
    }

    @Test
    void testHashCodeEqualityForSameInnovationNumber() {
        NeuronGene source1 = new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT);
        NeuronGene target1 = new NeuronGene(2, ActivationFunction.TANH, NeuronType.OUTPUT);
        ConnectionGene connection1 = new ConnectionGene(source1, target1, 0.5, true, 10);

        NeuronGene source2 = new NeuronGene(3, ActivationFunction.TANH, NeuronType.HIDDEN);
        NeuronGene target2 = new NeuronGene(4, ActivationFunction.SIGMOID, NeuronType.OUTPUT);
        ConnectionGene connection2 = new ConnectionGene(source2, target2, 0.8, false, 10);

        assertEquals(connection1.hashCode(), connection2.hashCode());
    }

    @Test
    void testHashCodeInequalityForDifferentInnovationNumber() {
        NeuronGene source1 = new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT);
        NeuronGene target1 = new NeuronGene(2, ActivationFunction.TANH, NeuronType.OUTPUT);
        ConnectionGene connection1 = new ConnectionGene(source1, target1, 0.5, true, 10);

        NeuronGene source2 = new NeuronGene(3, ActivationFunction.TANH, NeuronType.HIDDEN);
        NeuronGene target2 = new NeuronGene(4, ActivationFunction.SIGMOID, NeuronType.OUTPUT);
        ConnectionGene connection2 = new ConnectionGene(source2, target2, 0.8, false, 11);

        assertNotEquals(connection1.hashCode(), connection2.hashCode());
    }

    @Test
    void testToString() {
        NeuronGene source = new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT);
        NeuronGene target = new NeuronGene(2, ActivationFunction.TANH, NeuronType.OUTPUT);
        ConnectionGene connectionGene = new ConnectionGene(source, target, 0.5, true, 10);

        String expectedString = "ConnectionGene{sourceNeuronId=1, targetNeuronId=2, weight=0.5, enabled=true, innovationNumber=10}";
        assertEquals(expectedString, connectionGene.toString());
    }
}
