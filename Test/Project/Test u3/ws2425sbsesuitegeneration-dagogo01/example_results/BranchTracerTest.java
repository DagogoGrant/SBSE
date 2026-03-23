package de.uni_passau.fim.se2.sbse.suite_generation.instrumentation;



import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BranchTracerTest {

    @Test
    public void testTraceBranchDistance() {
        // Create a BranchTracer instance
        BranchTracer branchTracer = BranchTracer.getInstance();

        // Clear previous state (important for singleton class)
        branchTracer.clear();

        // Test Case 1: Add new branch distances
        branchTracer.traceBranchDistance(1, 5.0, 2, 3.0);

        // Assert initial state
        assertEquals(5.0, branchTracer.getDistances().get(1), "True branch distance should be 5.0");
        assertEquals(3.0, branchTracer.getDistances().get(2), "False branch distance should be 3.0");

        // Test Case 2: Update with smaller distances
        branchTracer.traceBranchDistance(1, 2.0, 2, 4.0);

        // Assert updated state
        assertEquals(2.0, branchTracer.getDistances().get(1), "True branch distance should be updated to 2.0");
        assertEquals(3.0, branchTracer.getDistances().get(2), "False branch distance should remain 3.0");

        // Test Case 3: Update with larger distances (should not change)
        branchTracer.traceBranchDistance(1, 6.0, 2, 7.0);

        // Assert no change in state
        assertEquals(2.0, branchTracer.getDistances().get(1), "True branch distance should remain 2.0");
        assertEquals(3.0, branchTracer.getDistances().get(2), "False branch distance should remain 3.0");
    }
}
