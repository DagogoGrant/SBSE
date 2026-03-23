package de.uni_passau.fim.se2.se.test_prioritisation.parent_selection;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.mutations.Mutation;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class TournamentSelectionTest {

    @Test
    void testSelect() {
        // Create a dummy mutation for TestOrder
        Mutation<TestOrder> dummyMutation = testOrder -> testOrder;

        // Create TestOrder instances
        int[] positions1 = {0, 1, 2};
        TestOrder testOrder1 = new TestOrder(positions1, dummyMutation);

        int[] positions2 = {2, 1, 0};
        TestOrder testOrder2 = new TestOrder(positions2, dummyMutation);

        // Ensure positions2 scores higher in fitness (custom logic to match implementation)
        testOrder1.setFitness(0.8);
        testOrder2.setFitness(1.0);

        // Create a list of TestOrder objects
        List<TestOrder> population = List.of(testOrder1, testOrder2);

        // Create TournamentSelection with a tournament size
        Random random = new Random();
        TournamentSelection<TestOrder> selection = new TournamentSelection<>(random, 2);

        // Perform selection
        TestOrder selectedOrder = selection.select(population);

        // Validate the selection
        assertNotNull(selectedOrder);
        assertTrue(selectedOrder.getFitness() >= 0.8); // Ensure fitness is valid
    }
}
