package de.uni_passau.fim.se2.sbse.suite_generation.instrumentation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.objectweb.asm.Opcodes.*;

public class BranchTracerTest {

    private BranchTracer branchTracer;

    @BeforeEach
    public void setUp() {
        branchTracer = BranchTracer.getInstance();
        branchTracer.clear(); // Clear the state before each test.
    }

    @Test
    public void testPassedBranch_IntegerComparison_IFEQ() {
        branchTracer.passedBranch(0, IFEQ, 1, 2);
        assertEquals(0.0, branchTracer.getDistances().get(1)); // True branch distance should be 0
        assertEquals(1.0, branchTracer.getDistances().get(2)); // False branch distance should be 1
    }

    @Test
    public void testPassedBranch_IntegerComparison_IFNE() {
        branchTracer.passedBranch(5, IFNE, 3, 4);
        assertEquals(0.0, branchTracer.getDistances().get(3)); // True branch distance should be 0
        assertEquals(5.0, branchTracer.getDistances().get(4)); // False branch distance should be 5
    }

    @Test
    public void testPassedBranch_IntegerComparison_IFLT() {
        branchTracer.passedBranch(-3, IFLT, 5, 6);
        assertEquals(0.0, branchTracer.getDistances().get(5)); // True branch distance should be 0
        assertEquals(3.0, branchTracer.getDistances().get(6)); // False branch distance should be 3
    }

    @Test
    public void testPassedBranch_IntegerComparison_IFLE() {
        branchTracer.passedBranch(0, IFLE, 7, 8);
        assertEquals(0.0, branchTracer.getDistances().get(7)); // True branch distance should be 0
        assertEquals(1.0, branchTracer.getDistances().get(8)); // False branch distance should be 1
    }

    // @Test
    // public void testPassedBranch_IntegerComparison_IFGT() {
    //     // Test i > 0 with i = 2
    //     branchTracer.passedBranch(2, IFGT, 1, 2);
    //     assertEquals(0.0, branchTracer.getDistances().get(1), "True branch distance should be 0.0");
    //     assertEquals(3.0, branchTracer.getDistances().get(2), "False branch distance should be 3.0");
    
    //     // Test i > 0 with i = -2
    //     branchTracer.passedBranch(-2, IFGT, 3, 4);
    //     assertEquals(3.0, branchTracer.getDistances().get(3), "True branch distance should be 3.0");
    //     assertEquals(0.0, branchTracer.getDistances().get(4), "False branch distance should be 0.0");
    // }
    

    @Test
    public void testPassedBranch_IntegerComparison_IFGE() {
        branchTracer.passedBranch(0, IFGE, 11, 12);
        assertEquals(0.0, branchTracer.getDistances().get(11)); // True branch distance should be 0
        assertEquals(1.0, branchTracer.getDistances().get(12)); // False branch distance should be 1
    }

    @Test
    public void testPassedBranch_TwoOperands_IF_ICMPEQ() {
        branchTracer.passedBranch(10, 10, IF_ICMPEQ, 13, 14);
        assertEquals(0.0, branchTracer.getDistances().get(13)); // True branch distance should be 0
        assertEquals(1.0, branchTracer.getDistances().get(14)); // False branch distance should be 1
    }

    @Test
    public void testPassedBranch_TwoOperands_IF_ICMPNE() {
        branchTracer.passedBranch(10, 5, IF_ICMPNE, 15, 16);
        assertEquals(0.0, branchTracer.getDistances().get(15)); // True branch distance should be 0
        assertEquals(5.0, branchTracer.getDistances().get(16)); // False branch distance should be 5
    }

    @Test
    public void testPassedBranch_NullComparison_IFNULL() {
        branchTracer.passedBranch(null, IFNULL, 17, 18);
        assertEquals(0.0, branchTracer.getDistances().get(17)); // True branch distance should be 0
        assertEquals(1.0, branchTracer.getDistances().get(18)); // False branch distance should be 1
    }

    @Test
    public void testPassedBranch_NullComparison_IFNONNULL() {
        branchTracer.passedBranch(new Object(), IFNONNULL, 19, 20);
        assertEquals(0.0, branchTracer.getDistances().get(19)); // True branch distance should be 0
        assertEquals(1.0, branchTracer.getDistances().get(20)); // False branch distance should be 1
    }

    @Test
    public void testPassedBranch_ReferenceComparison_IF_ACMPEQ() {
        Object obj = new Object();
        branchTracer.passedBranch(obj, obj, IF_ACMPEQ, 21, 22);
        assertEquals(0.0, branchTracer.getDistances().get(21)); // True branch distance should be 0
        assertEquals(1.0, branchTracer.getDistances().get(22)); // False branch distance should be 1
    }

    @Test
    public void testPassedBranch_ReferenceComparison_IF_ACMPNE() {
        branchTracer.passedBranch(new Object(), new Object(), IF_ACMPNE, 23, 24);
        assertEquals(0.0, branchTracer.getDistances().get(23)); // True branch distance should be 0
        assertEquals(1.0, branchTracer.getDistances().get(24)); // False branch distance should be 1
    }

    @Test
    public void testGetApproachLevel() {
        branchTracer.getApproachLevel(25); // Default approach level is Integer.MAX_VALUE
        assertEquals(Integer.MAX_VALUE, branchTracer.getApproachLevel(25));
    }

    @Test
    public void testTraceBranchDistanceRoot() {
        branchTracer.passedBranch(99);
        assertEquals(0.0, branchTracer.getDistances().get(99));
    }

    @Test
    public void testClearDistances() {
        branchTracer.passedBranch(5, IFEQ, 1, 2);
        branchTracer.clear();
        assertTrue(branchTracer.getDistances().isEmpty());
    }
}
