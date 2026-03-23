import static org.junit.jupiter.api.Assertions.*;

import de.uni_passau.fim.se2.se.hillclimbing.ScheduleGenerator;
import java.util.*;
import org.junit.jupiter.api.Test;

public class ScheduleGeneratorTest {
  @Test
  public void testGenerateInitialSchedule_NoJobs() {
    int[] jobs = {};
    List<List<Integer>> schedule = new ScheduleGenerator().generateSchedule(jobs, 0);
    assertEquals(0, schedule.size());
  }

  @Test
  public void testGenerateInitialSchedule_SingleJob() {
    int[] jobs = {1};
    List<List<Integer>> schedule = new ScheduleGenerator().generateSchedule(jobs, 1);
    assertEquals(1, schedule.size());
    assertEquals(1, schedule.get(0).size());
    assertEquals(1, (int) schedule.get(0).get(0));
  }

  @Test
  public void testGenerateInitialSchedule_ManyJobs() {
    int[] jobs = {1, 2, 3, 4, 5};
    List<List<Integer>> schedule = new ScheduleGenerator().generateSchedule(jobs, 3);
    assertEquals(3, schedule.size());
    int totalJobs = schedule.stream().mapToInt(List::size).sum();
    assertEquals(5, totalJobs);
  }

  @Test
  public void testGenerateNeighbours_EmptySchedule() {
    List<List<Integer>> schedule = Arrays.asList(new ArrayList<>(), new ArrayList<>());
    List<List<List<Integer>>> neighbours = new ScheduleGenerator().getNeighbors(schedule);
    assertEquals(0, neighbours.size());
  }

  @Test
  public void testGenerateNeighbours_SingleJob() {
    List<List<Integer>> schedule =
        Arrays.asList(new ArrayList<>(Arrays.asList(1)), new ArrayList<>());
    List<List<List<Integer>>> neighbours = new ScheduleGenerator().getNeighbors(schedule);
    assertEquals(1, neighbours.size());
  }

  @Test
  public void testGenerateNeighbours_MultipleMachinesJobs() {
    List<List<Integer>> schedule =
        Arrays.asList(new ArrayList<>(Arrays.asList(1)), new ArrayList<>(Arrays.asList(2, 3)));
    List<List<List<Integer>>> neighbours = new ScheduleGenerator().getNeighbors(schedule);
    assertEquals(3, neighbours.size());
  }
}
