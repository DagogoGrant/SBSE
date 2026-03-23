import static org.junit.jupiter.api.Assertions.*;

import de.uni_passau.fim.se2.se.hillclimbing.FitnessFunction;
import java.util.*;
import org.junit.jupiter.api.Test;

public class FitnessFunctionTest {
  @Test
  public void testCalculateMakespan_EmptySchedule() {
    List<List<Integer>> schedule = Arrays.asList(new ArrayList<>(), new ArrayList<>());
    int makespan = new FitnessFunction().calculateMakespan(schedule);
    assertEquals(0, makespan);
  }

  @Test
  public void testCalculateMakespan_NonEmptySchedule() {
    List<List<Integer>> schedule =
        Arrays.asList(new ArrayList<>(Arrays.asList(1, 2)), new ArrayList<>(Arrays.asList(3)));
    int makespan = new FitnessFunction().calculateMakespan(schedule);
    assertEquals(3, makespan);
  }
}
