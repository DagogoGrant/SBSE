package de.uni_passau.fim.se2.sbse.suite_minimisation.mutation;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Randomness;

import java.util.Random;

public class BitFlipMutation implements Mutation<TestSuiteChromosome> {

    @Override
    public TestSuiteChromosome apply(TestSuiteChromosome parent) {
        boolean[] parentSuite = parent.getTestSuite();
        boolean[] mutatedSuite = parentSuite.clone();
        Random random = Randomness.random();

        // Flip a random bit
        int mutationIndex = random.nextInt(mutatedSuite.length);
        mutatedSuite[mutationIndex] = !mutatedSuite[mutationIndex];

        // Ensure at least one test case is included
        boolean atLeastOneIncluded = false;
        for (boolean included : mutatedSuite) {
            if (included) {
                atLeastOneIncluded = true;
                break;
            }
        }
        if (!atLeastOneIncluded) {
            mutatedSuite[mutationIndex] = true; // Ensure the mutated bit is included
        }

        // Return the mutated chromosome
        return new TestSuiteChromosome(mutatedSuite, parent.getMutation(), parent.getCrossover());
    }
}
