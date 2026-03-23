package de.uni_passau.fim.se2.sbse.suite_generation.instrumentation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BranchTest {

    @Test
    void testBranchConstructorValidId() {
        Branch branch = new Branch.Entry(1, "TestClass", "testMethod", "(I)V");
        assertEquals(1, branch.getId());
    }

    @Test
    void testBranchConstructorNegativeIdThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
            new Branch.Entry(-1, "TestClass", "testMethod", "(I)V")
        );
        assertEquals("ID must not be negative", exception.getMessage());
    }

    @Test
    void testEntryConstructorValidArguments() {
        Branch.Entry entry = new Branch.Entry(10, "TestClass", "testMethod", "(I)V");
        assertNotNull(entry);
        assertEquals("RootBranch(10) of TestClass:testMethod(I)V", entry.toString());
    }

    @Test
    void testEntryConstructorNullMethodNameThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
            new Branch.Entry(1, "TestClass", null, "(I)V")
        );
        assertEquals("Method name must not be null and not be blank", exception.getMessage());
    }

    @Test
    void testEntryConstructorBlankMethodNameThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
            new Branch.Entry(1, "TestClass", " ", "(I)V")
        );
        assertEquals("Method name must not be null and not be blank", exception.getMessage());
    }

    @Test
    void testEntryConstructorNullDescriptorThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
            new Branch.Entry(1, "TestClass", "testMethod", null)
        );
        assertEquals("Descriptor must not be null and not be blank", exception.getMessage());
    }

    @Test
    void testEntryConstructorBlankDescriptorThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
            new Branch.Entry(1, "TestClass", "testMethod", " ")
        );
        assertEquals("Descriptor must not be null and not be blank", exception.getMessage());
    }

    @Test
    void testEntryConstructorNullClassNameThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
            new Branch.Entry(1, null, "testMethod", "(I)V")
        );
        assertEquals("Class name must not be null and not be blank", exception.getMessage());
    }

    @Test
    void testEntryConstructorBlankClassNameThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
            new Branch.Entry(1, " ", "testMethod", "(I)V")
        );
        assertEquals("Class name must not be null and not be blank", exception.getMessage());
    }

    @Test
    void testDecisionConstructorValidArguments() {
        Branch.Node node = new Branch.Node(5, "TestClass");
        Branch.Decision decision = new Branch.Decision(20, node, true);
        assertNotNull(decision);
        assertEquals(node, decision.getNode());
        assertEquals("DecisionBranch(20) of TestClass:5:T", decision.toString());
    }

    @Test
    void testDecisionConstructorNullNodeThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
            new Branch.Decision(2, null, true)
        );
        assertEquals("Only non-null branch nodes permitted", exception.getMessage());
    }

    @Test
    void testNodeConstructorValidArguments() {
        Branch.Node node = new Branch.Node(12, "TestClass");
        assertNotNull(node);
        assertEquals(12, node.line());
        assertEquals("TestClass:12", node.toString());
    }

    @Test
    void testNodeConstructorNegativeLineThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
            new Branch.Node(-1, "TestClass")
        );
        assertEquals("Line number must be positive", exception.getMessage());
    }

    @Test
    void testNodeConstructorNullClassNameThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
            new Branch.Node(12, null)
        );
        assertEquals("Class name must not be null and not be blank", exception.getMessage());
    }

    @Test
    void testNodeConstructorBlankClassNameThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
            new Branch.Node(12, " ")
        );
        assertEquals("Class name must not be null and not be blank", exception.getMessage());
    }

    @Test
    void testBranchEqualityAndHashCode() {
        Branch.Entry entry1 = new Branch.Entry(100, "TestClass", "methodA", "(I)V");
        Branch.Entry entry2 = new Branch.Entry(100, "TestClass", "methodA", "(I)V");
        Branch.Entry entry3 = new Branch.Entry(200, "TestClass", "methodB", "(I)V");

        assertEquals(entry1, entry2);
        assertNotEquals(entry1, entry3);
        assertEquals(entry1.hashCode(), entry2.hashCode());
        assertNotEquals(entry1.hashCode(), entry3.hashCode());
    }

    @Test
    void testNodeEqualityAndHashCode() {
        Branch.Node node1 = new Branch.Node(15, "TestClass");
        Branch.Node node2 = new Branch.Node(15, "TestClass");
        Branch.Node node3 = new Branch.Node(30, "TestClass");

        assertEquals(node1, node2);
        assertNotEquals(node1, node3);
        assertEquals(node1.hashCode(), node2.hashCode());
        assertNotEquals(node1.hashCode(), node3.hashCode());
    }

    @Test
    void testBranchToString() {
        Branch.Entry entry = new Branch.Entry(10, "TestClass", "testMethod", "(I)V");
        assertEquals("RootBranch(10) of TestClass:testMethod(I)V", entry.toString());

        Branch.Node node = new Branch.Node(10, "TestClass");
        Branch.Decision decision = new Branch.Decision(20, node, false);
        assertEquals("DecisionBranch(20) of TestClass:10:F", decision.toString());
    }
}
