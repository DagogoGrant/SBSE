package de.uni_passau.fim.se2.se.hillclimbing;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Hill climbing algorithm with restarts to solve the job scheduling problem. */
public final class HillClimbing {

  private final ScheduleGenerator generator;
  private final FitnessFunction fitnessFunction;
  private final Random random;

  public HillClimbing(ScheduleGenerator generator, FitnessFunction fitnessFunction) {
    this.generator = generator;
    this.fitnessFunction = fitnessFunction;
    this.random = new Random(42); // Using a fixed seed for repeatability
  }

  /**
   * Optimize the job scheduling problem using the hill climbing algorithm with restarts.
   *
   * @param jobs the jobs to schedule
   * @param numMachines the number of machines
   * @param maxIterations the maximum number of iterations
   * @param restartThreshold the number of iterations without improvement before a restart is
   *     triggered
   * @return the optimal schedule
   */
  public List<List<Integer>> optimize(
      int[] jobs, int numMachines, int maxIterations, int restartThreshold) {
    if (jobs.length == 0 || numMachines <= 0) {
      return new ArrayList<>();
    }

    List<List<Integer>> currentSolution = generator.generateSchedule(jobs, numMachines);
    int currentMakespan = fitnessFunction.calculateMakespan(currentSolution);
    int iterationsWithoutImprovement = 0;
    int dynamicRestartThreshold = restartThreshold;
    double acceptanceProbability = 0.3; // Initial acceptance probability for worse solutions

    for (int iteration = 0; iteration < maxIterations; iteration++) {
      List<List<List<Integer>>> neighbours = generator.getNeighbors(currentSolution);
      List<List<Integer>> bestNeighbour = null;
      int bestNeighbourMakespan = Integer.MAX_VALUE;

      // Consider a subset of neighbors for evaluation
      int subsetSize = Math.min(3, neighbours.size());
      for (int i = 0; i < subsetSize; i++) {
        List<List<Integer>> neighbour = neighbours.get(random.nextInt(neighbours.size()));
        int makespan = fitnessFunction.calculateMakespan(neighbour);
        if (makespan < bestNeighbourMakespan) {
          bestNeighbour = neighbour;
          bestNeighbourMakespan = makespan;
        }
      }

      // Introduce an adaptive probability to accept worse neighbors to escape local optima
      if (bestNeighbour != null
          && (bestNeighbourMakespan < currentMakespan
              || random.nextDouble() < acceptanceProbability)) {
        currentSolution = bestNeighbour;
        currentMakespan = bestNeighbourMakespan;
        iterationsWithoutImprovement = 0;
        dynamicRestartThreshold = restartThreshold; // Reset the restart threshold on improvement
        acceptanceProbability = 0.3; // Reset acceptance probability on improvement
      } else {
        iterationsWithoutImprovement++;
        // Gradually decrease the acceptance probability
        acceptanceProbability = Math.max(0.05, acceptanceProbability * 0.9);
      }

      // Restart if no progress is made for a certain threshold
      if (iterationsWithoutImprovement >= dynamicRestartThreshold) {
        // Partial restart: perturb the current solution by moving a few random jobs
        currentSolution = perturbSolution(currentSolution, jobs, numMachines);
        currentMakespan = fitnessFunction.calculateMakespan(currentSolution);
        iterationsWithoutImprovement = 0;
        // Adjust the restart threshold dynamically
        dynamicRestartThreshold = Math.max(5, dynamicRestartThreshold / 2);
        acceptanceProbability = 0.3; // Reset acceptance probability after restart
      }
    }
    return currentSolution;
  }

  // Helper method to perturb the current solution by moving a few random jobs
  private List<List<Integer>> perturbSolution(
      List<List<Integer>> solution, int[] jobs, int numMachines) {
    List<List<Integer>> perturbedSolution = deepCopySchedule(solution);
    int numPerturbations = Math.max(1, jobs.length / 20); // Perturb 5% of the jobs

    for (int i = 0; i < numPerturbations; i++) {
      int fromMachine = random.nextInt(numMachines);
      int toMachine = random.nextInt(numMachines);
      while (fromMachine == toMachine) {
        toMachine = random.nextInt(numMachines);
      }

      List<Integer> fromMachineJobs = perturbedSolution.get(fromMachine);
      if (!fromMachineJobs.isEmpty()) {
        int jobIndex = random.nextInt(fromMachineJobs.size());
        int job = fromMachineJobs.remove(jobIndex);
        perturbedSolution.get(toMachine).add(job);
      }
    }

    return perturbedSolution;
  }

  // Helper method to create a deep copy of the schedule
  private List<List<Integer>> deepCopySchedule(List<List<Integer>> schedule) {
    List<List<Integer>> newSchedule = new ArrayList<>();
    for (List<Integer> machine : schedule) {
      newSchedule.add(new ArrayList<>(machine));
    }
    return newSchedule;
  }
}
