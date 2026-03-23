package de.uni_passau.fim.se2.sbse.suite_generation.utils;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestChromosome;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestChromosomeGenerator;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.JavaStatement;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.JavaStatement.FieldAssignmentStatement;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.JavaStatement.InitializationStatement;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.JavaStatement.MethodCallStatement;
import de.uni_passau.fim.se2.sbse.suite_generation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.IBranch;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.BranchTracer;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.Branch;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import static java.util.Objects.requireNonNull;


/**
 * Utility class for common operations used in test suite generation.
 */
public class Utils {

    /**
     * Initializes a population of test chromosomes.
     *
     * @param size      The size of the population to generate.
     * @param generator The generator for creating random test chromosomes.
     * @return A list of initialized test chromosomes.
     */
    public static List<TestChromosome> initializePopulation(int size, TestChromosomeGenerator generator) {
        List<TestChromosome> population = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            population.add(generator.get());
        }
        return population;
    }
    /**
 * Logs debug information to the console.
 *
 * @param message the message to log
 */
private static void logDebug(String message) {
    System.out.println("[DEBUG] " + message);
}


    /**
     * Evaluates the fitness of each test chromosome in a population against all target branches.
     *
     * @param population       The population of test chromosomes.
     * @param targetBranches   The branches to evaluate.
     * @param fitnessFunctions A mapping of branches to their corresponding fitness functions.
     * @return A mapping of test chromosomes to their fitness values for all branches.
     */
    /**
 * Evaluates the fitness of each test chromosome in a population against all target branches.
 *
 * @param population       The population of test chromosomes.
 * @param targetBranches   The branches to evaluate.
 * @param fitnessFunctions A mapping of branches to their corresponding fitness functions.
 * @return A mapping of test chromosomes to their fitness values for all branches.
 */
public static Map<TestChromosome, Map<Branch, Double>> evaluateFitness(
    List<TestChromosome> population,
    List<Branch> targetBranches,
    Map<Branch, FitnessFunction<TestChromosome>> fitnessFunctions
) {
    Map<TestChromosome, Map<Branch, Double>> fitnessMap = new HashMap<>();
    Map<Branch, Map<TestChromosome, Double>> fitnessCache = new HashMap<>();

    logDebug("Evaluating fitness for population size: " + population.size());
    logDebug("Target branches size: " + targetBranches.size());

    for (int i = 0; i < population.size(); i++) {
        TestChromosome chromosome = population.get(i);
        logDebug("Evaluating chromosome " + i + ": " + chromosome);

        Map<Branch, Double> branchFitness = new HashMap<>();
        for (Branch branch : targetBranches) {
            logDebug("Evaluating fitness for branch: " + branch);

            fitnessCache.putIfAbsent(branch, new HashMap<>());

            if (!fitnessCache.get(branch).containsKey(chromosome)) {
                logDebug("Fitness not cached for chromosome and branch. Calculating...");
                double fitness = fitnessFunctions.get(branch).applyAsDouble(chromosome);
                logDebug("Calculated fitness: " + fitness);
                fitnessCache.get(branch).put(chromosome, fitness);
            } else {
                logDebug("Fitness retrieved from cache.");
            }

            branchFitness.put(branch, fitnessCache.get(branch).get(chromosome));
        }
        fitnessMap.put(chromosome, branchFitness);
        logDebug("Fitness for chromosome " + i + ": " + branchFitness);
    }

    logDebug("Fitness evaluation complete for population.");
    return fitnessMap;
}

    /**
     * Extracts constructors, fields, and methods from a class under test and creates valid statement representations.
     *
     * @param classUnderTest The class from which statements are derived.
     * @return A list of valid JavaStatement representations.
     */
    public static List<JavaStatement> allStatements(Class<?> classUnderTest) {
        List<JavaStatement> allStatements = new ArrayList<>();
        try {
            Constructor<?>[] constructors = classUnderTest.getDeclaredConstructors();

            // Add initialization statements
            for (Constructor<?> constructor : constructors) {
                if (Modifier.isPublic(constructor.getModifiers())) {
                    try {
                        Object[] params = generateRandomParameters(constructor.getParameterTypes());
                        constructor.setAccessible(true);
                        Object instance = constructor.newInstance(params);
                        allStatements.add(new InitializationStatement(instance, classUnderTest.getName(), params));
                    } catch (Exception e) {
                        System.err.println("Failed to process constructor: " + constructor.getName());
                        e.printStackTrace();
                    }
                }
            }

            // Add field assignment statements
            Field[] fields = classUnderTest.getDeclaredFields();
            for (Field field : fields) {
                if (!Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) {
                    try {
                        Object value = generateRandomValue(field.getType());
                        field.setAccessible(true);
                        allStatements.add(new FieldAssignmentStatement(null, field, value));
                    } catch (Exception e) {
                        System.err.println("Failed to process field: " + field.getName());
                        e.printStackTrace();
                    }
                }
            }

            // Add method call statements
            Method[] methods = classUnderTest.getDeclaredMethods();
            for (Method method : methods) {
                if (Modifier.isPublic(method.getModifiers())) {
                    try {
                        Object[] params = generateRandomParameters(method.getParameterTypes());
                        allStatements.add(new MethodCallStatement(null, method, params));
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
        return allStatements;
    }

    /**
     * Generates random parameters for the given parameter types.
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
        Random random = new Random();

        if (type == int.class || type == Integer.class) {
            return random.nextInt(2048) - 1024; // Range: -1024 to 1023
        } else if (type == double.class || type == Double.class) {
            return random.nextDouble() * 2048 - 1024;
        } else if (type == long.class || type == Long.class) {
            return random.nextLong() % 2048 - 1024;
        } else if (type == float.class || type == Float.class) {
            return random.nextFloat() * 2048 - 1024;
        } else if (type == char.class || type == Character.class) {
            return (char) (random.nextInt(95) + 32); // Printable ASCII
        } else if (type == boolean.class || type == Boolean.class) {
            return random.nextBoolean();
        } else if (type == String.class) {
            int length = random.nextInt(10) + 1;
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                sb.append((char) (random.nextInt(95) + 32));
            }
            return sb.toString();
        } else {
            return null;
        }
    }
    /**
     * Updates the archive of best solutions by evaluating and retaining only non-dominated solutions.
     *
     * @param population       the current generation of test chromosomes
     * @param fitnessMap       a map of chromosomes to their fitness values for each target branch
     * @param archive          the archive of best solutions (to be updated)
     * @param targetBranches   the list of branches targeted for coverage
     */
    public static void updateArchive(
        List<TestChromosome> population,
        Map<TestChromosome, Map<Branch, Double>> fitnessMap,
        List<TestChromosome> archive,
        List<Branch> targetBranches
    ) {
        requireNonNull(population, "Population must not be null.");
        requireNonNull(fitnessMap, "Fitness map must not be null.");
        requireNonNull(archive, "Archive must not be null.");
        requireNonNull(targetBranches, "Target branches must not be null.");
    
        logDebug("Starting archive update...");
        logDebug("Population size: " + population.size());
        logDebug("Archive size before update: " + archive.size());
    
        // Combine the current population with the archive
        List<TestChromosome> combined = new ArrayList<>(population);
        combined.addAll(archive);
        logDebug("Combined size (population + archive): " + combined.size());
    
        // Perform non-dominated sorting to find the best solutions
        logDebug("Performing non-dominated sorting...");
        List<List<TestChromosome>> fronts = nonDominatedSorting(combined, fitnessMap, targetBranches);
        logDebug("Non-dominated sorting complete. Number of fronts: " + fronts.size());
    
        // Clear the archive and add only the non-dominated front
        archive.clear();
        if (!fronts.isEmpty()) {
            logDebug("Adding non-dominated front to archive. Front size: " + fronts.get(0).size());
            archive.addAll(fronts.get(0)); // Add the Pareto front to the archive
        }
        logDebug("Archive size after update: " + archive.size());
        logDebug("Archive update complete.");
    }
    
    

    /**
     * Performs non-dominated sorting to organize chromosomes into Pareto fronts.
     *
     * @param population     the population of test chromosomes
     * @param fitnessMap     a map of chromosomes to their fitness values for each target branch
     * @param targetBranches the list of branches targeted for coverage
     * @return a list of Pareto fronts, each front being a list of non-dominated solutions
     */
    public static List<List<TestChromosome>> nonDominatedSorting(
    List<TestChromosome> population,
    Map<TestChromosome, Map<Branch, Double>> fitnessMap,
    List<Branch> targetBranches
) {
    logDebug("Performing non-dominated sorting on combined population of size: " + population.size());
    
    List<List<TestChromosome>> fronts = new ArrayList<>();
    Map<TestChromosome, Integer> dominationCount = new HashMap<>();
    Map<TestChromosome, List<TestChromosome>> dominatedBy = new HashMap<>();

    for (TestChromosome p : population) {
        dominationCount.put(p, 0);
        dominatedBy.put(p, new ArrayList<>());

        for (TestChromosome q : population) {
            if (dominates(p, q, fitnessMap, targetBranches)) {
                dominatedBy.get(p).add(q);
            } else if (dominates(q, p, fitnessMap, targetBranches)) {
                dominationCount.put(p, dominationCount.get(p) + 1);
            }
        }

        if (dominationCount.get(p) == 0) {
            if (fronts.isEmpty()) {
                fronts.add(new ArrayList<>());
            }
            fronts.get(0).add(p);
        }
    }

    int frontIndex = 0;
    while (frontIndex < fronts.size()) {
        logDebug("Processing front index: " + frontIndex + " with " + fronts.get(frontIndex).size() + " chromosomes.");
        
        List<TestChromosome> nextFront = new ArrayList<>();
        for (TestChromosome p : fronts.get(frontIndex)) {
            for (TestChromosome q : dominatedBy.get(p)) {
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

    logDebug("Non-dominated sorting complete. Total number of fronts: " + fronts.size());
    return fronts;
}




    /**
     * Determines whether chromosome `p` dominates chromosome `q`.
     *
     * @param p              the first chromosome
     * @param q              the second chromosome
     * @param fitnessMap     a map of chromosomes to their fitness values for each target branch
     * @param targetBranches the list of branches targeted for coverage
     * @return true if `p` dominates `q`, false otherwise
     */
    private static boolean dominates(
        TestChromosome p,
        TestChromosome q,
        Map<TestChromosome, Map<Branch, Double>> fitnessMap,
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
    
}
