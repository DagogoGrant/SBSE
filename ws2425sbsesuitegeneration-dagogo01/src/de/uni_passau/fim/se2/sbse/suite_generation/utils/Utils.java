package de.uni_passau.fim.se2.sbse.suite_generation.utils;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestChromosome;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestChromosomeGenerator;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.JavaStatement.FieldAssignmentStatement;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.JavaStatement.ConstructorStatement;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.JavaStatement.MethodStatement;
import de.uni_passau.fim.se2.sbse.suite_generation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.Branch;
import de.uni_passau.fim.se2.sbse.suite_generation.stopping_conditions.MaxFitnessEvaluations;
import de.uni_passau.fim.se2.sbse.suite_generation.stopping_conditions.StoppingCondition;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;



/**
 * Store common functions used in both algorithms
 */
public class Utils {
 
  
    /**
     * Calculate Branch distance for every branch and for each test case
     * @param population: A population of possible TestCases
     * @param targetBranches: Branches to be evaluated against the Testcases
     * @param fitnessFunctions: Function to use for evaluation
     * @return A mapping of testcases and their fitness values against all branches
     */
    public static Map<TestChromosome, Map<Branch, Double>> evaluateFitness(
        List<TestChromosome> population,
        List<Branch> targetBranches,
        Map<Branch, FitnessFunction<TestChromosome>> fitnessFunctions
        ) {
        Map<TestChromosome, Map<Branch, Double>> fitnessMap = new HashMap<>();

        for (TestChromosome testCase : population) {
            Map<Branch, Double> branchFitness = new HashMap<>();
            for (Branch branch : targetBranches) {
                double fitness = fitnessFunctions.get(branch).applyAsDouble(testCase);
                branchFitness.put(branch, fitness);
            }
            fitnessMap.put(testCase, branchFitness);
        }

        return fitnessMap;
    }

    /**
     * extracts the constructor, fields and methods from a class under test
     * and constructs valid statement representations for each
     * @param classUnderTest: This is the class from which statements will be derived
     * @return a list of valid statement representations
     */
    public static List<Statement> allStatements(Class<?> classUnderTest){
        List<Statement> allStatements = new ArrayList<>();
        // Use a full qualified class name of a class under test
        try {
            Class<?> clazz = classUnderTest;
            String className = clazz.getName();
            // Collect all constructors, fields, and methods

            Constructor<?>[] constructors = clazz.getDeclaredConstructors(); // Only declared constructors
            for (Constructor<?> constructor : constructors) {
                Object[] params = generateRandomParameters(constructor.getParameterTypes(), null);
                Object instance = constructor.newInstance(params);

                // Add constructors
                if (Modifier.isPublic(constructor.getModifiers())) { // Filter out non-public constructors
                    try {
                        Statement statement = new ConstructorStatement(instance,constructor, params);

                        allStatements.add(statement);
                    } catch (Exception e) {
                        System.err.println("Failed to invoke constructor: " + constructor.getName());
                        e.printStackTrace();
                    }
                }

                Object obj = instance;

                // Add fields
                Field[] fields = clazz.getDeclaredFields(); // Only declared fields
                for (Field field : fields) {
                    if (!Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) { // Ignore static and private fields
                        try {
                            Object value = generateRandomValue(field.getType(), null);
                            field.setAccessible(true); // Allow access to private fields if needed
                            Statement statement = new FieldAssignmentStatement(obj, field, value);

                            allStatements.add(statement);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                // Add methods
                Method[] methods = clazz.getDeclaredMethods(); // Only declared methods
                for (Method method : methods) {
                    if (Modifier.isPublic(method.getModifiers())) { // Filter out private methods
                        try {
                            Object[] parameters = generateRandomParameters(method.getParameterTypes(), obj);
                            Statement statement = new MethodStatement(obj, method, parameters);

                            allStatements.add(statement);
                        } catch (Exception e) {
                            System.err.println("Failed to process method: " + method.getName());
                            e.printStackTrace();
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error while processing class: " + classUnderTest.getName());
            e.printStackTrace();
        }
        return allStatements;
    }

    /**
     * Generates random values for the given parameter types.
     *a
     * @param parameterTypes Array of parameter types.
     * @param obj The object of the class under test
     * @return Array of randomly generated parameter values.
     */
    private static Object[] generateRandomParameters(Class<?>[] parameterTypes, Object obj) {
        Object[] parameters = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            parameters[i] = generateRandomValue(parameterTypes[i], obj);
        }
        return parameters;
    }

    
    /**
     * Generates a random value for a given type.
     *
     * @param type The type for which a random value will be generated.
     * @param obj The object of the class under test
     * @return A randomly generated value of the given type.
     */
    /**
     * Generates a random value for a given type.
     *
     * @param type The type for which a random value will be generated.
     * @return A randomly generated value of the given type.
     */
    private static Object generateRandomValue(Class<?> type, Object obj) {
        Random random = Randomness.random();

        if (type.isPrimitive()) {
            if (type == boolean.class) return random.nextBoolean();
            if (type == byte.class) return (byte) random.nextInt(256);
            if (type == char.class) return (char) (random.nextInt(126 - 32 + 1) + 32);
            if (type == short.class)
                return (short) (random.nextInt(2048) - 1024); // Generate short between -1024 and 1023
            if (type == int.class) return random.nextInt(200) - 100; // Generate int between -100 and 99
            if (type == long.class) return random.nextLong(2048) - 1024; // Generate long between -1024 and 1023
            if (type == float.class) return random.nextFloat() * 2048 - 1024; // Generate float between -1024 and 1023
            if (type == double.class)
                return random.nextDouble() * 2048 - 1024; // Generate double between -1024 and 1023
        } else if (type == String.class) {
            int length = Randomness.random().nextInt(10) + 1;
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                sb.append((char) (Randomness.random().nextInt(95) + 32));
            }
            return sb.toString(); // Generate a string
        } else if (type == Integer.class) {
            return random.nextBoolean() ? random.nextInt(2048) - 1024 : null; // Generate Integer between -1024 and 1023 or null
        } else if (type == Long.class) {
            return random.nextBoolean() ? random.nextLong(2048) - 1024 : null; // Generate Long between -1024 and 1023 or null
        } else if (type == Float.class) {
            return random.nextBoolean() ? random.nextFloat() * 2048 - 1024 : null; // Generate Float between -1024 and 1023 or null
        } else if (type == Double.class) {
            return random.nextBoolean() ? random.nextDouble() * 2048 - 1024 : null; // Generate Double between -1024 and 1023 or null
        } else if (obj != null && type == obj.getClass()) {
            return random.nextBoolean() ? obj : null;
        }

        return null; // For other reference types

    }
}



