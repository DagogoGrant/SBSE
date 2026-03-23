package de.uni_passau.fim.se2.se.hillclimbing;

import java.util.ArrayList;
import java.util.List;

/** Generates random schedules for the hill climbing algorithm. */
public class ScheduleGenerator {

  public ScheduleGenerator() {
    // No random seed needed as random instance is not used anymore
  }

  /**
   * Generates a balanced initial schedule for the given jobs and number of machines.
   *
   * @param jobs the jobs to schedule
   * @param numMachines the number of machines
   * @return a balanced initial schedule
   */
  public List<List<Integer>> generateSchedule(int[] jobs, int numMachines) {
    // Edge case: if there are no jobs or no machines, return an empty schedule
    if (jobs == null || jobs.length == 0 || numMachines <= 0) {
      return new ArrayList<>();
    }

    // Initialize empty lists for each machine
    List<List<Integer>> schedule = new ArrayList<>();
    for (int i = 0; i < numMachines; i++) {
      schedule.add(new ArrayList<>());
    }

    // Assign jobs in a balanced way to minimize initial makespan differences
    int[] machineLoads = new int[numMachines];
    for (int job : jobs) {
      // Find the machine with the least load
      int minLoadIndex = 0;
      for (int i = 1; i < numMachines; i++) {
        if (machineLoads[i] < machineLoads[minLoadIndex]) {
          minLoadIndex = i;
        }
      }
      // Assign the job to the machine with the least load
      schedule.get(minLoadIndex).add(job);
      machineLoads[minLoadIndex] += job;
    }

    return schedule;
  }

  /**
   * Generate all neighbors of the current schedule by moving a single job from one machine to
   * another.
   *
   * @param schedule the current schedule
   * @return a list of all neighbors of the current schedule
   */
  public List<List<List<Integer>>> getNeighbors(List<List<Integer>> schedule) {
    List<List<List<Integer>>> neighbors = new ArrayList<>();

    // Edge case: if the schedule is null or empty, return an empty list of neighbors
    if (schedule == null || schedule.isEmpty()) {
      return neighbors;
    }

    // Iterate through all machines and jobs to generate neighbors
    for (int i = 0; i < schedule.size(); i++) {
      List<Integer> currentMachine = schedule.get(i);

      // Iterate through jobs in the current machine
      for (int j = 0; j < currentMachine.size(); j++) {
        int job = currentMachine.get(j);

        // Try moving the job to all other machines
        for (int k = 0; k < schedule.size(); k++) {
          if (i != k) {
            // Create a copy of the current schedule
            List<List<Integer>> newSchedule = deepCopySchedule(schedule);

            // Move the job from machine i to machine k
            newSchedule.get(i).remove(Integer.valueOf(job));
            newSchedule.get(k).add(job);

            // Add the new schedule to the list of neighbors
            neighbors.add(newSchedule);
          }
        }
      }
    }

    return neighbors;
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
