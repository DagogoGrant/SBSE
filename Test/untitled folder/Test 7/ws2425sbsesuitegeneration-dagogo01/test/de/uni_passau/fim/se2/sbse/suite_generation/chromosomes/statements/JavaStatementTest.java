package de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class JavaStatementTest {

    private TestClass testInstance;
    private Constructor<?> constructor;
    private Field field;
    private Method method;

    @BeforeEach
    void setUp() throws Exception {
        testInstance = new TestClass();

        // Get constructor, field, and method reflectively
        constructor = TestClass.class.getConstructor(int.class, String.class);
        field = TestClass.class.getDeclaredField("value");
        method = TestClass.class.getDeclaredMethod("setValue", int.class);
    }

    @Test
    void testConstructorStatementSuccess() {
        Object[] parameters = {42, "Test"};
        JavaStatement statement = new JavaStatement.ConstructorStatement(null, constructor, parameters);

        assertDoesNotThrow(statement::run);
        assertEquals("new TestClass(42, Test);", statement.toString());
    }

    @Test
    void testConstructorStatementFailure() {
        Object[] parameters = { "invalid", "Test" };  // Passing an invalid argument (String instead of int)
        JavaStatement statement = new JavaStatement.ConstructorStatement(null, constructor, parameters);
        assertThrows(RuntimeException.class, statement::run, "Expected constructor invocation to fail.");
    }
    

    @Test
    void testFieldAssignmentStatementSuccess() throws IllegalAccessException {
        JavaStatement statement = new JavaStatement.FieldAssignmentStatement(testInstance, field, 99);

        assertDoesNotThrow(statement::run);
        assertEquals(99, field.get(testInstance));
        assertEquals("TestClass.value = 99;", statement.toString());
    }

    // @Test
    // void testFieldAssignmentStatementFailure() throws NoSuchFieldException {
    //     class TestClass {
    //         private final int value = 10;  // Final field to trigger failure
    //     }
    
    //     TestClass instance = new TestClass();
    //     Field privateField = TestClass.class.getDeclaredField("value");
    
    //     JavaStatement.FieldAssignmentStatement statement = new JavaStatement.FieldAssignmentStatement(instance, privateField, 42);
    
    //     // Ensure field accessibility is not set before test
    //     assertThrows(RuntimeException.class, () -> {
    //         statement.run();
    //     }, "Expected field assignment to fail due to final modifier.");
    // }
    

    @Test
    void testMethodStatementSuccess() {
        Object[] parameters = {123};
        JavaStatement statement = new JavaStatement.MethodStatement(testInstance, method, parameters);

        assertDoesNotThrow(statement::run);
        assertEquals("TestClass.setValue(123);", statement.toString());
    }

    // @Test
    // void testMethodStatementFailure() throws NoSuchMethodException {
    //     class TestClass {
    //         private void privateMethod(int x) { /* Private method should not be accessible */ }
    //     }
    
    //     TestClass instance = new TestClass();
    //     Method privateMethod = TestClass.class.getDeclaredMethod("privateMethod", int.class);
    
    //     // Make sure the method is private to trigger access failure
    //     JavaStatement.MethodStatement statement = new JavaStatement.MethodStatement(instance, privateMethod, new Object[]{42});
    
    //     assertThrows(RuntimeException.class, () -> {
    //         statement.run();
    //     }, "Expected method invocation to fail due to access restriction.");
    // }
    
    

    // Helper classes for testing
    public static class TestClass {
        public int value;
        private String name;

        public TestClass() {}

        public TestClass(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public void setValue(int newValue) {
            this.value = newValue;
        }
    }

    private static class PrivateTestClass {
        private int privateValue;

        private void privateMethod(int newValue) {
            this.privateValue = newValue;
        }
    }
    @Test
void testConstructorStatementWithNullParameters() {
    Object[] parameters = null;
    JavaStatement statement = new JavaStatement.ConstructorStatement(null, constructor, parameters);

    assertThrows(RuntimeException.class, statement::run, "Expected constructor invocation to fail due to null parameters.");
}

@Test
void testConstructorStatementWithIncorrectParametersCount() {
    Object[] parameters = {42};  // Missing second argument
    JavaStatement statement = new JavaStatement.ConstructorStatement(null, constructor, parameters);

    assertThrows(RuntimeException.class, statement::run, "Expected constructor invocation to fail due to incorrect parameter count.");
}

@Test
void testFieldAssignmentStatementWithNullValue() throws IllegalAccessException {
    JavaStatement statement = new JavaStatement.FieldAssignmentStatement(testInstance, field, 0); // Assign default int value

    assertDoesNotThrow(statement::run);
    assertEquals(0, field.get(testInstance), "Field should be set to default value.");
}


@Test
void testFieldAssignmentStatementWithIncorrectType() {
    JavaStatement statement = new JavaStatement.FieldAssignmentStatement(testInstance, field, "invalidType");

    assertThrows(RuntimeException.class, statement::run, "Expected field assignment to fail due to type mismatch.");
}

@Test
void testFieldAssignmentStatementWithNullInstance() {
    JavaStatement statement = new JavaStatement.FieldAssignmentStatement(null, field, 99);

    assertThrows(NullPointerException.class, statement::run, "Expected field assignment to fail due to null instance.");
}

// @Test
// void testMethodStatementWithNullInstance() {
//     JavaStatement statement = new JavaStatement.MethodStatement(null, method, new Object[]{123});

//     assertThrows(IllegalArgumentException.class, statement::run, "Expected method invocation to fail due to null instance.");
// }


@Test
void testMethodStatementWithNullParameters() {
    JavaStatement statement = new JavaStatement.MethodStatement(testInstance, method, null);

    assertThrows(RuntimeException.class, statement::run, "Expected method invocation to fail due to null parameters.");
}

@Test
void testMethodStatementWithIncorrectParameterType() {
    JavaStatement statement = new JavaStatement.MethodStatement(testInstance, method, new Object[]{"wrongType"});

    assertThrows(RuntimeException.class, statement::run, "Expected method invocation to fail due to parameter type mismatch.");
}

// @Test
// void testPrivateConstructorStatement() throws NoSuchMethodException {
//     Constructor<PrivateTestClass> privateConstructor = PrivateTestClass.class.getDeclaredConstructor();
    
//     // Explicitly set accessibility to false before testing
//     privateConstructor.setAccessible(false);

//     assertFalse(privateConstructor.canAccess(null), "Constructor should not be accessible initially.");

//     JavaStatement statement = new JavaStatement.ConstructorStatement(null, privateConstructor, new Object[]{});
//     assertThrows(RuntimeException.class, statement::run, "Expected constructor invocation to fail due to private access.");
// }



@Test
void testToStringEdgeCases() {
    JavaStatement statement = new JavaStatement.MethodStatement(testInstance, method, new Object[]{null});
    assertEquals("TestClass.setValue(null);", statement.toString(), "Expected toString to handle null parameters correctly.");
}

// @Test
// void testInvalidFieldAssignmentWithoutAccessibility() throws NoSuchFieldException {
//     Field privateField = PrivateTestClass.class.getDeclaredField("privateValue");

//     // Explicitly ensure field is not accessible
//     privateField.setAccessible(false);
//     assertFalse(privateField.canAccess(new PrivateTestClass()), "Field should not be accessible initially");

//     JavaStatement.FieldAssignmentStatement statement = new JavaStatement.FieldAssignmentStatement(new PrivateTestClass(), privateField, 50);
//     assertThrows(RuntimeException.class, statement::run, "Expected field assignment to fail due to private access.");
// }


// @Test
// void testMethodWithException() throws NoSuchMethodException {
//     Method exceptionMethod = TestClass.class.getDeclaredMethod("throwException");

//     JavaStatement.MethodStatement statement = new JavaStatement.MethodStatement(testInstance, exceptionMethod, new Object[]{});

//     assertThrows(RuntimeException.class, statement::run, "Expected method invocation to fail due to thrown exception.");
// }

@Test
void testConstructorStatementWithNoArgs() throws NoSuchMethodException {
    Constructor<TestClass> noArgConstructor = TestClass.class.getDeclaredConstructor();
    JavaStatement statement = new JavaStatement.ConstructorStatement(null, noArgConstructor, new Object[]{});

    assertDoesNotThrow(statement::run, "Expected no-arg constructor invocation to succeed.");
}

@Test
void testConstructorStatementWithPrimitiveParameters() {
    Object[] parameters = {10, "Test"};
    JavaStatement statement = new JavaStatement.ConstructorStatement(null, constructor, parameters);

    assertDoesNotThrow(statement::run, "Expected constructor invocation to succeed with primitive types.");
}

// @Test
// void testConstructorStatementWithPrivateConstructor() throws NoSuchMethodException {
//     Constructor<PrivateTestClass> privateConstructor = PrivateTestClass.class.getDeclaredConstructor();
//     JavaStatement statement = new JavaStatement.ConstructorStatement(null, privateConstructor, new Object[]{});

//     assertThrows(RuntimeException.class, statement::run, "Expected constructor invocation to fail due to private access.");
// }
@Test
void testFieldAssignmentStatementWithDifferentDataType() {
    JavaStatement statement = new JavaStatement.FieldAssignmentStatement(testInstance, field, "wrongType");

    assertThrows(RuntimeException.class, statement::run, "Expected field assignment to fail due to type mismatch.");
}

@Test
void testFieldAssignmentStatementWithAccessibleField() throws NoSuchFieldException, IllegalAccessException {
    Field accessibleField = TestClass.class.getDeclaredField("value");
    accessibleField.setAccessible(true);

    JavaStatement statement = new JavaStatement.FieldAssignmentStatement(testInstance, accessibleField, 100);

    assertDoesNotThrow(statement::run, "Expected assignment to succeed with accessible field.");

    assertEquals(100, accessibleField.get(testInstance));
}


@Test
void testFieldAssignmentStatementWithStaticField() throws NoSuchFieldException {
    Field staticField = StaticTestClass.class.getDeclaredField("staticValue");
    JavaStatement statement = new JavaStatement.FieldAssignmentStatement(null, staticField, 200);

    assertDoesNotThrow(statement::run, "Expected assignment to succeed with static field.");
    assertEquals(200, StaticTestClass.staticValue);
}
// @Test
// void testMethodStatementWithMultipleParameters() throws NoSuchMethodException {
//     Method multiParamMethod = TestClass.class.getDeclaredMethod("setMultipleValues", int.class, String.class);
//     Object[] parameters = {10, "hello"};

//     JavaStatement statement = new JavaStatement.MethodStatement(testInstance, multiParamMethod, parameters);

//     assertDoesNotThrow(statement::run, "Expected method invocation to succeed with multiple parameters.");
// }

// @Test
// void testMethodStatementWithNullInstance() {
//     JavaStatement statement = new JavaStatement.MethodStatement(null, method, new Object[]{123});

//     assertThrows(IllegalArgumentException.class, statement::run, "Expected method invocation to fail due to null instance.");
// }



// @Test
// void testMethodStatementWithPrivateMethod() throws NoSuchMethodException {
//     Method privateMethod = PrivateTestClass.class.getDeclaredMethod("privateMethod", int.class);
//     JavaStatement statement = new JavaStatement.MethodStatement(new PrivateTestClass(), privateMethod, new Object[]{5});

//     assertThrows(RuntimeException.class, statement::run, "Expected private method invocation to fail.");
// }
@Test
void testToStringConstructorWithNullParameters() {
    JavaStatement statement = new JavaStatement.ConstructorStatement(null, constructor, new Object[]{null, null});

    assertEquals("new TestClass(null, null);", statement.toString(), "Expected toString to handle null parameters correctly.");
}

@Test
void testToStringFieldAssignmentWithNullInstance() {
    JavaStatement statement = new JavaStatement.FieldAssignmentStatement(null, field, 99);

    assertEquals("TestClass.value = 99;", statement.toString(), "Expected toString with null instance to return correct format.");
}

@Test
void testToStringMethodInvocationWithNullParameters() {
    JavaStatement statement = new JavaStatement.MethodStatement(testInstance, method, new Object[]{null});

    assertEquals("TestClass.setValue(null);", statement.toString(), "Expected toString to handle null parameters correctly.");
}
public static class StaticTestClass {
    public static int staticValue;
}
// @Test
// void testConstructorStatementWithNullConstructor() {
//     assertThrows(NullPointerException.class, () -> new JavaStatement.ConstructorStatement(null, null, new Object[]{42, "Test"}));
// }
@Test
void testConstructorStatementWithExcessParameters() {
    Object[] parameters = {42, "Test", "ExtraParam"};
    JavaStatement statement = new JavaStatement.ConstructorStatement(null, constructor, parameters);

    assertThrows(RuntimeException.class, statement::run, "Expected constructor invocation to fail due to excess parameters.");
}
@Test
void testConstructorStatementWithPrimitiveDefaults() {
    Object[] parameters = {0, null};
    JavaStatement statement = new JavaStatement.ConstructorStatement(null, constructor, parameters);

    assertDoesNotThrow(statement::run, "Expected constructor invocation to succeed with default values.");
}
// @Test
// void testFieldAssignmentOnFinalField() throws NoSuchFieldException, IllegalAccessException {
//     FinalFieldTestClass instance = new FinalFieldTestClass();
//     Field finalField = FinalFieldTestClass.class.getDeclaredField("finalValue");
//     finalField.setAccessible(true);

//     finalField.set(instance, 50);
    
//     assertEquals(10, instance.finalValue, "Final fields should not change their value.");
// }


@Test
void testFieldAssignmentOnPrivateStaticField() throws NoSuchFieldException {
    Field privateStaticField = PrivateStaticTestClass.class.getDeclaredField("privateStaticValue");
    privateStaticField.setAccessible(true);

    JavaStatement statement = new JavaStatement.FieldAssignmentStatement(null, privateStaticField, 300);

    assertDoesNotThrow(statement::run, "Expected assignment to succeed on private static field.");
    assertEquals(300, PrivateStaticTestClass.getPrivateStaticValue());
}
@Test
void testMethodStatementOnStaticMethod() throws NoSuchMethodException {
    Method staticMethod = StaticMethodTestClass.class.getDeclaredMethod("staticMethod", int.class);
    JavaStatement statement = new JavaStatement.MethodStatement(null, staticMethod, new Object[]{123});

    assertDoesNotThrow(statement::run, "Expected static method invocation to succeed.");
    assertEquals(123, StaticMethodTestClass.getValue());
}
@Test
void testFieldAssignmentWithInvalidInstanceType() {
    JavaStatement statement = new JavaStatement.FieldAssignmentStatement("InvalidInstance", field, 99);

    assertThrows(RuntimeException.class, statement::run, "Expected assignment to fail due to invalid instance type.");
}
@Test
void testMethodStatementWithExcessParameters() {
    Object[] parameters = {123, "ExtraParam"};
    JavaStatement statement = new JavaStatement.MethodStatement(testInstance, method, parameters);

    assertThrows(RuntimeException.class, statement::run, "Expected method invocation to fail due to excess parameters.");
}
@Test
void testMethodStatementWithIncorrectParameterTypes() {
    Object[] parameters = {"wrongType"};
    JavaStatement statement = new JavaStatement.MethodStatement(testInstance, method, parameters);

    assertThrows(RuntimeException.class, statement::run, "Expected method invocation to fail due to incorrect parameter types.");
}
@Test
void testFieldAssignmentWithFieldOfWrongClass() throws NoSuchFieldException {
    Field unrelatedField = UnrelatedTestClass.class.getDeclaredField("unrelatedValue");
    JavaStatement statement = new JavaStatement.FieldAssignmentStatement(testInstance, unrelatedField, 50);

    assertThrows(RuntimeException.class, statement::run, "Expected field assignment to fail due to wrong class field.");
}
// @Test
// void testPrivateMethodInvocation() throws NoSuchMethodException {
//     Method privateMethod = TestClass.class.getDeclaredMethod("privateMethod", int.class);
//     privateMethod.setAccessible(true);

//     JavaStatement statement = new JavaStatement.MethodStatement(testInstance, privateMethod, new Object[]{456});

//     assertThrows(RuntimeException.class, statement::run, "Expected private method invocation to fail.");
// }

// Add private method to TestClass

@Test
void testToStringEdgeCaseForFieldAssignment() {
    JavaStatement statement = new JavaStatement.FieldAssignmentStatement(null, field, null);
    assertEquals("TestClass.value = null;", statement.toString(), "Expected toString to handle null values correctly.");
}
// @Test
// void testConstructorStatementWithAbstractClass() {
//     assertThrows(InstantiationException.class, () -> {
//         Constructor<AbstractTestClass> abstractConstructor = AbstractTestClass.class.getDeclaredConstructor();
//         abstractConstructor.setAccessible(true);
//         abstractConstructor.newInstance(); // Should trigger InstantiationException
//     });
// }

public static class FinalFieldTestClass {
    public final int finalValue = 10;
}

public static class PrivateStaticTestClass {
    private static int privateStaticValue;

    public static int getPrivateStaticValue() {
        return privateStaticValue;
    }

    public static void setPrivateStaticValue(int value) {
        privateStaticValue = value;
    }
}

public static class StaticMethodTestClass {
    private static int value;

    public static void staticMethod(int v) {
        value = v;
    }

    public static int getValue() {
        return value;
    }

    public static void resetValue() {
        value = 0;
    }
}

public static class UnrelatedTestClass {
    public int unrelatedValue;

    public UnrelatedTestClass(int value) {
        this.unrelatedValue = value;
    }

    public void setUnrelatedValue(int value) {
        this.unrelatedValue = value;
    }

    public int getUnrelatedValue() {
        return unrelatedValue;
    }
}

public abstract static class AbstractTestClass {
    public AbstractTestClass() {
        System.out.println("AbstractTestClass constructor called");
    }

    public abstract void abstractMethod();
}

@Test
void testConstructorStatementWithProtectedConstructor() throws NoSuchMethodException {
    Constructor<ProtectedTestClass> protectedConstructor = ProtectedTestClass.class.getDeclaredConstructor();
    JavaStatement statement = new JavaStatement.ConstructorStatement(null, protectedConstructor, new Object[]{});

    assertDoesNotThrow(statement::run, "Expected invocation of protected constructor to succeed.");
}

// Helper class for protected constructor
public static class ProtectedTestClass {
    protected ProtectedTestClass() {}
}
@Test
void testConstructorStatementWithException() throws NoSuchMethodException {
    Constructor<TestClassWithException> exceptionConstructor = TestClassWithException.class.getDeclaredConstructor();
    JavaStatement statement = new JavaStatement.ConstructorStatement(null, exceptionConstructor, new Object[]{});

    assertThrows(RuntimeException.class, statement::run, "Expected constructor to throw an exception.");
}

// Helper class for constructor exception
public static class TestClassWithException {
    public TestClassWithException() {
        throw new RuntimeException("Constructor exception");
    }
}
@Test
void testFieldAssignmentWithNullCutInstance() {
    JavaStatement statement = new JavaStatement.FieldAssignmentStatement(null, field, 100);

    assertThrows(NullPointerException.class, statement::run, "Expected failure due to null cutInstance.");
}
@Test
void testFieldAssignmentOnFinalField() throws NoSuchFieldException, IllegalAccessException {
    FinalFieldTestClass instance = new FinalFieldTestClass();
    Field finalField = FinalFieldTestClass.class.getDeclaredField("finalValue");
    finalField.setAccessible(true);

    finalField.set(instance, 50);  // Attempt assignment

    assertEquals(10, instance.finalValue, "Final fields should not change their value.");
}

// @Test
// void testMethodStatementWithVoidMethod() throws NoSuchMethodException {
//     Method voidMethod = TestClass.class.getDeclaredMethod("voidMethod");
//     JavaStatement statement = new JavaStatement.MethodStatement(testInstance, voidMethod, new Object[]{});

//     assertDoesNotThrow(statement::run, "Expected void method to execute without issues.");
// }

// Add method to TestClass
public static class TrestClass {
    public void voidMethod() {
        System.out.println("Void method executed");
    }
}
@Test
void testMethodStatementWithStaticMethod() throws NoSuchMethodException {
    Method staticMethod = StaticMethodTestClass.class.getDeclaredMethod("staticMethod", int.class);
    JavaStatement statement = new JavaStatement.MethodStatement(null, staticMethod, new Object[]{999});

    assertDoesNotThrow(statement::run, "Expected static method to execute successfully.");
    assertEquals(999, StaticMethodTestClass.getValue(), "Static value should be updated.");
}
@Test
void testToStringForConstructorStatementWithNulls() {
    JavaStatement statement = new JavaStatement.ConstructorStatement(null, constructor, new Object[]{null, null});

    String expected = "new TestClass(null, null);";
    assertEquals(expected, statement.toString(), "Expected string output with null parameters.");
}
// @Test
// void testFieldAssignmentWithoutAccessibility() throws NoSuchFieldException {
//     Field privateField = PrivateTestClass.class.getDeclaredField("privateValue");
//     privateField.setAccessible(false);  // Ensure accessibility is restricted

//     JavaStatement statement = new JavaStatement.FieldAssignmentStatement(new PrivateTestClass(), privateField, 50);

//     assertThrows(RuntimeException.class, statement::run, "Expected failure due to private field without accessibility.");
// }

// // Modify helper class
// public static class PrivateTestClass {
//     private int privateValue;
// }

// @Test
// void testMethodStatementWithNullMethod() {
//     assertThrows(NullPointerException.class, () -> new JavaStatement.MethodStatement(testInstance, null, new Object[]{}));
// }
@Test
void testConstructorStatementWithNegativeValues() {
    Object[] parameters = {-5, "Negative"};
    JavaStatement statement = new JavaStatement.ConstructorStatement(null, constructor, parameters);

    assertDoesNotThrow(statement::run, "Expected constructor to handle negative values.");
}
// @Test
// void testMethodStatementWithExceptionThrowingMethod() throws NoSuchMethodException {
//     Method exceptionMethod = TestClass.class.getDeclaredMethod("throwException");

//     JavaStatement statement = new JavaStatement.MethodStatement(testInstance, exceptionMethod, new Object[]{});
//     assertThrows(RuntimeException.class, statement::run, "Expected RuntimeException from method.");
// }

// // Add method to TestClass
// public static class TraestClass {
//     public void throwException() {
//         throw new RuntimeException("Method exception");
//     }
@Test
void testConstructorStatementWithNullParams() {
    Object[] parameters = null;
    JavaStatement statement = new JavaStatement.ConstructorStatement(null, constructor, parameters);

    assertThrows(RuntimeException.class, statement::run, "Expected failure due to null parameters.");
}
@Test
void testConstructorStatementWithWrongParamTypes() {
    Object[] parameters = {"wrongType", 42};  // Incorrect type for first parameter
    JavaStatement statement = new JavaStatement.ConstructorStatement(null, constructor, parameters);

    assertThrows(RuntimeException.class, statement::run, "Expected failure due to type mismatch.");
}
@Test
void testConstructorStatementWithExtraParameters() {
    Object[] parameters = {42, "Test", "Extra"};
    JavaStatement statement = new JavaStatement.ConstructorStatement(null, constructor, parameters);

    assertThrows(RuntimeException.class, statement::run, "Expected constructor failure due to excess parameters.");
}
// @Test
// void testFieldAssignmentStatementWithFinalField() throws NoSuchFieldException {
//     Field finalField = FinalFieldTestClass.class.getDeclaredField("finalValue");
//     JavaStatement statement = new JavaStatement.FieldAssignmentStatement(new FinalFieldTestClass(), finalField, 42);

//     finalField.setAccessible(false);  // Ensure accessibility is disabled

//     assertThrows(RuntimeException.class, statement::run, "Expected failure due to assignment to final field.");
// }

@Test
void testFieldAssignmentStatementWithNullField() {
    JavaStatement statement = new JavaStatement.FieldAssignmentStatement(testInstance, null, 42);

    assertThrows(NullPointerException.class, statement::run, "Expected failure due to null field.");
}
@Test
void testMethodStatementWithNullMethod() {
    JavaStatement statement = new JavaStatement.MethodStatement(testInstance, null, new Object[]{});

    assertThrows(NullPointerException.class, statement::run, "Expected failure due to null method.");
}
@Test
void testMethodStatementWithExtraParameters() {
    Object[] parameters = {42, "ExtraParam"};
    JavaStatement statement = new JavaStatement.MethodStatement(testInstance, method, parameters);

    assertThrows(RuntimeException.class, statement::run, "Expected failure due to excess parameters.");
}
@Test
void testToStringMethodWithNullValues() {
    JavaStatement statement = new JavaStatement.MethodStatement(testInstance, method, new Object[]{null});
    assertEquals("TestClass.setValue(null);", statement.toString(), "Expected string output for null values.");
}
@Test
void testFieldAssignmentStatementWithInvalidType() {
    JavaStatement statement = new JavaStatement.FieldAssignmentStatement(testInstance, field, "invalid");

    assertThrows(RuntimeException.class, statement::run, "Expected failure due to incorrect field type.");
}
@Test
void testConstructorStatementWithPrimitiveArrayParameter() {
    Object[] parameters = {new int[]{1, 2, 3}};
    JavaStatement statement = new JavaStatement.ConstructorStatement(null, constructor, parameters);

    assertThrows(RuntimeException.class, statement::run, "Expected constructor to fail with array parameter.");
}

@Test
void testConstructorStatementWithProtectedAccess() throws NoSuchMethodException {
    Constructor<ProtectedTestClass> protectedConstructor = ProtectedTestClass.class.getDeclaredConstructor();
    JavaStatement statement = new JavaStatement.ConstructorStatement(null, protectedConstructor, new Object[]{});

    assertDoesNotThrow(statement::run, "Expected protected constructor invocation to succeed.");
}

@Test
void testConstructorStatementWithEmptyParameterList() throws NoSuchMethodException {
    Constructor<TestClass> emptyConstructor = TestClass.class.getDeclaredConstructor();
    JavaStatement statement = new JavaStatement.ConstructorStatement(null, emptyConstructor, new Object[]{});

    assertDoesNotThrow(statement::run, "Expected empty constructor invocation to succeed.");
}
@Test
void testFieldAssignmentStatementOnInheritedField() throws NoSuchFieldException {
    Field inheritedField = ChildTestClass.class.getSuperclass().getDeclaredField("value");
    JavaStatement statement = new JavaStatement.FieldAssignmentStatement(new ChildTestClass(), inheritedField, 123);

    assertDoesNotThrow(statement::run, "Expected inherited field assignment to succeed.");
}

// @Test
// void testFieldAssignmentStatementOnArrayField() throws NoSuchFieldException {
//     Field arrayField = TestClass.class.getDeclaredField("intArray");
//     JavaStatement statement = new JavaStatement.FieldAssignmentStatement(testInstance, arrayField, new int[]{1, 2, 3});

//     assertDoesNotThrow(statement::run, "Expected assignment of int array to succeed.");
// }
// @Test
// void testMethodStatementOnMethodWithMultipleParameters() throws NoSuchMethodException {
//     Method multiParamMethod = TestClass.class.getDeclaredMethod("setMultipleValues", int.class, String.class);
//     Object[] parameters = {10, "Hello"};

//     JavaStatement statement = new JavaStatement.MethodStatement(testInstance, multiParamMethod, parameters);

//     assertDoesNotThrow(statement::run, "Expected method with multiple parameters to succeed.");
// }

@Test
void testMethodStatementWithNullObject() {
    JavaStatement statement = new JavaStatement.MethodStatement(null, method, new Object[]{42});

    assertThrows(RuntimeException.class, statement::run, "Expected method invocation to fail with null object.");
}

@Test
void testMethodStatementWithInheritedMethod() throws NoSuchMethodException {
    Method inheritedMethod = ChildTestClass.class.getDeclaredMethod("inheritedMethod", int.class);
    JavaStatement statement = new JavaStatement.MethodStatement(new ChildTestClass(), inheritedMethod, new Object[]{50});

    assertDoesNotThrow(statement::run, "Expected inherited method invocation to succeed.");
}
@Test
void testFieldAssignmentStatementWithNegativeValue() {
    JavaStatement statement = new JavaStatement.FieldAssignmentStatement(testInstance, field, -99);

    assertDoesNotThrow(statement::run, "Expected negative value assignment to succeed.");
}

// @Test
// void testMethodStatementWithExceptionHandling() throws NoSuchMethodException {
//     Method exceptionMethod = TestClass.class.getDeclaredMethod("throwException");

//     JavaStatement statement = new JavaStatement.MethodStatement(testInstance, exceptionMethod, new Object[]{});

//     assertThrows(RuntimeException.class, statement::run, "Expected method to throw an exception.");
// }

@Test
void testFieldAssignmentStatementWithNullFieldObject() {
    JavaStatement statement = new JavaStatement.FieldAssignmentStatement(null, null, null);

    assertThrows(NullPointerException.class, statement::run, "Expected failure due to null field.");
}

@Test
void testConstructorStatementWithAbstractClass() {
    assertThrows(InstantiationException.class, () -> {
        Constructor<AbstractTestClass> abstractConstructor = AbstractTestClass.class.getDeclaredConstructor();
        abstractConstructor.setAccessible(true);
        abstractConstructor.newInstance(); // Should trigger InstantiationException
    });
}
public static class ChildTestClass extends TestClass {
    public void inheritedMethod(int value) {
        this.value = value;
    }
}


}




