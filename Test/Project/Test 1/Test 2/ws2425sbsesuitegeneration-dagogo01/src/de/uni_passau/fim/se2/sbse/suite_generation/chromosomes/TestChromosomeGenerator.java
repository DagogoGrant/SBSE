package de.uni_passau.fim.se2.sbse.suite_generation.chromosomes;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.ConstructorStatement;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.FieldAssignmentStatement;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.MethodStatement;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;
import de.uni_passau.fim.se2.sbse.suite_generation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_generation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_generation.utils.Randomness;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class TestChromosomeGenerator implements ChromosomeGenerator<TestChromosome> {

    private final Class<?> cut; // Class Under Test (CUT)
    private final Mutation<TestChromosome> mutation;
    private final Crossover<TestChromosome> crossover;
    private final Map<Class<?>, List<Object>> valuePool; // Value pool for smart parameter generation
    private static final int MAX_STATEMENTS = 50;

    public TestChromosomeGenerator(Class<?> cut, Mutation<TestChromosome> mutation, Crossover<TestChromosome> crossover) {
        this.cut = cut;
        this.mutation = mutation;
        this.crossover = crossover;
        this.valuePool = new HashMap<>();
        initializeValuePool();
    }

    private void initializeValuePool() {
        valuePool.put(Integer.class, Arrays.asList(0, 1, -1, Integer.MAX_VALUE, Integer.MIN_VALUE));
        valuePool.put(Double.class, Arrays.asList(0.0, 1.0, -1.0, Double.MAX_VALUE, Double.MIN_VALUE));
        valuePool.put(Boolean.class, Arrays.asList(true, false));
        valuePool.put(String.class, Arrays.asList("", "null", "test", "longStringValue"));
    }

    @Override
    public TestChromosome get() {
        TestChromosome chromosome = new TestChromosome(mutation, crossover);

        if (!createInstanceUsingConstructor(chromosome)) {
            System.err.println("Failed to create an instance using a constructor.");
            return chromosome; // Return an empty chromosome if no instance is created.
        }

        int numStatements = Randomness.random().nextInt(MAX_STATEMENTS - 1) + 1;
        for (int i = 0; i < numStatements; i++) {
            Statement statement;
            if (Randomness.random().nextDouble() < 0.5) {
                statement = generateFieldAssignmentStatement(chromosome);
            } else {
                statement = generateMethodStatement(chromosome);
            }

            if (statement != null) {
                chromosome.addStatement(statement);
            }
        }

        return chromosome;
    }

    private boolean createInstanceUsingConstructor(TestChromosome chromosome) {
        Constructor<?>[] constructors = cut.getConstructors();
        if (constructors.length == 0) {
            System.err.println("No constructors found for the class under test.");
            return false;
        }

        for (Constructor<?> constructor : constructors) {
            try {
                Object[] args = generateSmartArgs(constructor.getParameterTypes());
                ConstructorStatement statement = new ConstructorStatement(constructor, args);
                chromosome.addStatement(statement);
                return true;
            } catch (Exception e) {
                System.err.println("Constructor failed: " + e.getMessage());
            }
        }
        return false;
    }

    private MethodStatement generateMethodStatement(TestChromosome chromosome) {
        Method[] methods = cut.getDeclaredMethods();
        List<Method> filteredMethods = Arrays.stream(methods)
                .filter(method -> !Modifier.isStatic(method.getModifiers()))
                .collect(Collectors.toList());
    
        if (filteredMethods.isEmpty()) return null;
    
        Method method = filteredMethods.get(Randomness.random().nextInt(filteredMethods.size()));
        Statement firstStatement = chromosome.getStatements().get(0);
    
        Object target;
        if (firstStatement instanceof ConstructorStatement) {
            ConstructorStatement constructorStatement = (ConstructorStatement) firstStatement;
            if (constructorStatement.getInstance() == null) {
                constructorStatement.run(); // Execute the constructor if not already executed
            }
            target = constructorStatement.getInstance();
        } else {
            throw new IllegalStateException("The first statement is not a ConstructorStatement.");
        }
    
        // Ensure the target is assignable to the method's declaring class
        if (!method.getDeclaringClass().isInstance(target)) {
            return null; // Skip incompatible target
        }
    
        Object[] parameters = generateSmartArgs(method.getParameterTypes());
        return new MethodStatement(method, target, parameters);
    }
    
    
    

    private FieldAssignmentStatement generateFieldAssignmentStatement(TestChromosome chromosome) {
        Field[] fields = cut.getDeclaredFields();
        List<Field> filteredFields = Arrays.stream(fields)
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .collect(Collectors.toList());
    
        if (filteredFields.isEmpty()) return null;
    
        Field field = filteredFields.get(Randomness.random().nextInt(filteredFields.size()));
        Object target = chromosome.getStatements().get(0); // Constructor target
    
        // FIX: Ensure value type matches field type
        Object value = generateSmartValue(field.getType());
        if (value == null || !field.getType().isAssignableFrom(value.getClass())) {
            return null; // Skip if types don't match
        }
    
        return new FieldAssignmentStatement(field, target, value);
    }
    

    private Object getTargetObject(TestChromosome chromosome) {
        if (chromosome.getStatements().isEmpty()) {
            return null;
        }

        Statement firstStatement = chromosome.getStatements().get(0);
        if (firstStatement instanceof ConstructorStatement) {
            return ((ConstructorStatement) firstStatement).getInstance();
        }
        return null;
    }

    private Object[] generateSmartArgs(Class<?>[] parameterTypes) {
        return Arrays.stream(parameterTypes).map(this::generateSmartValue).toArray();
    }

    private Object generateSmartValue(Class<?> type) {
        if (valuePool.containsKey(type)) {
            List<Object> pool = valuePool.get(type);
            return pool.get(Randomness.random().nextInt(pool.size()));
        }

        if (type.equals(int.class) || type.equals(Integer.class)) {
            return Randomness.random().nextInt(2048) - 1024;
        } else if (type.equals(double.class) || type.equals(Double.class)) {
            return Randomness.random().nextDouble() * 2048 - 1024;
        } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
            return Randomness.random().nextBoolean();
        } else if (type.equals(String.class)) {
            return Randomness.random().nextBoolean() ? "test" : "example";
        }
        return null;
    }
}
