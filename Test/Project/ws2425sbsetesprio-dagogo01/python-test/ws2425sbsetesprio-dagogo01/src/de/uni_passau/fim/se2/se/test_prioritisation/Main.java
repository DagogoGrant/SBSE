package de.uni_passau.fim.se2.se.test_prioritisation;

import picocli.CommandLine;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Main class for the Test Prioritisation application.
 */
@CommandLine.Command(
        name = "TestPrioritisation",
        description = "Optimizes test case prioritisation using various meta-heuristic algorithms.",
        mixinStandardHelpOptions = true
)
public class Main implements Callable<Integer> {

    private static final Set<String> SUPPORTED_ALGORITHMS = new HashSet<>(Arrays.asList(
            "RANDOM_SEARCH", "RANDOM_WALK", "SIMULATED_ANNEALING", "SIMPLE_GENETIC_ALGORITHM"
    ));

    @CommandLine.Option(
            names = {"-c", "--class"},
            description = "The name of the class under test.",
            required = true
    )
    private String testClass;

    @CommandLine.Option(
            names = {"-f", "--fitness"},
            description = "Number of fitness evaluations.",
            required = true
    )
    private int fitnessEvaluations;

    @CommandLine.Option(
            names = {"-r", "--runs"},
            description = "Number of independent runs.",
            defaultValue = "1"
    )
    private int runs;

    @CommandLine.Parameters(
            description = "Algorithms to use for optimization. Supported values: RANDOM_SEARCH, RANDOM_WALK, SIMULATED_ANNEALING, SIMPLE_GENETIC_ALGORITHM.",
            arity = "1..*"
    )
    private String[] algorithms;

    @Override
    public Integer call() {
        // Validate inputs
        if (!validateInputs()) {
            return 1; // Exit code 1 for input validation failure
        }

        // Display the configuration
        System.out.println("Test Prioritisation Starting...");
        System.out.printf("Class: %s, Fitness Evaluations: %d, Runs: %d, Algorithms: %s%n",
                testClass, fitnessEvaluations, runs, String.join(", ", algorithms));

        // Process each algorithm
        for (String algorithm : algorithms) {
            if (!processAlgorithm(algorithm)) {
                return 1; // Exit code 1 for unsupported algorithms
            }
        }

        System.out.println("Test Prioritisation Completed.");
        return 0; // Success
    }

    private boolean validateInputs() {
        if (fitnessEvaluations <= 0) {
            System.err.println("Fitness evaluations must be greater than 0.");
            return false;
        }
        if (runs <= 0) {
            System.err.println("Number of runs must be greater than 0.");
            return false;
        }
        for (String algorithm : algorithms) {
            if (!SUPPORTED_ALGORITHMS.contains(algorithm.toUpperCase())) {
                System.err.printf("Unsupported algorithm: %s%n", algorithm);
                return false;
            }
        }
        return true;
    }

    private boolean processAlgorithm(String algorithm) {
        switch (algorithm.toUpperCase()) {
            case "RANDOM_SEARCH":
                runRandomSearch();
                return true;
            case "RANDOM_WALK":
                runRandomWalk();
                return true;
            case "SIMULATED_ANNEALING":
                runSimulatedAnnealing();
                return true;
            case "SIMPLE_GENETIC_ALGORITHM":
                runGeneticAlgorithm();
                return true;
            default:
                System.err.printf("Unsupported algorithm: %s%n", algorithm);
                return false;
        }
    }

    private void runRandomSearch() {
        System.out.println("Running Random Search...");
        // Implement the logic for Random Search
    }

    private void runRandomWalk() {
        System.out.println("Running Random Walk...");
        // Implement the logic for Random Walk
    }

    private void runSimulatedAnnealing() {
        System.out.println("Running Simulated Annealing...");
        // Implement the logic for Simulated Annealing
    }

    private void runGeneticAlgorithm() {
        System.out.println("Running Genetic Algorithm...");
        // Implement the logic for Genetic Algorithm
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}
