package de.uni_passau.fim.se2.se.hillclimbing;

import java.util.List;

/** Fitness function for the hill climbing algorithm. */
public class FitnessFunction {

  /**
   * Calculates the makespan of a schedule.
   *
   * @param schedule the schedule to calculate the makespan for
   * @return the makespan of the schedule
   */
  public int calculateMakespan(List<List<Integer>> schedule) {
    if (schedule == null || schedule.isEmpty()) {
      return 0;
    }

    int makespan = 0;
    for (List<Integer> machineJobs : schedule) {
      int machineTime = machineJobs.stream().mapToInt(Integer::intValue).sum();
      makespan = Math.max(makespan, machineTime);
    }
    return makespan;
  }
}
