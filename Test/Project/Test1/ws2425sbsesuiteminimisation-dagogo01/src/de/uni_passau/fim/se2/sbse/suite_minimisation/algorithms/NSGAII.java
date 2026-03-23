package de.uni_passau.fim.se2.sbse.suite_minimisation.algorithms;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosomeGenerator;
import de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_minimisation.stopping_conditions.StoppingCondition;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Pair;
import de.uni_passau.fim.se2.sbse.suite_minimisation.selection.BinaryTournamentSelection;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;

import java.util.*;

/**
 * NSGA-II Algorithm for Test Suite Minimisation.
 */
public class NSGAII implements GeneticAlgorithm<TestSuiteChromosome> {

    private final TestSuiteChromosomeGenerator generator;
    private final FitnessFunction<TestSuiteChromosome> sizeFitnessFunction;
    private final FitnessFunction<TestSuiteChromosome> coverageFitnessFunction;
    private final StoppingCondition stoppingCondition;
    private final BinaryTournamentSelection<TestSuiteChromosome> selection;
    private final Crossover<TestSuiteChromosome> crossover;
    private final Mutation<TestSuiteChromosome> mutation;
    private final int populationSize;

    /**
     * Constructs an NSGA-II algorithm instance.
     */
    public NSGAII(TestSuiteChromosomeGenerator generator,
                  FitnessFunction<TestSuiteChromosome> sizeFitnessFunction,
                  FitnessFunction<TestSuiteChromosome> coverageFitnessFunction,
                  StoppingCondition stoppingCondition,
                  BinaryTournamentSelection<TestSuiteChromosome> selection,
                  Crossover<TestSuiteChromosome> crossover,
                  Mutation<TestSuiteChromosome> mutation,
                  int populationSize) {
        this.generator = generator;
        this.sizeFitnessFunction = sizeFitnessFunction;
        this.coverageFitnessFunction = coverageFitnessFunction;
        this.stoppingCondition = stoppingCondition;
        this.selection = selection;
        this.crossover = crossover;
        this.mutation = mutation;
        this.populationSize = populationSize;
    }

    @Override
    public List<TestSuiteChromosome> findSolution() {
        stoppingCondition.notifySearchStarted();

        // Initialize the population
        List<TestSuiteChromosome> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            population.add(generator.get());
        }

        while (!stoppingCondition.searchMustStop()) {
            evaluatePopulation(population);

            List<List<TestSuiteChromosome>> fronts = nondominatedSorting(population);

            Map<TestSuiteChromosome, Double> crowdingDistances = calculateCrowdingDistances(fronts);

            List<TestSuiteChromosome> offspring = generateOffspring(population);

            population = selectNextGeneration(fronts, crowdingDistances, offspring);
        }

        return nondominatedSorting(population).get(0);
    }

    @Override
    public StoppingCondition getStoppingCondition() {
        return stoppingCondition;
    }

    private void evaluatePopulation(List<TestSuiteChromosome> population) {
        for (TestSuiteChromosome chromosome : population) {
            sizeFitnessFunction.applyAsDouble(chromosome);
            coverageFitnessFunction.applyAsDouble(chromosome);
            stoppingCondition.notifyFitnessEvaluation();
        }
    }

    private List<List<TestSuiteChromosome>> nondominatedSorting(List<TestSuiteChromosome> population) {
        List<List<TestSuiteChromosome>> fronts = new ArrayList<>();
        List<TestSuiteChromosome> currentFront = new ArrayList<>();
        for (TestSuiteChromosome candidate : population) {
            boolean dominated = false;
            for (TestSuiteChromosome member : currentFront) {
                if (dominates(member, candidate)) {
                    dominated = true;
                    break;
                }
            }
            if (!dominated) {
                currentFront.add(candidate);
            }
        }
        fronts.add(currentFront);
        return fronts;
    }

    private Map<TestSuiteChromosome, Double> calculateCrowdingDistances(List<List<TestSuiteChromosome>> fronts) {
        Map<TestSuiteChromosome, Double> crowdingDistances = new HashMap<>();

        for (List<TestSuiteChromosome> front : fronts) {
            int n = front.size();
            if (n == 0) continue;

            for (TestSuiteChromosome chromosome : front) {
                crowdingDistances.put(chromosome, 0.0);
            }

            for (FitnessFunction<TestSuiteChromosome> fitnessFunction : List.of(sizeFitnessFunction, coverageFitnessFunction)) {
                front.sort(Comparator.comparingDouble(fitnessFunction::applyAsDouble));

                crowdingDistances.put(front.get(0), Double.POSITIVE_INFINITY);
                crowdingDistances.put(front.get(n - 1), Double.POSITIVE_INFINITY);

                for (int i = 1; i < n - 1; i++) {
                    double distance = crowdingDistances.get(front.get(i));
                    double next = fitnessFunction.applyAsDouble(front.get(i + 1));
                    double prev = fitnessFunction.applyAsDouble(front.get(i - 1));
                    distance += (next - prev);
                    crowdingDistances.put(front.get(i), distance);
                }
            }
        }

        return crowdingDistances;
    }

    private List<TestSuiteChromosome> generateOffspring(List<TestSuiteChromosome> population) {
        List<TestSuiteChromosome> offspring = new ArrayList<>();
        while (offspring.size() < populationSize) {
    TestSuiteChromosome parent1 = selection.apply(population);
    TestSuiteChromosome parent2 = selection.apply(population);
    Pair<TestSuiteChromosome> offspringPair = crossover.apply(parent1, parent2);
    offspring.addAll(List.of(offspringPair.getFst(), offspringPair.getSnd()));
}

        for (int i = 0; i < offspring.size(); i++) {
            offspring.set(i, mutation.apply(offspring.get(i)));
        }
        return offspring;
    }

    private List<TestSuiteChromosome> selectNextGeneration(List<List<TestSuiteChromosome>> fronts,
                                                           Map<TestSuiteChromosome, Double> crowdingDistances,
                                                           List<TestSuiteChromosome> offspring) {
        List<TestSuiteChromosome> nextGeneration = new ArrayList<>();
        List<TestSuiteChromosome> combinedPopulation = new ArrayList<>(offspring);
        combinedPopulation.addAll(nextGeneration);

        for (List<TestSuiteChromosome> front : fronts) {
            front.sort(Comparator.comparingDouble(crowdingDistances::get).reversed());
            for (TestSuiteChromosome chromosome : front) {
                if (nextGeneration.size() < populationSize) {
                    nextGeneration.add(chromosome);
                } else {
                    return nextGeneration;
                }
            }
        }
        return nextGeneration;
    }

    private boolean dominates(TestSuiteChromosome a, TestSuiteChromosome b) {
        double sizeA = sizeFitnessFunction.applyAsDouble(a);
        double coverageA = coverageFitnessFunction.applyAsDouble(a);
        double sizeB = sizeFitnessFunction.applyAsDouble(b);
        double coverageB = coverageFitnessFunction.applyAsDouble(b);
        return (sizeA <= sizeB && coverageA >= coverageB) &&
                (sizeA < sizeB || coverageA > coverageB);
    }
}
