package de.uni_passau.fim.se2.sbse.suite_minimisation.crossover;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.BitFlipMutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Pair;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OnePointCrossoverTest {

    @Test
    public void testCrossoverProducesValidOffspring() {
        boolean[] parent1Genes = {true, true, true, false, false};
        boolean[] parent2Genes = {false, false, false, true, true};
        
        OnePointCrossover crossover = new OnePointCrossover();
        BitFlipMutation mutation = new BitFlipMutation(0.0); // No mutation for this test
        
        TestSuiteChromosome parent1 = new TestSuiteChromosome(parent1Genes, mutation, crossover);
        TestSuiteChromosome parent2 = new TestSuiteChromosome(parent2Genes, mutation, crossover);
        
        Pair<TestSuiteChromosome> offspringPair = crossover.apply(parent1, parent2);
        boolean[] offspring1Genes = offspringPair.getFst().getGenes();
        boolean[] offspring2Genes = offspringPair.getSnd().getGenes();
        
        // Verify offspring genes are a combination of parent genes
        assertTrue(
            hasCombinedGenes(parent1Genes, parent2Genes, offspring1Genes) &&
            hasCombinedGenes(parent2Genes, parent1Genes, offspring2Genes)
        );
    }

    private boolean hasCombinedGenes(boolean[] genes1, boolean[] genes2, boolean[] offspringGenes) {
        int crossoverPoint = findCrossoverPoint(genes1, genes2, offspringGenes);
        return crossoverPoint > 0; // Valid crossover point found
    }

    private int findCrossoverPoint(boolean[] genes1, boolean[] genes2, boolean[] offspringGenes) {
        for (int i = 1; i < genes1.length; i++) {
            boolean before = true, after = true;
            for (int j = 0; j < i; j++) {
                before &= (offspringGenes[j] == genes1[j]);
            }
            for (int j = i; j < genes2.length; j++) {
                after &= (offspringGenes[j] == genes2[j]);
            }
            if (before && after) return i;
        }
        return -1;
    }
}
