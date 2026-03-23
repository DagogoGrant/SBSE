package de.uni_passau.fim.se2.sbse.suite_minimisation.mutation;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class BitFlipMutationTest {

    @Test
    void testMutationFlipsOneBitAndEnsuresValidity() {
        // Scenario: Valid parent chromosome
        boolean[] genes = {true, false, true, false};
        TestSuiteChromosome parent = new TestSuiteChromosome(genes);
        BitFlipMutation mutation = new BitFlipMutation(new Random(0));

        TestSuiteChromosome offspring = mutation.apply(parent);

        // Check that only one bit is flipped
        int changes = 0;
        for (int i = 0; i < genes.length; i++) {
            if (genes[i] != offspring.getGenes()[i]) {
                changes++;
            }
        }
        assertEquals(1, changes, "Mutation should flip exactly one bit");

        // Check that the offspring is valid
        assertTrue(offspring.isValid(), "Mutation should ensure offspring is valid");
    }

    @Test
    void testMutationWithInvalidParentEnsuresValidity() {
        // Scenario: Invalid parent chromosome (all false genes)
        boolean[] genes = {false, false, false, false};
        TestSuiteChromosome parent = new TestSuiteChromosome(genes);
        BitFlipMutation mutation = new BitFlipMutation(new Random(0));

        TestSuiteChromosome offspring = mutation.apply(parent);

        // Check that the offspring is valid
        assertTrue(offspring.isValid(), "Mutation should ensure offspring is valid");

        // Verify that exactly one bit was flipped
        int changes = 0;
        for (int i = 0; i < genes.length; i++) {
            if (genes[i] != offspring.getGenes()[i]) {
                changes++;
            }
        }
        assertEquals(1, changes, "Mutation should flip exactly one bit from an invalid chromosome");
    }
}
