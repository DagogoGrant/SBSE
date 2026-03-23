package de.uni_passau.fim.se2.se.test_prioritisation;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for Main.
 */
public class MainTest {

    @Test
    void testInvalidClassArgument() {
        // Simulate invalid command-line arguments
        String[] args = {"-c=", "-f=100"};
        CommandLine cmd = new CommandLine(new Main());
        int exitCode = cmd.execute(args);
        // Picocli should return an exit code of 2 for validation errors
        assertEquals(2, exitCode, "Invalid arguments should result in an error.");
    }

    @Test
    void testValidArgs() {
        // Simulate valid command-line arguments with a supported algorithm
        String[] args = {"-c=TestClass", "-f=100", "-r=5", "RANDOM_SEARCH"};
        CommandLine cmd = new CommandLine(new Main());
        int exitCode = cmd.execute(args);
        // Picocli should return an exit code of 0 for successful execution
        assertEquals(0, exitCode, "Valid arguments should execute successfully.");
    }

    @Test
    void testUnsupportedAlgorithm() {
        // Simulate arguments with an unsupported algorithm
        String[] args = {"-c=TestClass", "-f=100", "-r=5", "INVALID_ALGO"};
        CommandLine cmd = new CommandLine(new Main());
        int exitCode = cmd.execute(args);
        // Expect an error exit code
        assertEquals(1, exitCode, "Unsupported algorithm should result in an error.");
    }
}
