package de.uni_passau.fim.se2.sa.readability.features;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CyclomaticComplexityFeatureTest {

    private final CyclomaticComplexityFeature feature = new CyclomaticComplexityFeature();

    @Test
    public void testComputeMetric_NullInput() {
        assertEquals(1.0, feature.computeMetric(null));
    }

    @Test
    public void testComputeMetric_EmptyInput() {
        assertEquals(1.0, feature.computeMetric(""));
    }

    @Test
    public void testComputeMetric_InvalidJavaCode() {
        assertEquals(1.0, feature.computeMetric("invalid java code {{{"));
    }

    @Test
    public void testComputeMetric_SimpleMethod() {
        String code = "public void simple() { System.out.println(\"hello\"); }";
        assertEquals(1.0, feature.computeMetric(code));
    }

    @Test
    public void testComputeMetric_WithIfStatement() {
        String code = "public void test(int x) { if (x > 0) { System.out.println(\"positive\"); } }";
        assertEquals(2.0, feature.computeMetric(code));
    }

    @Test
    public void testComputeMetric_WithMultipleControlStructures() {
        String code = """
            public void complex(int x) {
                if (x > 0) {
                    for (int i = 0; i < x; i++) {
                        while (i < 10) {
                            i++;
                        }
                    }
                }
            }
            """;
        assertEquals(4.0, feature.computeMetric(code));
    }

    @Test
    public void testComputeMetric_WithTryCatch() {
        String code = """
            public void test() {
                try {
                    int x = 1/0;
                } catch (Exception e) {
                    System.out.println("error");
                }
            }
            """;
        assertEquals(2.0, feature.computeMetric(code));
    }

    @Test
    public void testComputeMetric_WithTernaryOperator() {
        String code = "public int test(int x) { return x > 0 ? 1 : 0; }";
        assertEquals(2.0, feature.computeMetric(code));
    }

    @Test
    public void testComputeMetric_WithLogicalOperators() {
        String code = "public boolean test(int x, int y) { return x > 0 && y > 0 || x < 0; }";
        assertEquals(3.0, feature.computeMetric(code));
    }

    @Test
    public void testComputeMetric_WithSwitch() {
        String code = """
            public void test(int x) {
                switch (x) {
                    case 1: break;
                    case 2: break;
                    default: break;
                }
            }
            """;
        assertEquals(3.0, feature.computeMetric(code));
    }

    @Test
    public void testComputeMetric_WithForEach() {
        String code = """
            public void test(int[] arr) {
                for (int x : arr) {
                    System.out.println(x);
                }
            }
            """;
        assertEquals(2.0, feature.computeMetric(code));
    }

    @Test
    public void testComputeMetric_WithDoWhile() {
        String code = """
            public void test() {
                int i = 0;
                do {
                    i++;
                } while (i < 10);
            }
            """;
        assertEquals(2.0, feature.computeMetric(code));
    }

    @Test
    public void testGetIdentifier() {
        assertEquals("CyclomaticComplexity", feature.getIdentifier());
    }
}