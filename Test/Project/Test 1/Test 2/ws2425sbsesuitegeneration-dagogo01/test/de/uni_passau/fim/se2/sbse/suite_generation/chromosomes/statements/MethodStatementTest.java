package de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class MethodStatementTest {

    @Test
    public void testMethodExecution() throws Exception {
        // Create a test object
        TestClass testObject = new TestClass();
        Method method = TestClass.class.getDeclaredMethod("incrementValue", int.class);

        // Create the method statement
        MethodStatement statement = new MethodStatement(method, testObject, new Object[]{5});

        // Execute the method
        statement.run();

        // Verify the method execution result
        assertEquals(5, testObject.getValue());
    }

    @Test
    public void testStaticMethodExecution() throws Exception {
        // Access a static method
        Method method = TestClass.class.getDeclaredMethod("setStaticValue", int.class);

        // Create the method statement for the static method
        MethodStatement statement = new MethodStatement(method, null, new Object[]{10});

        // Execute the static method
        statement.run();

        // Verify the static field value
        assertEquals(10, TestClass.getStaticValue());
    }

    @Test(expected = RuntimeException.class)
    public void testMethodExecutionThrowsException() throws Exception {
        // Create a test object
        TestClass testObject = new TestClass();

        // Create a method statement with incorrect parameters
        Method method = TestClass.class.getDeclaredMethod("incrementValue", int.class);
        MethodStatement statement = new MethodStatement(method, testObject, new Object[]{"invalid"});

        // Expect a RuntimeException during execution
        statement.run();
    }

    @Test
    public void testToStringMethod() throws Exception {
        // Create a test object
        TestClass testObject = new TestClass();
        Method method = TestClass.class.getDeclaredMethod("incrementValue", int.class);

        // Create a method statement
        MethodStatement statement = new MethodStatement(method, testObject, new Object[]{5});

        // Verify the string representation
        String result = statement.toString();
        assertNotNull(result);
        assertTrue(result.contains("TestClass.incrementValue(5)"));
    }

    @Test
    public void testToStringWithNullTarget() throws Exception {
        // Access a static method
        Method method = TestClass.class.getDeclaredMethod("setStaticValue", int.class);

        // Create a method statement for the static method
        MethodStatement statement = new MethodStatement(method, null, new Object[]{10});

        // Verify the string representation
        String result = statement.toString();
        assertNotNull(result);
        assertTrue(result.contains("<static>.setStaticValue(10)"));
    }

    // Helper class for testing
    public static class TestClass {
        private int value;
        private static int staticValue;

        public void incrementValue(int amount) {
            this.value += amount;
        }

        public int getValue() {
            return value;
        }

        public static void setStaticValue(int value) {
            staticValue = value;
        }

        public static int getStaticValue() {
            return staticValue;
        }
    }
}
