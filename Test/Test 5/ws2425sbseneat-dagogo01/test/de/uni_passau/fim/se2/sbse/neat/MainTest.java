package de.uni_passau.fim.se2.sbse.neat;
import de.uni_passau.fim.se2.sbse.neat.algorithms.Neuroevolution;


import de.uni_passau.fim.se2.sbse.neat.environments.Tasks;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    private Main main;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        main = new Main();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void testMain_XOR_Task() {
        String[] args = {"-t", "XOR", "-p", "10", "-g", "5", "-r", "2"};
        int exitCode = new CommandLine(main).execute(args);
        assertEquals(0, exitCode);
        assertTrue(outContent.toString().contains("Analysing task 'XOR'"));
    }

    @Test
    void testMain_CartPole_Task() {
        String[] args = {"-t", "CART", "-p", "10", "-g", "5", "-r", "1"};
        int exitCode = new CommandLine(main).execute(args);
        assertEquals(0, exitCode);
        assertTrue(outContent.toString().contains("CARTPOLE"));
    }

//     @Test
// void testMain_InvalidTask() {
//     String[] args = {"-t", "INVALID"}; 
//     CommandLine cmd = new CommandLine(new Main());

//     int exitCode = cmd.execute(args);
//     String errorMessage = cmd.getExecutionResult().getErrorMessage(); // Check if error message exists

//     assertTrue(exitCode != 0, "Expected a non-zero exit code for an invalid task.");
//     assertNotNull(errorMessage, "Expected an error message for an invalid task.");
//     assertTrue(errorMessage.contains("not a valid reinforcement learning task"), "Unexpected error message.");
// }


    @Test
    void testTaskConverter() {
        TaskConverter converter = new TaskConverter();
        assertEquals(Tasks.XOR, converter.convert("XOR"));
        assertEquals(Tasks.CARTPOLE, converter.convert("CART"));
        assertEquals(Tasks.CARTPOLE_RANDOM, converter.convert("CART_RANDOM"));
        
        assertThrows(IllegalArgumentException.class, () -> converter.convert("INVALID"));
    }

    @Test
    void testInitialiseNeat() {
        Neuroevolution neat = Main.initialiseNeat(5, 10);
        assertNotNull(neat);
    }

    @Test
    void testSeedOption() {
        String[] args = {"-t", "XOR", "-s", "42", "-p", "5", "-g", "3", "-r", "1"};
        int exitCode = new CommandLine(main).execute(args);
        assertEquals(0, exitCode);
    }

    @Test
    void testMissingRequiredArgs() {
        String[] args = {"-p", "10", "-g", "5"}; // Missing the required task option
        int exitCode = new CommandLine(main).execute(args);
        assertNotEquals(0, exitCode);
    }

    @Test
    void testPrintResults() {
        String[] args = {"-t", "XOR", "-p", "5", "-g", "3", "-r", "1"};
        new CommandLine(main).execute(args);
        String output = outContent.toString();
        assertTrue(output.contains("Successful repetitions:"));
        assertTrue(output.contains("Average generations:"));
        assertTrue(output.contains("Max generations:"));
        assertTrue(output.contains("Average time per task (s):"));
    }
}
