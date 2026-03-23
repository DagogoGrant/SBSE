package de.uni_passau.fim.se2.sbse.suite_minimisation.mutation;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.OnePointCrossover;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BitFlipMutationTest {

    @Test
    public void testMutationFlipsBits() {
        boolean[] genes = {true, false, true, false, true}; // Initial chromosome
        BitFlipMutation mutation = new BitFlipMutation(1.0); // 100% mutation rate
        
        TestSuiteChromosome parent = new TestSuiteChromosome(genes, mutation, new OnePointCrossover());
        TestSuiteChromosome offspring = mutation.apply(parent);
        
        boolean[] offspringGenes = offspring.getGenes();
        
        // Verify all bits are flipped
        for (int i = 0; i < genes.length; i++) {
            assertNotEquals(genes[i], offspringGenes[i]);
        }
    }

    @Test
    public void testNoMutationWhenRateIsZero() {
        boolean[] genes = {true, false, true, false, true};
        BitFlipMutation mutation = new BitFlipMutation(0.0); // 0% mutation rate
        
        TestSuiteChromosome parent = new TestSuiteChromosome(genes, mutation, new OnePointCrossover());
        TestSuiteChromosome offspring = mutation.apply(parent);
        
        // Verify genes remain unchanged
        assertArrayEquals(genes, offspring.getGenes());
    }
}
