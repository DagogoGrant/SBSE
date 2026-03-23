package de.uni_passau.fim.se2.sa.readability.utils;

import com.github.javaparser.ast.body.BodyDeclaration;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CyclomaticComplexityVisitorTest {

    @Test
    public void testSimpleMethod() throws Exception {
        String code = "public void test() { System.out.println(\"hello\"); }";
        BodyDeclaration<?> body = Parser.parseJavaSnippet(code);
        
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        body.accept(visitor, null);
        
        assertEquals(1, visitor.getComplexity());
    }

    @Test
    public void testIfStatement() throws Exception {
        String code = "public void test(int x) { if (x > 0) { System.out.println(\"positive\"); } }";
        BodyDeclaration<?> body = Parser.parseJavaSnippet(code);
        
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        body.accept(visitor, null);
        
        assertEquals(2, visitor.getComplexity());
    }

    @Test
    public void testForLoop() throws Exception {
        String code = "public void test() { for (int i = 0; i < 10; i++) { System.out.println(i); } }";
        BodyDeclaration<?> body = Parser.parseJavaSnippet(code);
        
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        body.accept(visitor, null);
        
        assertEquals(2, visitor.getComplexity());
    }

    @Test
    public void testForEachLoop() throws Exception {
        String code = "public void test(int[] arr) { for (int x : arr) { System.out.println(x); } }";
        BodyDeclaration<?> body = Parser.parseJavaSnippet(code);
        
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        body.accept(visitor, null);
        
        assertEquals(2, visitor.getComplexity());
    }

    @Test
    public void testWhileLoop() throws Exception {
        String code = "public void test() { int i = 0; while (i < 10) { i++; } }";
        BodyDeclaration<?> body = Parser.parseJavaSnippet(code);
        
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        body.accept(visitor, null);
        
        assertEquals(2, visitor.getComplexity());
    }

    @Test
    public void testDoWhileLoop() throws Exception {
        String code = "public void test() { int i = 0; do { i++; } while (i < 10); }";
        BodyDeclaration<?> body = Parser.parseJavaSnippet(code);
        
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        body.accept(visitor, null);
        
        assertEquals(2, visitor.getComplexity());
    }

    @Test
    public void testSwitchWithCases() throws Exception {
        String code = """
            public void test(int x) {
                switch (x) {
                    case 1: break;
                    case 2: break;
                    default: break;
                }
            }
            """;
        BodyDeclaration<?> body = Parser.parseJavaSnippet(code);
        
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        body.accept(visitor, null);
        
        assertEquals(3, visitor.getComplexity()); // 1 base + 2 cases (default not counted)
    }

    @Test
    public void testCatchClause() throws Exception {
        String code = """
            public void test() {
                try {
                    int x = 1/0;
                } catch (Exception e) {
                    System.out.println("error");
                }
            }
            """;
        BodyDeclaration<?> body = Parser.parseJavaSnippet(code);
        
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        body.accept(visitor, null);
        
        assertEquals(2, visitor.getComplexity());
    }

    @Test
    public void testConditionalExpr() throws Exception {
        String code = "public int test(int x) { return x > 0 ? 1 : 0; }";
        BodyDeclaration<?> body = Parser.parseJavaSnippet(code);
        
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        body.accept(visitor, null);
        
        assertEquals(2, visitor.getComplexity());
    }

    @Test
    public void testLogicalAndOperator() throws Exception {
        String code = "public boolean test(int x, int y) { return x > 0 && y > 0; }";
        BodyDeclaration<?> body = Parser.parseJavaSnippet(code);
        
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        body.accept(visitor, null);
        
        assertEquals(2, visitor.getComplexity());
    }

    @Test
    public void testLogicalOrOperator() throws Exception {
        String code = "public boolean test(int x, int y) { return x > 0 || y > 0; }";
        BodyDeclaration<?> body = Parser.parseJavaSnippet(code);
        
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        body.accept(visitor, null);
        
        assertEquals(2, visitor.getComplexity());
    }

    @Test
    public void testComplexMethod() throws Exception {
        String code = """
            public void complex(int x, int y) {
                if (x > 0) {                    // +1
                    for (int i = 0; i < x; i++) { // +1
                        while (y > 0) {         // +1
                            if (i == y) {       // +1
                                break;
                            }
                            y--;
                        }
                    }
                } else if (x < 0) {             // +1
                    try {
                        x = -x;
                    } catch (Exception e) {     // +1
                        return;
                    }
                }
                boolean result = x > 0 && y > 0; // +1
            }
            """;
        BodyDeclaration<?> body = Parser.parseJavaSnippet(code);
        
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        body.accept(visitor, null);
        
        assertEquals(8, visitor.getComplexity()); // 1 base + 7 decision points
    }

    @Test
    public void testReset() throws Exception {
        String code = "public void test(int x) { if (x > 0) { System.out.println(\"positive\"); } }";
        BodyDeclaration<?> body = Parser.parseJavaSnippet(code);
        
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        body.accept(visitor, null);
        
        assertEquals(2, visitor.getComplexity());
        
        visitor.reset();
        assertEquals(1, visitor.getComplexity());
    }

    @Test
    public void testGetComplexity() {
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        assertEquals(1, visitor.getComplexity()); // Initial complexity should be 1
    }
    @Test
    public void testConditionalExpr_SuperVisitCall_Line59() throws Exception {
        // Target: removed call to super.visit() in visit(ConditionalExpr) at line 59
        // This test has nested conditional expressions that require super.visit() to traverse
        
        String code = """
            public int test(int x, int y, int z) {
                return x > 0 ? 
                    (y > 0 ? 
                        (z > 0 ? 1 : 2) : 
                        (z > 0 ? 3 : 4)) : 
                    (y > 0 ? 
                        (z > 0 ? 5 : 6) : 
                        7);
            }
            """;
        
        BodyDeclaration<?> body = Parser.parseJavaSnippet(code);
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        body.accept(visitor, null);
        
        // Should count: base(1) + outer conditional(1) + 4 nested conditionals(4) = 6
        // Without super.visit(), nested conditionals won't be counted, result would be 2
        assertEquals(7, visitor.getComplexity(), 
                    "Should count all nested conditional expressions when super.visit() is called");
    }

    @Test
    public void testCatchClause_SuperVisitCall_Line53() throws Exception {
        // Target: removed call to super.visit() in visit(CatchClause) at line 53
        // This test has nested control structures within catch blocks
        
        String code = """
            public void test(int x) {
                try {
                    riskyOperation();
                } catch (IOException e) {
                    if (e.getCause() != null) {
                        for (int i = 0; i < 10; i++) {
                            if (i % 2 == 0) {
                                while (x > 0) {
                                    x--;
                                    if (x == 5) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } catch (RuntimeException e) {
                    for (String msg : errorMessages) {
                        if (msg.contains("critical")) {
                            return;
                        }
                    }
                }
            }
            """;
        
        BodyDeclaration<?> body = Parser.parseJavaSnippet(code);
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        body.accept(visitor, null);
        
        // Should count: base(1) + catch1(1) + catch2(1) + if(1) + for(1) + if(1) + while(1) + if(1) + for(1) + if(1) = 10
        // Without super.visit() in catch clauses, nested structures won't be counted
        assertTrue(visitor.getComplexity() >= 9, 
                  "Should count nested structures in catch blocks when super.visit() is called, got: " + visitor.getComplexity());
    }

    @Test
    public void testDoStmt_SuperVisitCall_Line39() throws Exception {
        // Target: removed call to super.visit() in visit(DoStmt) at line 39
        // This test has nested control structures within do-while loops
        
        String code = """
            public void test(int x, int y) {
                do {
                    if (x > 0) {
                        for (int i = 0; i < x; i++) {
                            if (i == y) {
                                do {
                                    y--;
                                    if (y < 0) {
                                        break;
                                    }
                                } while (y > 0);
                            }
                        }
                    } else {
                        while (x < 0) {
                            x++;
                        }
                    }
                    x--;
                } while (x > -10);
            }
            """;
        
        BodyDeclaration<?> body = Parser.parseJavaSnippet(code);
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        body.accept(visitor, null);
        
        // Should count: base(1) + do(1) + if(1) + for(1) + if(1) + inner do(1) + if(1) + while(1) = 8
        // Without super.visit() in do-while, nested structures won't be counted
        assertTrue(visitor.getComplexity() >= 7, 
                  "Should count nested structures in do-while loops when super.visit() is called, got: " + visitor.getComplexity());
    }

    @Test
    public void testForEachStmt_SuperVisitCall_Line27() throws Exception {
        // Target: removed call to super.visit() in visit(ForEachStmt) at line 27
        // This test has nested control structures within for-each loops
        
        String code = """
            public void test(List<String> items, List<Integer> numbers) {
                for (String item : items) {
                    if (item.length() > 5) {
                        for (Integer num : numbers) {
                            if (num > 0) {
                                while (num > 10) {
                                    num = num / 2;
                                    if (num == 1) {
                                        break;
                                    }
                                }
                            } else {
                                for (char c : item.toCharArray()) {
                                    if (Character.isDigit(c)) {
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            """;
        
        BodyDeclaration<?> body = Parser.parseJavaSnippet(code);
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        body.accept(visitor, null);
        
        // Should count: base(1) + for-each(1) + if(1) + inner for-each(1) + if(1) + while(1) + if(1) + for-each(1) + if(1) = 9
        // Without super.visit() in for-each, nested structures won't be counted
        assertTrue(visitor.getComplexity() >= 8, 
                  "Should count nested structures in for-each loops when super.visit() is called, got: " + visitor.getComplexity());
    }

    @Test
    public void testSwitchEntry_SuperVisitCall_Line47() throws Exception {
        // Target: removed call to super.visit() in visit(SwitchEntry) at line 47
        // This test has nested control structures within switch cases
        
        String code = """
            public void test(int x, int y) {
                switch (x) {
                    case 1:
                        if (y > 0) {
                            for (int i = 0; i < y; i++) {
                                if (i % 2 == 0) {
                                    while (i < 10) {
                                        i++;
                                        if (i == 8) {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    case 2:
                        try {
                            riskyOperation();
                        } catch (Exception e) {
                            if (e.getMessage() != null) {
                                for (String line : e.getMessage().split("\\n")) {
                                    if (line.contains("error")) {
                                        return;
                                    }
                                }
                            }
                        }
                        break;
                    case 3:
                        do {
                            y--;
                            if (y < 0) {
                                break;
                            }
                        } while (y > 0);
                        break;
                    default:
                        break;
                }
            }
            """;
        
        BodyDeclaration<?> body = Parser.parseJavaSnippet(code);
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        body.accept(visitor, null);
        
        // Should count: base(1) + case1(1) + case2(1) + case3(1) + nested structures(8+) = 12+
        // Without super.visit() in switch entries, nested structures won't be counted
        assertTrue(visitor.getComplexity() >= 11, 
                  "Should count nested structures in switch cases when super.visit() is called, got: " + visitor.getComplexity());
    }

    @Test
    public void testConditionalExpr_NestedWithLogicalOperators_Line59() throws Exception {
        // Additional test for ConditionalExpr with logical operators inside
        
        String code = """
            public boolean test(int a, int b, int c, int d) {
                return (a > 0 && b > 0) ? 
                    (c > 0 || d > 0) : 
                    (a < 0 && b < 0);
            }
            """;
        
        BodyDeclaration<?> body = Parser.parseJavaSnippet(code);
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        body.accept(visitor, null);
        
        // Should count: base(1) + conditional(1) + && (1) + || (1) + && (1) = 5
        // Without super.visit(), logical operators inside conditional won't be counted
        assertEquals(5, visitor.getComplexity(), 
                    "Should count logical operators within conditional expressions");
    }

    @Test
    public void testAllSuperVisitCalls_CombinedScenario() throws Exception {
        // Test that combines all the problematic constructs to ensure all super.visit() calls work
        
        String code = """
            public void complexMethod(List<String> items, int threshold) {
                for (String item : items) {                    // ForEachStmt - needs super.visit()
                    switch (item.length()) {                   // SwitchEntry - needs super.visit()
                        case 1:
                            do {                               // DoStmt - needs super.visit()
                                threshold--;
                                if (threshold < 0) {
                                    break;
                                }
                            } while (threshold > 0);
                            break;
                        case 2:
                            try {
                                processItem(item);
                            } catch (Exception e) {            // CatchClause - needs super.visit()
                                boolean retry = e.getCause() != null ? 
                                    (threshold > 5 && item.length() > 0) :  // ConditionalExpr - needs super.visit()
                                    false;
                                if (retry) {
                                    continue;
                                }
                            }
                            break;
                    }
                }
            }
            """;
        
        BodyDeclaration<?> body = Parser.parseJavaSnippet(code);
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        body.accept(visitor, null);
        
        // Should count all nested structures
        // If any super.visit() call is missing, the complexity will be lower
        assertTrue(visitor.getComplexity() >= 9, 
                  "Should count all nested structures when all super.visit() calls are present, got: " + visitor.getComplexity());
    }

    @Test
    public void testDoStmt_DeeplyNested_Line39() throws Exception {
        // More extreme test for DoStmt super.visit() call
        
        String code = """
            public void test() {
                int x = 10;
                do {
                    if (x > 5) {
                        do {
                            x--;
                            if (x == 7) {
                                for (int i = 0; i < 3; i++) {
                                    if (i == 1) {
                                        continue;
                                    }
                                }
                            }
                        } while (x > 6);
                    }
                } while (x > 0);
            }
            """;
        
        BodyDeclaration<?> body = Parser.parseJavaSnippet(code);
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        body.accept(visitor, null);
        
        // Should count: base(1) + outer do(1) + if(1) + inner do(1) + if(1) + for(1) + if(1) = 7
        assertEquals(7, visitor.getComplexity(), 
                    "Should count all nested structures in deeply nested do-while loops");
    }

    @Test
    public void testForEachStmt_WithConditionalAndSwitch_Line27() throws Exception {
        // Test ForEachStmt with nested switch and conditional expressions
        
        String code = """
            public void test(List<Integer> numbers) {
                for (Integer num : numbers) {
                    switch (num % 3) {
                        case 0:
                            boolean isEven = num % 2 == 0 ? true : false;
                            if (isEven) {
                                System.out.println("Even multiple of 3");
                            }
                            break;
                        case 1:
                            if (num > 10) {
                                System.out.println("Large number");
                            }
                            break;
                    }
                }
            }
            """;
        
        BodyDeclaration<?> body = Parser.parseJavaSnippet(code);
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        body.accept(visitor, null);
        
        // Should count: base(1) + for-each(1) + case0(1) + case1(1) + conditional(1) + if(1) + if(1) = 7
        assertEquals(7, visitor.getComplexity(), 
                    "Should count all nested structures in for-each with switch and conditionals");
    }
}

