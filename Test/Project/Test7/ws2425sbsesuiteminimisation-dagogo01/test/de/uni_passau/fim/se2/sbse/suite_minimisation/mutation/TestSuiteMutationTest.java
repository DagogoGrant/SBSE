package de.uni_passau.fim.se2.sbse.suite_minimisation.mutation;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.TestSuiteCrossover;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestSuiteMutationTest {

    private final TestSuiteMutation mutation = new TestSuiteMutation();
    private final TestSuiteCrossover crossover = new TestSuiteCrossover(); // Provide a valid crossover

    @Test
    void testMutationFlipsGene() {
        boolean[] genes = {true, false, true};
        TestSuiteChromosome parent = new TestSuiteChromosome(genes, mutation, crossover);

        TestSuiteChromosome offspring = mutation.apply(parent);

        assertNotSame(parent, offspring);
        assertEquals(genes.length, offspring.getGenes().length);

        // Ensure at least one gene is different
        boolean mutated = false;
        for (int i = 0; i < genes.length; i++) {
            if (genes[i] != offspring.getGenes()[i]) {
                mutated = true;
                break;
            }
        }
        assertTrue(mutated, "At least one gene should be flipped.");
    }

    @Test
    void testMutationEnsuresAtLeastOneTrue() {
        boolean[] genes = {false, false, false};
        TestSuiteChromosome parent = new TestSuiteChromosome(genes, mutation, crossover);

        TestSuiteChromosome offspring = mutation.apply(parent);

        assertTrue(hasAtLeastOneTrue(offspring.getGenes()), "At least one gene must remain true after mutation.");
    }

    @Test
    void testMutationWithAllTrueGenes() {
        boolean[] genes = {true, true, true};
        TestSuiteChromosome parent = new TestSuiteChromosome(genes, mutation, crossover);

        TestSuiteChromosome offspring = mutation.apply(parent);

        assertNotSame(parent, offspring);
        assertTrue(hasAtLeastOneTrue(offspring.getGenes()), "Mutation should not invalidate the chromosome.");
    }

    @Test
    void testMutationWithSingleGene() {
        boolean[] genes = {true};
        TestSuiteChromosome parent = new TestSuiteChromosome(genes, mutation, crossover);

        TestSuiteChromosome offspring = mutation.apply(parent);

        assertNotNull(offspring);
        assertEquals(genes.length, offspring.getGenes().length);
        assertTrue(hasAtLeastOneTrue(offspring.getGenes()), "Mutation should keep at least one true gene.");
    }

    private boolean hasAtLeastOneTrue(boolean[] genes) {
        for (boolean gene : genes) {
            if (gene) return true;
        }
        return false;
    }
}
