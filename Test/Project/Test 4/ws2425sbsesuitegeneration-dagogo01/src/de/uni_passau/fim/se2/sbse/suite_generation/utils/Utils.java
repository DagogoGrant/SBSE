package de.uni_passau.fim.se2.sbse.suite_generation.utils;

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

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestCase;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestCaseGenerator;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.StatementRepresenation.FieldAssignmentStatement;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.StatementRepresenation.InitializationStatement;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.StatementRepresenation.MethodCallStatement;
import de.uni_passau.fim.se2.sbse.suite_generation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.Branch;

/**
 * Store common functions used in both algorithms
 */
public class Utils {
    
    /**
     * Intialize the population to be used
     * @param size: Size of the population to be generated
     * @param generator: A generator of test caseses with random statements
     * @return a list of test cases
     */
    public static List<TestCase> initializePopulation(int size, TestCaseGenerator generator) {
        List<TestCase> population = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            population.add(generator.get());
        }
        return population;
    }

    /**
     * Calculate Branch distance for every branch and for each test case
     * @param population: A population of possible TestCases
     * @param targetBranches: Branches to be evaluated against the Testcases
     * @param fitnessFunctions: Function to use for evaluation
     * @return A mapping of testcases and their fitness values against all branches
     */
    public static Map<TestCase, Map<Branch, Double>> evaluateFitness(
        List<TestCase> population,
        List<Branch> targetBranches,
        Map<Branch, FitnessFunction<TestCase>> fitnessFunctions
        ) {
        Map<TestCase, Map<Branch, Double>> fitnessMap = new HashMap<>();

        for (TestCase testCase : population) {
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
     * Return True if P dominates q
     * 
     * @param p: The first TestCase to use for Comparison
     * @param q: The first TestCase to use for Comparison
     * @param fitnessMap: A mapping of TestCase IDs Branches and their fitness values
     * @param targetBranches: The target braches to be considered
     * @return True  of p dominates q, otherwise false
     */
    private static boolean dominates(
        TestCase p,
        TestCase q,
        Map<TestCase, Map<Branch, Double>> fitnessMap,
        List<Branch> targetBranches
        ) {
        Map<Branch, Double> pFitness = fitnessMap.get(p);
        Map<Branch, Double> qFitness = fitnessMap.get(q);

        boolean betterInOne = false;
        for (Branch branch : targetBranches) {
            if (pFitness.get(branch) > qFitness.get(branch)) return false;
            if (pFitness.get(branch) < qFitness.get(branch)) betterInOne = true;
        }
        return betterInOne;
    }

    /**
     * Returns a non dominated list of lists of testcases
     * @param population: A population of possible TestCases
     * @param fitnessMap: A mapping of TestCase IDs Branches and their fitness values
     * @param targetBranches: The target braches to be considered
     * @return A non dominated list of lists of testcases
     */
    public static List<List<TestCase>> nonDominatedSorting(
        List<TestCase> population,
        Map<TestCase, Map<Branch, Double>> fitnessMap,
        List<Branch> targetBranches
        ) {
        List<List<TestCase>> fronts = new ArrayList<>();
        Map<TestCase, Integer> dominationCount = new HashMap<>();
        Map<TestCase, List<TestCase>> dominatedBy = new HashMap<>();

        for (TestCase p : population) {
            dominationCount.put(p, 0);
            dominatedBy.put(p, new ArrayList<>());

            for (TestCase q : population) {
                if (dominates(p, q, fitnessMap, targetBranches)) {
                    dominatedBy.get(p).add(q);
                } else if (dominates(q, p, fitnessMap, targetBranches)) {
                    dominationCount.put(p, dominationCount.get(p) + 1);
                }
            }

            if (dominationCount.get(p) == 0) {
                if (fronts.isEmpty()) fronts.add(new ArrayList<>());
                fronts.get(0).add(p);
            }
        }

        int frontIndex = 0;
        while (frontIndex < fronts.size()) {
            List<TestCase> nextFront = new ArrayList<>();
            for (TestCase p : fronts.get(frontIndex)) {
                for (TestCase q : dominatedBy.get(p)) {
                    dominationCount.put(q, dominationCount.get(q) - 1);
                    if (dominationCount.get(q) == 0) {
                        nextFront.add(q);
                    }
                }
            }
            if (!nextFront.isEmpty()) {
                fronts.add(nextFront);
            }
            frontIndex++;
        }

        return fronts;
    }

    /**
     * 
     * * @param population: A population of possible TestCases
     * @param fitnessMap: A mapping of TestCase IDs Branches and their fitness values
     * @param targetBranches: The target braches to be considered
     * @param archive: An population of the best test cases chosen from evolving population
     */
    public static void updateArchive(
        List<TestCase> population,
        Map<TestCase, Map<Branch, Double>> fitnessMap,
        List<TestCase> archive,
        List<Branch> targetBranches
        ) {
            TestCase candidate = population.get(0);
            for (int i = 1; i < population.size(); i++) {
                if (dominates(population.get(i), candidate, fitnessMap, targetBranches)) {
                    candidate = population.get(i);
                }
            }
    
            Iterator<TestCase> archiveIterator = archive.iterator();
            while (archiveIterator.hasNext()) {
                TestCase archivedTestCase = archiveIterator.next();
                if (dominates(candidate, archivedTestCase, fitnessMap, targetBranches)) {
                    archiveIterator.remove();
                }
            }
            archive.add(candidate);
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

            // Add constructors
            Constructor<?>[] constructors = clazz.getDeclaredConstructors(); // Only declared constructors
            for (Constructor<?> constructor : constructors) {
                if (Modifier.isPublic(constructor.getModifiers())) { // Filter out non-public constructors
                    try {
                        Object[] parameters = generateRandomParameters(constructor.getParameterTypes());
                        constructor.setAccessible(true); // Allow access to private constructors if needed
                        Object instance = constructor.newInstance(parameters);
                        Statement statement = new InitializationStatement(instance, className, parameters);
                        allStatements.add(statement);
                    } catch (Exception e) {
                        System.err.println("Failed to invoke constructor: " + constructor.getName());
                        e.printStackTrace();
                    }
                }
            }

            // Use the first available constructor to create an instance of the CUT
            Constructor<?> defaultConstructor = constructors[0];
            Object obj = defaultConstructor.newInstance(generateRandomParameters(defaultConstructor.getParameterTypes()));

            // Add fields
            Field[] fields = clazz.getDeclaredFields(); // Only declared fields
            for (Field field : fields) {
                if (!Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) { // Ignore static and private fields
                    try {
                        Object value = generateRandomValue(field.getType());
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
                        Object[] parameters = generateRandomParameters(method.getParameterTypes());
                        Statement statement = new MethodCallStatement(obj, method, parameters);
                        allStatements.add(statement);
                    } catch (Exception e) {
                        System.err.println("Failed to process method: " + method.getName());
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error while processing class: " + classUnderTest.getName());
            e.printStackTrace();
        }
        System.out.println("The number of statments is: " + allStatements.size());
        return allStatements;
    }

    /**
     * Generates random values for the given parameter types.
     *
     * @param parameterTypes Array of parameter types.
     * @return Array of randomly generated parameter values.
     */
    private static Object[] generateRandomParameters(Class<?>[] parameterTypes) {
        Object[] parameters = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            parameters[i] = generateRandomValue(parameterTypes[i]);
        }
        return parameters;
    }

    /**
     * Generates a random value for a given type.
     *
     * @param type The type for which a random value will be generated.
     * @return A randomly generated value of the given type.
     */
    private static Object generateRandomValue(Class<?> type) {
        Random random = Randomness.random();

        if (type == int.class || type == Integer.class) {
            return random.nextInt(Randomness.MAX_INT - Randomness.MIN_INT + 1) + Randomness.MIN_INT;
        } else if (type == double.class || type == Double.class) {
            return random.nextDouble() * (Randomness.MAX_INT - Randomness.MIN_INT) + Randomness.MIN_INT;
        } else if (type == long.class || type == Long.class) {
            return random.nextLong() % (Randomness.MAX_INT - Randomness.MIN_INT + 1) + Randomness.MIN_INT;
        } else if (type == float.class || type == Float.class) {
            return random.nextFloat() * (Randomness.MAX_INT - Randomness.MIN_INT) + Randomness.MIN_INT;
        } else if (type == char.class || type == Character.class) {
            return (char) (random.nextInt(95) + 32); // Printable ASCII: 32 to 126
        } else if (type == boolean.class || type == Boolean.class) {
            return random.nextBoolean();
        } else if (type == String.class) {
            int length = random.nextInt(10) + 1; // Random string length between 1 and 10
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                sb.append((char) (random.nextInt(95) + 32)); // Printable ASCII
            }
            return sb.toString();
        } else {
            return null; // All other types get null
        }
    }
}
