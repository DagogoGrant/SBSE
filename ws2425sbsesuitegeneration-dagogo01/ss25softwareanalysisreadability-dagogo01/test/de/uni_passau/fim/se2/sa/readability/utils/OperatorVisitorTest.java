package de.uni_passau.fim.se2.sa.readability.utils;

import com.github.javaparser.ast.body.BodyDeclaration;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OperatorVisitorTest {

    @Test
    public void testVisitVariableDeclarator() throws Exception {
        String code = "public void test() { int x = 5; }";
        BodyDeclaration<?> body = Parser.parseJavaSnippet(code);
        
        OperatorVisitor visitor = new OperatorVisitor();
        body.accept(visitor, null);
        
        assertTrue(visitor.getOperatorsPerMethod().containsKey(OperatorVisitor.OperatorType.ASSIGNMENT));
        assertTrue(visitor.getTotalOperators() > 0);
    }

    @Test
    public void testVisitAssignExpr() throws Exception {
        String code = "public void test() { int x; x = 5; }";
        BodyDeclaration<?> body = Parser.parseJavaSnippet(code);
        
        OperatorVisitor visitor = new OperatorVisitor();
        body.accept(visitor, null);
        
        assertTrue(visitor.getOperatorsPerMethod().containsKey(OperatorVisitor.OperatorType.ASSIGNMENT));
        assertTrue(visitor.getOperatorsPerMethod().get(OperatorVisitor.OperatorType.ASSIGNMENT) >= 2);
    }

    @Test
    public void testVisitBinaryExpr() throws Exception {
        String code = "public void test() { int result = 5 + 3; }";
        BodyDeclaration<?> body = Parser.parseJavaSnippet(code);
        
        OperatorVisitor visitor = new OperatorVisitor();
        body.accept(visitor, null);
        
        assertTrue(visitor.getOperatorsPerMethod().containsKey(OperatorVisitor.OperatorType.BINARY));
        assertTrue(visitor.getOperatorsPerMethod().containsKey(OperatorVisitor.OperatorType.ASSIGNMENT));
    }

    @Test
    public void testVisitUnaryExpr() throws Exception {
        String code = "public void test() { int x = 5; x++; }";
        BodyDeclaration<?> body = Parser.parseJavaSnippet(code);
        
        OperatorVisitor visitor = new OperatorVisitor();
        body.accept(visitor, null);
        
        assertTrue(visitor.getOperatorsPerMethod().containsKey(OperatorVisitor.OperatorType.UNARY));
    }

    @Test
    public void testVisitConditionalExpr() throws Exception {
        String code = "public void test() { int x = 5 > 3 ? 1 : 0; }";
        BodyDeclaration<?> body = Parser.parseJavaSnippet(code);
        
        OperatorVisitor visitor = new OperatorVisitor();
        body.accept(visitor, null);
        
        assertTrue(visitor.getOperatorsPerMethod().containsKey(OperatorVisitor.OperatorType.CONDITIONAL));
        assertTrue(visitor.getOperatorsPerMethod().containsKey(OperatorVisitor.OperatorType.BINARY));
    }

    @Test
    public void testVisitInstanceOfExpr() throws Exception {
        String code = "public void test(Object obj) { boolean b = obj instanceof String; }";
        BodyDeclaration<?> body = Parser.parseJavaSnippet(code);
        
        OperatorVisitor visitor = new OperatorVisitor();
        body.accept(visitor, null);
        
        assertTrue(visitor.getOperatorsPerMethod().containsKey(OperatorVisitor.OperatorType.TYPE_COMPARISON));
    }

    @Test
    public void testGetTotalOperators() throws Exception {
        String code = "public void test() { int x = 5; int y = x + 3; }";
        BodyDeclaration<?> body = Parser.parseJavaSnippet(code);
        
        OperatorVisitor visitor = new OperatorVisitor();
        body.accept(visitor, null);
        
        assertTrue(visitor.getTotalOperators() >= 3); // 2 assignments + 1 binary
    }

    @Test
    public void testGetUniqueOperatorCount() throws Exception {
        String code = "public void test() { int x = 5; int y = x + 3; }";
        BodyDeclaration<?> body = Parser.parseJavaSnippet(code);
        
        OperatorVisitor visitor = new OperatorVisitor();
        body.accept(visitor, null);
        
        assertTrue(visitor.getUniqueOperatorCount() >= 2); // ASSIGNMENT and BINARY
    }

    @Test
    public void testEmptyMethod() throws Exception {
        String code = "public void test() { }";
        BodyDeclaration<?> body = Parser.parseJavaSnippet(code);
        
        OperatorVisitor visitor = new OperatorVisitor();
        body.accept(visitor, null);
        
        assertEquals(0, visitor.getTotalOperators());
        assertEquals(0, visitor.getUniqueOperatorCount());
    }

    @Test
    public void testAllOperatorTypes() throws Exception {
        String code = """
            public void test(Object obj) {
                int x = 5;           // ASSIGNMENT
                int y = x + 3;       // ASSIGNMENT, BINARY
                y++;                 // UNARY
                boolean b = x > 0 ? true : false;  // ASSIGNMENT, BINARY, CONDITIONAL
                boolean c = obj instanceof String; // ASSIGNMENT, TYPE_COMPARISON
            }
            """;
        BodyDeclaration<?> body = Parser.parseJavaSnippet(code);
        
        OperatorVisitor visitor = new OperatorVisitor();
        body.accept(visitor, null);
        
        assertEquals(5, visitor.getUniqueOperatorCount()); // All 5 types should be present
        assertTrue(visitor.getOperatorsPerMethod().containsKey(OperatorVisitor.OperatorType.ASSIGNMENT));
        assertTrue(visitor.getOperatorsPerMethod().containsKey(OperatorVisitor.OperatorType.BINARY));
        assertTrue(visitor.getOperatorsPerMethod().containsKey(OperatorVisitor.OperatorType.UNARY));
        assertTrue(visitor.getOperatorsPerMethod().containsKey(OperatorVisitor.OperatorType.CONDITIONAL));
        assertTrue(visitor.getOperatorsPerMethod().containsKey(OperatorVisitor.OperatorType.TYPE_COMPARISON));
    }
    @Test
public void testBranchNewOperatorType() throws Exception {
    String code = "public void test() { boolean b = true ? false : true; }"; // Only CONDITIONAL
    BodyDeclaration<?> body = Parser.parseJavaSnippet(code);

    OperatorVisitor visitor = new OperatorVisitor(); // empty map at start
    body.accept(visitor, null);

    // Should insert a new entry (i.e., default value path hit)
    assertEquals(1, visitor.getOperatorsPerMethod().get(OperatorVisitor.OperatorType.CONDITIONAL));
}
@Test
public void testSecondCallToSameOperatorTriggersUpdateBranch() throws Exception {
    String code = "public void test() { int x = 1; int y = 2; }"; // Two ASSIGNMENTs
    BodyDeclaration<?> body = Parser.parseJavaSnippet(code);

    OperatorVisitor visitor = new OperatorVisitor();
    body.accept(visitor, null);

    // Two assignments → confirms the map already had the key for the second
    assertEquals(2, visitor.getOperatorsPerMethod().get(OperatorVisitor.OperatorType.ASSIGNMENT));
}
@Test
    public void testGetUniqueOperatorCount_WithZeroCounts() throws Exception {
        // Create a visitor and manually add zero counts to test the filter branch
        OperatorVisitor visitor = new OperatorVisitor();
        
        // First, add some operators normally
        String code = "public void test() { int x = 5; }"; // Only ASSIGNMENT
        BodyDeclaration<?> body = Parser.parseJavaSnippet(code);
        body.accept(visitor, null);
        
        // Now manually add zero counts to the map to test the filter
        // We need to access the map and add entries with 0 values
        visitor.getOperatorsPerMethod().put(OperatorVisitor.OperatorType.BINARY, 0);
        visitor.getOperatorsPerMethod().put(OperatorVisitor.OperatorType.UNARY, 0);
        visitor.getOperatorsPerMethod().put(OperatorVisitor.OperatorType.CONDITIONAL, 0);
        
        // Now the map has:
        // ASSIGNMENT: > 0 (should be counted)
        // BINARY: 0 (should NOT be counted)
        // UNARY: 0 (should NOT be counted) 
        // CONDITIONAL: 0 (should NOT be counted)
        
        int uniqueCount = visitor.getUniqueOperatorCount();
        
        // Should only count ASSIGNMENT (count > 0), not the others (count == 0)
        assertEquals(1, uniqueCount, "Should only count operator types with count > 0");
        
        // Verify the filter is working - total map size vs unique count
        assertTrue(visitor.getOperatorsPerMethod().size() > uniqueCount, 
                  "Map should contain more entries than unique count due to zero values");
    }

    @Test
    public void testGetUniqueOperatorCount_AllZeroCounts() throws Exception {
        // Test case where all counts are zero (edge case)
        OperatorVisitor visitor = new OperatorVisitor();
        
        // Manually add all operator types with zero counts
        visitor.getOperatorsPerMethod().put(OperatorVisitor.OperatorType.ASSIGNMENT, 0);
        visitor.getOperatorsPerMethod().put(OperatorVisitor.OperatorType.BINARY, 0);
        visitor.getOperatorsPerMethod().put(OperatorVisitor.OperatorType.UNARY, 0);
        visitor.getOperatorsPerMethod().put(OperatorVisitor.OperatorType.CONDITIONAL, 0);
        visitor.getOperatorsPerMethod().put(OperatorVisitor.OperatorType.TYPE_COMPARISON, 0);
        
        int uniqueCount = visitor.getUniqueOperatorCount();
        
        // All counts are 0, so filter should exclude all of them
        assertEquals(0, uniqueCount, "Should return 0 when all operator counts are 0");
        assertEquals(5, visitor.getOperatorsPerMethod().size(), "Map should still contain all 5 entries");
    }

    @Test
    public void testGetUniqueOperatorCount_MixedZeroAndPositive() throws Exception {
        // Test with a mix of zero and positive counts
        OperatorVisitor visitor = new OperatorVisitor();
        
        // Add some with positive counts
        String code = "public void test() { int x = 5 + 3; }"; // ASSIGNMENT + BINARY
        BodyDeclaration<?> body = Parser.parseJavaSnippet(code);
        body.accept(visitor, null);
        
        // Add others with zero counts
        visitor.getOperatorsPerMethod().put(OperatorVisitor.OperatorType.UNARY, 0);
        visitor.getOperatorsPerMethod().put(OperatorVisitor.OperatorType.CONDITIONAL, 0);
        visitor.getOperatorsPerMethod().put(OperatorVisitor.OperatorType.TYPE_COMPARISON, 0);
        
        int uniqueCount = visitor.getUniqueOperatorCount();
        
        // Should only count ASSIGNMENT and BINARY (positive counts)
        assertEquals(2, uniqueCount, "Should only count operator types with positive counts");
        assertEquals(5, visitor.getOperatorsPerMethod().size(), "Map should contain all 5 entries");
        
        // Verify specific counts
        assertTrue(visitor.getOperatorsPerMethod().get(OperatorVisitor.OperatorType.ASSIGNMENT) > 0);
        assertTrue(visitor.getOperatorsPerMethod().get(OperatorVisitor.OperatorType.BINARY) > 0);
        assertEquals(0, visitor.getOperatorsPerMethod().get(OperatorVisitor.OperatorType.UNARY));
        assertEquals(0, visitor.getOperatorsPerMethod().get(OperatorVisitor.OperatorType.CONDITIONAL));
        assertEquals(0, visitor.getOperatorsPerMethod().get(OperatorVisitor.OperatorType.TYPE_COMPARISON));
    }
}

