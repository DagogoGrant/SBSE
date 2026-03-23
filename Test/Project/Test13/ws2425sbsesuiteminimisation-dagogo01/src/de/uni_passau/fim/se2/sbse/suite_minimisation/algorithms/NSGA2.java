package de.uni_passau.fim.se2.sbse.suite_minimisation.algorithms;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosomeGenerator;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.selection.BinaryTournamentSelection;
import de.uni_passau.fim.se2.sbse.suite_minimisation.stopping_conditions.StoppingCondition;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A more complete implementation of the NSGA-II algorithm for multi-objective optimization,
 * specialized here for test-suite minimization (e.g., size) and coverage objectives.
 * 
 * <p>Key Steps:</p>
 * <ul>
 *     <li>Initialize population</li>
 *     <li>Evaluate fitness for each individual</li>
 *     <li>While stopping condition not met:
 *         <ul>
 *             <li>Non-dominated sort the population (split into multiple fronts)</li>
 *             <li>Calculate crowding distance within each front</li>
 *             <li>Generate offspring (selection -> crossover -> mutation)</li>
 *             <li>Combine old population + offspring</li>
 *             <li>Non-dominated sort + crowding-distance selection for the next generation</li>
 *         </ul>
 *     </li>
 * </ul>
 */
public class NSGA2 implements GeneticAlgorithm<TestSuiteChromosome> {

    /** Stopping condition (e.g., max generations, time-based, etc.). */
    private final StoppingCondition stoppingCondition;

    /** Mutation operator for introducing random variations. */
    private final Mutation<TestSuiteChromosome> mutation;

    /** Crossover operator for combining genetic material from two parents. */
    private final Crossover<TestSuiteChromosome> crossover;

    /** Selection operator (binary tournament in this case). */
    private final BinaryTournamentSelection<TestSuiteChromosome> selection;

    /** First objective fitness function, e.g. size minimization. */
    private final FitnessFunction<TestSuiteChromosome> sizeFitnessFunction;

    /** Second objective fitness function, e.g. coverage (also treated as minimization here). */
    private final FitnessFunction<TestSuiteChromosome> coverageFitnessFunction;

    /** The fixed population size. */
    private final int populationSize;

    /** Generator for creating initial chromosomes (test suites). */
    private final TestSuiteChromosomeGenerator generator;

    /**
     * Constructs an NSGA-II instance with the specified configuration.
     *
     * @param stoppingCondition       The stopping condition for the search.
     * @param mutation                The mutation operator.
     * @param crossover               The crossover operator.
     * @param selection               The selection mechanism (binary tournament).
     * @param sizeFitnessFunction     Fitness function for the first objective (size).
     * @param coverageFitnessFunction Fitness function for the second objective (coverage).
     * @param populationSize          Desired population size.
     * @param generator               Chromosome generator for initial population.
     */
    public NSGA2(
            StoppingCondition stoppingCondition,
            Mutation<TestSuiteChromosome> mutation,
            Crossover<TestSuiteChromosome> crossover,
            BinaryTournamentSelection<TestSuiteChromosome> selection,
            FitnessFunction<TestSuiteChromosome> sizeFitnessFunction,
            FitnessFunction<TestSuiteChromosome> coverageFitnessFunction,
            int populationSize,
            TestSuiteChromosomeGenerator generator) {

        if (populationSize <= 0) {
            throw new IllegalArgumentException("Population size must be greater than zero.");
        }
        if (generator == null) {
            throw new IllegalArgumentException("Chromosome generator cannot be null.");
        }

        this.stoppingCondition = stoppingCondition;
        this.mutation = mutation;
        this.crossover = crossover;
        this.selection = selection;
        this.sizeFitnessFunction = sizeFitnessFunction;
        this.coverageFitnessFunction = coverageFitnessFunction;
        this.populationSize = populationSize;
        this.generator = generator;
    }

    /**
     * Runs the NSGA-II algorithm until the stopping condition is met.
     *
     * @return Final population, potentially a set of non-dominated solutions.
     */
    @Override
    public List<TestSuiteChromosome> findSolution() {
        // Step 1: Initialize population
        List<TestSuiteChromosome> population = initializePopulation();

        // Step 2: Evaluate fitness of the initial population
        evaluateFitness(population);

        // Step 3: Evolution loop
        while (!stoppingCondition.searchMustStop()) {

            if (population.isEmpty()) {
                throw new IllegalStateException("Population is empty during evolution.");
            }

            // Step 3a: Non-dominated sorting (get the fronts)
            List<List<TestSuiteChromosome>> fronts = nonDominatedSorting(population);

            // Step 3b: Calculate crowding distances in each front
            calculateCrowdingDistances(fronts);

            // Step 3c: Generate offspring by (selection -> crossover -> mutation)
            List<TestSuiteChromosome> offspring = generateOffspring(fronts);

            // Evaluate fitness of all newly created offspring
            evaluateFitness(offspring);

            // Step 3d: Combine population + offspring => pick the best new population
            population = updatePopulation(population, offspring);

            // Notify stopping condition of # of new evaluations
            stoppingCondition.notifyFitnessEvaluations(offspring.size());
        }

        return population;
    }

    /**
     * Creates the initial population by generating chromosomes (test suites).
     */
    private List<TestSuiteChromosome> initializePopulation() {
        List<TestSuiteChromosome> population = new ArrayList<>(populationSize);
        for (int i = 0; i < populationSize; i++) {
            TestSuiteChromosome chromosome = generator.get();
            if (chromosome == null) {
                throw new IllegalStateException("Chromosome generator produced a null chromosome.");
            }
            population.add(chromosome);
        }
        if (population.isEmpty()) {
            throw new IllegalStateException("Population is empty after initialization.");
        }
        return population;
    }

    /**
     * Evaluates the fitness of each chromosome for the two objectives 
     * (assuming both are to be minimized in this code).
     */
    private void evaluateFitness(List<TestSuiteChromosome> population) {
        for (TestSuiteChromosome chromosome : population) {
            double sizeFitness = sizeFitnessFunction.applyAsDouble(chromosome);
            double coverageFitness = coverageFitnessFunction.applyAsDouble(chromosome);

            // objective(0) = size
            chromosome.setObjective(0, sizeFitness);

            // objective(1) = coverage (treated as minimization here)
            chromosome.setObjective(1, coverageFitness);

            // Reset crowding distance each time we re-evaluate
            chromosome.setCrowdingDistance(0.0);
        }
    }

    /**
     * Fully implemented non-dominated sorting. Splits the population into several fronts.
     * 
     * <p>A chromosome p "dominates" chromosome q if p is at least as good
     * as q in all objectives and strictly better in at least one objective.</p>
     *
     * @param population The current population to sort.
     * @return A list of Pareto fronts (each front is a list of chromosomes).
     */
    protected List<List<TestSuiteChromosome>> nonDominatedSorting(List<TestSuiteChromosome> population) {
        // Store for each chromosome:
        // 1) The set of chromosomes it dominates
        // 2) The number of chromosomes that dominate it
        Map<TestSuiteChromosome, List<TestSuiteChromosome>> dominatedSet = new HashMap<>();
        Map<TestSuiteChromosome, Integer> dominatingCount = new HashMap<>();

        // Initialize
        for (TestSuiteChromosome c : population) {
            dominatedSet.put(c, new ArrayList<>());
            dominatingCount.put(c, 0);
        }

        // For each pair (p, q) in population, check if p dominates q or q dominates p
        for (int i = 0; i < population.size(); i++) {
            for (int j = i + 1; j < population.size(); j++) {
                TestSuiteChromosome p = population.get(i);
                TestSuiteChromosome q = population.get(j);

                if (dominates(p, q)) {
                    dominatedSet.get(p).add(q);
                    dominatingCount.put(q, dominatingCount.get(q) + 1);
                } else if (dominates(q, p)) {
                    dominatedSet.get(q).add(p);
                    dominatingCount.put(p, dominatingCount.get(p) + 1);
                }
            }
        }

        // The first front will contain all solutions not dominated by any other
        List<List<TestSuiteChromosome>> fronts = new ArrayList<>();
        List<TestSuiteChromosome> firstFront = new ArrayList<>();
        for (TestSuiteChromosome c : population) {
            if (dominatingCount.get(c) == 0) {
                firstFront.add(c);
            }
        }
        fronts.add(firstFront);

        // Build subsequent fronts
        int i = 0;
        while (i < fronts.size()) {
            List<TestSuiteChromosome> nextFront = new ArrayList<>();
            for (TestSuiteChromosome p : fronts.get(i)) {
                // For each q dominated by p
                for (TestSuiteChromosome q : dominatedSet.get(p)) {
                    int count = dominatingCount.get(q) - 1;
                    dominatingCount.put(q, count);
                    if (count == 0) {
                        nextFront.add(q);
                    }
                }
            }
            if (!nextFront.isEmpty()) {
                fronts.add(nextFront);
            }
            i++;
        }
        return fronts;
    }

    /**
     * Checks if chromosome p dominates chromosome q.
     * <p>We assume both objectives are to be minimized here. 
     * Adjust if the second objective is to be maximized (or any other scenario).</p>
     *
     * @param p The potential dominator.
     * @param q The chromosome that might be dominated.
     * @return True if p dominates q.
     */
    private boolean dominates(TestSuiteChromosome p, TestSuiteChromosome q) {
        // "p dominates q" if:
        // 1) p is no worse than q in every objective
        // 2) p is strictly better in at least one objective

        boolean strictlyBetterInOne = false;

        // We have 2 objectives: 0 => size, 1 => coverage (both minimized in this example)
        for (int i = 0; i < 2; i++) {
            double pVal = p.getObjective(i);
            double qVal = q.getObjective(i);

            // If p is worse in any objective, no domination
            if (pVal > qVal) {
                return false;
            }
            // Check if strictly better in at least one
            if (pVal < qVal) {
                strictlyBetterInOne = true;
            }
        }
        return strictlyBetterInOne;
    }

    /**
     * Calculates the crowding distance for each chromosome within each front.
     * A higher distance indicates a less crowded solution (more "isolated").
     *
     * @param fronts The list of Pareto fronts in the current population.
     */
    protected void calculateCrowdingDistances(List<List<TestSuiteChromosome>> fronts) {
        for (List<TestSuiteChromosome> front : fronts) {
            if (front.isEmpty()) {
                continue;
            }
    
            int numObjectives = 2; // or however many you truly have
            int frontSize = front.size();
    
            // Reset crowding distance
            for (TestSuiteChromosome c : front) {
                c.setCrowdingDistance(0.0);
            }
    
            for (int i = 0; i < numObjectives; i++) {
                final int objIndex = i;
    
                // Create a mutable copy for sorting
                List<TestSuiteChromosome> sortedFront = new ArrayList<>(front);
                sortedFront.sort((a, b) -> Double.compare(a.getObjective(objIndex), b.getObjective(objIndex)));
    
                // Mark boundary solutions
                sortedFront.get(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
                sortedFront.get(frontSize - 1).setCrowdingDistance(Double.POSITIVE_INFINITY);
    
                double minVal = sortedFront.get(0).getObjective(objIndex);
                double maxVal = sortedFront.get(frontSize - 1).getObjective(objIndex);
                double range = maxVal - minVal;
                if (range == 0.0) {
                    // If all values are the same, skip
                    continue;
                }
    
                for (int j = 1; j < frontSize - 1; j++) {
                    double dist = sortedFront.get(j).getCrowdingDistance();
                    double nextVal = sortedFront.get(j + 1).getObjective(objIndex);
                    double prevVal = sortedFront.get(j - 1).getObjective(objIndex);
    
                    dist += (nextVal - prevVal) / range;
                    sortedFront.get(j).setCrowdingDistance(dist);
                }
            }
        }
    }

    /**
     * Generates offspring population of size = populationSize 
     * by repeated selection, crossover, and mutation.
     *
     * @param fronts The list of Pareto fronts from the current population.
     * @return Offspring population (each child mutated, etc.).
     */
    /**
 * Generates offspring population of size = populationSize 
 * by repeated selection, crossover, and mutation, 
 * with fallback if too many null children occur.
 */
private List<TestSuiteChromosome> generateOffspring(List<List<TestSuiteChromosome>> fronts) {
    List<TestSuiteChromosome> offspring = new ArrayList<>();
    List<TestSuiteChromosome> allChromosomes = flatten(fronts);

    if (allChromosomes.isEmpty()) {
        throw new IllegalStateException("No chromosomes available for selection.");
    }

    while (offspring.size() < populationSize) {
        TestSuiteChromosome parent1 = selection.apply(allChromosomes);
        TestSuiteChromosome parent2 = selection.apply(allChromosomes);

        Pair<TestSuiteChromosome> children = parent1.crossover(parent2);

        TestSuiteChromosome child1 = children.getFst().mutate();
        TestSuiteChromosome child2 = children.getSnd().mutate();

        offspring.add(child1);
        if (offspring.size() < populationSize) {
            offspring.add(child2);
        }
    }
    return offspring;
}


    
    

    /**
     * Updates and (selects) the population for the next generation from the combined set
     * of the current population + offspring. 
     *
     * @param population Current population.
     * @param offspring  Newly created individuals.
     * @return The new population for the next generation.
     */
    private List<TestSuiteChromosome> updatePopulation(List<TestSuiteChromosome> population,
                                                       List<TestSuiteChromosome> offspring) {
        // Combine current population with offspring
        List<TestSuiteChromosome> combined = new ArrayList<>(population);
        combined.addAll(offspring);

        // Perform non-dominated sorting
        List<List<TestSuiteChromosome>> fronts = nonDominatedSorting(combined);
        // Calculate crowding distances for the combined set (organized by front)
        calculateCrowdingDistances(fronts);

        // Build the next generation up to populationSize
        List<TestSuiteChromosome> nextGen = new ArrayList<>(populationSize);
        for (List<TestSuiteChromosome> front : fronts) {
            if (nextGen.size() + front.size() <= populationSize) {
                // If we can fit the entire front, take it
                nextGen.addAll(front);
            } else {
                // Otherwise, sort that front by descending crowding distance
                front.sort((a, b) -> Double.compare(b.getCrowdingDistance(), a.getCrowdingDistance()));
                int slotsLeft = populationSize - nextGen.size();
                nextGen.addAll(front.subList(0, slotsLeft));
                break;
            }
        }

        return nextGen;
    }

    /**
     * Flattens the list of fronts into a single list of chromosomes.
     */
    private List<TestSuiteChromosome> flatten(List<List<TestSuiteChromosome>> fronts) {
        List<TestSuiteChromosome> all = new ArrayList<>();
        for (List<TestSuiteChromosome> f : fronts) {
            all.addAll(f);
        }
        return all;
    }

    /**
     * Accessor for the stopping condition used by this algorithm.
     *
     * @return The stopping condition.
     */
    @Override
    public StoppingCondition getStoppingCondition() {
        return stoppingCondition;
    }
}
