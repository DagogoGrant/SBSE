package de.uni_passau.fim.se2.sa.readability.utils;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.body.BodyDeclaration;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {

    @Test
    public void testParseJavaSnippet_ValidCode() throws ParseException {
        String code = "public void test() { System.out.println(\"hello\"); }";
        BodyDeclaration<?> result = Parser.parseJavaSnippet(code);
        assertNotNull(result);
    }

    @Test
    public void testParseJavaSnippet_NullInput() {
        assertThrows(ParseException.class, () -> {
            Parser.parseJavaSnippet(null);
        });
    }

    @Test
    public void testParseJavaSnippet_InvalidCode() {
        String invalidCode = "invalid java code {{{";
        assertThrows(ParseException.class, () -> {
            Parser.parseJavaSnippet(invalidCode);
        });
    }

    @Test
    public void testParseJavaSnippet_EmptyCode() {
        assertThrows(ParseException.class, () -> {
            Parser.parseJavaSnippet("");
        });
    }

    @Test
    public void testParseJavaSnippet_ComplexMethod() throws ParseException {
        String code = """
            public int factorial(int n) {
                if (n <= 1) {
                    return 1;
                } else {
                    return n * factorial(n - 1);
                }
            }
            """;
        BodyDeclaration<?> result = Parser.parseJavaSnippet(code);
        assertNotNull(result);
    }

    @Test
    public void testParseJavaSnippet_MethodWithComments() throws ParseException {
        String code = """
            // This is a test method
            public void test() {
                /* Block comment */
                System.out.println("hello");
            }
            """;
        BodyDeclaration<?> result = Parser.parseJavaSnippet(code);
        assertNotNull(result);
    }
}