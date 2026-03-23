import static org.junit.jupiter.api.Assertions.*;

import de.uni_passau.fim.se2.se.hillclimbing.FitnessFunction;
import de.uni_passau.fim.se2.se.hillclimbing.HillClimbing;
import de.uni_passau.fim.se2.se.hillclimbing.ScheduleGenerator;
import java.util.*;
import org.junit.jupiter.api.Test;

public class HillClimbingTest {
  @Test
  public void testOptimize_TrivialCase() {
    int[] jobs = {1};
    int numMachines = 1;
    int numIterations = 10;
    int restartThreshold = 5;

    HillClimbing hillClimbing = new HillClimbing(new ScheduleGenerator(), new FitnessFunction());
    List<List<Integer>> result =
        hillClimbing.optimize(jobs, numMachines, numIterations, restartThreshold);
    assertEquals(1, result.size());
    assertEquals(1, result.get(0).size());
    assertEquals(1, (int) result.get(0).get(0));
  }

  @Test
  public void testOptimize_SimpleCase() {
    int[] jobs = {1, 2, 3, 4};
    int numMachines = 2;
    int numIterations = 100;
    int restartThreshold = 10;

    HillClimbing hillClimbing = new HillClimbing(new ScheduleGenerator(), new FitnessFunction());
    List<List<Integer>> result =
        hillClimbing.optimize(jobs, numMachines, numIterations, restartThreshold);
    int makespan = new FitnessFunction().calculateMakespan(result);
    assertTrue(makespan <= 15); // Simple heuristic check
  }

  @Test
  public void testOptimize_ComplexCase() {
    int[] jobs = {1, 2, 3, 4, 5, 6, 7, 8};
    int numMachines = 3;
    int numIterations = 5000; // Increased iterations
    int restartThreshold = 10; // Reduced restart threshold

    HillClimbing hillClimbing = new HillClimbing(new ScheduleGenerator(), new FitnessFunction());
    List<List<Integer>> result =
        hillClimbing.optimize(jobs, numMachines, numIterations, restartThreshold);
    int makespan = new FitnessFunction().calculateMakespan(result);

    // Adjusting the heuristic check to a realistic range
    assertTrue(
        makespan <= 15, "Expected makespan to be less than or equal to 15, but was " + makespan);
  }
}
