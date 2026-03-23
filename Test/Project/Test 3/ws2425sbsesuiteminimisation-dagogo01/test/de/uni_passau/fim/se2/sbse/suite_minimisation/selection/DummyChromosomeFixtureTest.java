package de.uni_passau.fim.se2.sbse.suite_minimisation.selection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for DummyChromosomeFixture.
 */
public class DummyChromosomeFixtureTest {

    @Test
    void testMutation() {
        DummyChromosomeFixture original = new DummyChromosomeFixture(2.0, 3.0);
        DummyChromosomeFixture mutated = DummyChromosomeFixture.dummyMutation().apply(original); // Use fully qualified method call
    
        assertEquals(3.0, mutated.getSizeFitness(), "Size fitness should be incremented.");
        assertEquals(4.0, mutated.getCoverageFitness(), "Coverage fitness should be incremented.");
    }
    


}
