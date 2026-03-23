package de.uni_passau.fim.se2.sbse.neat.chromosomes;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EdgeInnovationTest {

    @Test
    void testConstructorAndGetters() {
        EdgeInnovation innovation = new EdgeInnovation(1, 2, 100);
        assertEquals(1, innovation.getSourceNeuronId());
        assertEquals(2, innovation.getTargetNeuronId());
        assertEquals(100, innovation.getInnovationNumber());
    }

    @Test
    void testEqualsAndHashCode() {
        EdgeInnovation innovation1 = new EdgeInnovation(1, 2, 100);
        EdgeInnovation innovation2 = new EdgeInnovation(1, 2, 200);
        EdgeInnovation innovation3 = new EdgeInnovation(2, 1, 100);

        // Test equals
        assertEquals(innovation1, innovation2); // Same source/target should be equal
        assertNotEquals(innovation1, innovation3); // Different source/target should not be equal
        assertNotEquals(innovation1, null); // Null check
        assertNotEquals(innovation1, new Object()); // Type check

        // Test hashCode
        assertEquals(innovation1.hashCode(), innovation2.hashCode());
        assertNotEquals(innovation1.hashCode(), innovation3.hashCode());
    }

    @Test
    void testDifferentSourceTargetCombinations() {
        EdgeInnovation innovation1 = new EdgeInnovation(1, 2, 100);
        EdgeInnovation innovation2 = new EdgeInnovation(2, 3, 101);
        EdgeInnovation innovation3 = new EdgeInnovation(1, 3, 102);

        assertNotEquals(innovation1, innovation2);
        assertNotEquals(innovation1, innovation3);
        assertNotEquals(innovation2, innovation3);
    }

    @Test
    void testToString() {
        EdgeInnovation innovation = new EdgeInnovation(1, 2, 100);
        String expected = "EdgeInnovation{sourceNeuronId=1, targetNeuronId=2, innovationNumber=100}";
        assertEquals(expected, innovation.toString());
    }
}
