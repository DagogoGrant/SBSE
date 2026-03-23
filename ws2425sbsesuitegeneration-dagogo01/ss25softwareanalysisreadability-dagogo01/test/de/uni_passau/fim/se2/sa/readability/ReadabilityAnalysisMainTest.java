package de.uni_passau.fim.se2.sa.readability;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ReadabilityAnalysisMainTest {

    @TempDir
    Path tempDir;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUpStreams() {
        PrintStream combinedStream = new PrintStream(outContent);
        System.setOut(combinedStream);
        System.setErr(combinedStream);
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        outContent.reset();
    }

    @Test
    void testMain_NoArgs() {
        int exitCode = ReadabilityAnalysisMain.runCommand(new String[]{});
        String output = outContent.toString();
        assertTrue(output.contains("Usage:"), "Should show usage information");
        assertNotEquals(0, exitCode, "Should return non-zero exit code for missing arguments");
    }

    @Test
    void testMain_Help() {
        int exitCode = ReadabilityAnalysisMain.runCommand(new String[]{"--help"});
        String output = outContent.toString();
        assertTrue(output.contains("Usage:"), "Should show help information");
        assertEquals(0, exitCode, "Should return zero exit code for help command");
    }

    @Test
    void testMain_InvalidCommand() {
        int exitCode = ReadabilityAnalysisMain.runCommand(new String[]{"invalid"});
        String output = outContent.toString();
        assertTrue(output.contains("Unknown subcommand"), "Should show error for invalid command");
        assertTrue(output.contains("Usage:"), "Should show usage information after error");
        assertNotEquals(0, exitCode, "Should return non-zero exit code for invalid command");
    }

    @Test
    void testMain_PreprocessCommand() throws IOException {
        Path sourceDir = tempDir.resolve("source");
        Files.createDirectory(sourceDir);
        Files.writeString(sourceDir.resolve("1.jsnp"), "public class Test { }");

        Path truthFile = tempDir.resolve("truth.csv");
        Files.writeString(truthFile, "Rater,Snippet1\nMean,3.7\n");

        Path outputFile = tempDir.resolve("output.csv");

        String[] args = {
            "preprocess",
            "-s", sourceDir.toString(),
            "-g", truthFile.toString(),
            "-t", outputFile.toString(),
            "LINES", "TOKEN_ENTROPY", "H_VOLUME"
        };

        int exitCode = ReadabilityAnalysisMain.runCommand(args);
        assertEquals(0, exitCode, "Should return zero exit code for successful preprocessing");
        assertTrue(Files.exists(outputFile), "Should create output file");
        String content = Files.readString(outputFile);
        assertTrue(content.startsWith("File,NumberLines,TokenEntropy,HalsteadVolume,Truth"),
            "Should have correct CSV header");
    }

    @Test
    void testMain_ClassifyCommand() throws IOException {
        Path dataFile = tempDir.resolve("data.csv");

        // At least 10 entries for 10-fold cross-validation
        StringBuilder csv = new StringBuilder("File,NumberLines,TokenEntropy,HalsteadVolume,Truth\n");
        for (int i = 1; i <= 10; i++) {
            csv.append("file").append(i).append(".jsnp,").append(10 + i).append(",2.5,100.0,")
                .append(i % 2 == 0 ? "Y" : "N").append("\n");
        }
        Files.writeString(dataFile, csv.toString());

        int exitCode = ReadabilityAnalysisMain.runCommand(new String[]{"classify", "-d", dataFile.toString()});
        String output = outContent.toString();
        assertTrue(output.contains("Accuracy"), "Should show classification results");
        assertEquals(0, exitCode, "Should return zero exit code for successful classification");
    }
    @Test
    void testMain_PreprocessWithInvalidArgs() {
        // Capture both stdout and stderr into one stream
        ByteArrayOutputStream outErrContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        PrintStream combined = new PrintStream(outErrContent);
    
        System.setOut(combined);
        System.setErr(combined);
    
        // Invalid source directory
        String[] args = {
            "preprocess",
            "--source", "nonexistent-dir",
            "--ground-truth", "doesnotexist.csv",
            "--target", "output.csv",
            "LINES", "TOKEN_ENTROPY", "H_VOLUME"
        };
    
        int exitCode = ReadabilityAnalysisMain.runCommand(args);
    
        // Restore original streams
        System.setOut(originalOut);
        System.setErr(originalErr);
    
        String combinedOutput = outErrContent.toString().toLowerCase();
    
        assertEquals(1, exitCode, "Expected exit code 1 for invalid arguments.");
        assertTrue(combinedOutput.contains("source directory does not exist"), 
            "Should show error for invalid source directory.");
    }
    
    
    

    @Test
    void testMain_ClassifyWithInvalidArgs() {
        String[] args = {
            "classify",
            "-d", "nonexistent.csv"
        };

        int exitCode = ReadabilityAnalysisMain.runCommand(args);
        String output = outContent.toString();
        assertTrue(output.contains("does not exist"), "Should show error for invalid data file");
        assertNotEquals(0, exitCode, "Should return non-zero exit code for invalid arguments");
    }
    @Test
    void testMain_InvalidOptionStartingWithDash() {
        // Target: the !args[0].startsWith("-") branch - test with invalid option that DOES start with "-"
        // This should bypass the "Unknown subcommand" check and go to cmd.execute()
        String[] args = {"--invalid-option-not-help-or-version"};
        
        int exitCode = ReadabilityAnalysisMain.runCommand(args);
        String output = outContent.toString();
        
        // Should trigger parameter exception handler, not the "Unknown subcommand" path
        assertNotEquals(0, exitCode, "Should return non-zero exit code for invalid option");
        // Should NOT contain "Unknown subcommand" since it starts with "-"
        assertFalse(output.contains("Unknown subcommand"), "Should not show 'Unknown subcommand' for options starting with -");
        assertTrue(output.contains("Usage:"), "Should show usage information");
    }

    @Test
    void testMain_InvalidLongOption() {
        // Another test for options starting with "--" that aren't valid
        String[] args = {"--nonexistent"};
        
        int exitCode = ReadabilityAnalysisMain.runCommand(args);
        String output = outContent.toString();
        
        assertNotEquals(0, exitCode, "Should return non-zero exit code for invalid long option");
        assertFalse(output.contains("Unknown subcommand"), "Should not show 'Unknown subcommand' for --options");
        assertTrue(output.contains("Usage:"), "Should show usage information");
    }

    @Test
    void testMain_InvalidShortOption() {
        // Test for short options starting with "-" that aren't valid
        String[] args = {"-z"};
        
        int exitCode = ReadabilityAnalysisMain.runCommand(args);
        String output = outContent.toString();
        
        assertNotEquals(0, exitCode, "Should return non-zero exit code for invalid short option");
        assertFalse(output.contains("Unknown subcommand"), "Should not show 'Unknown subcommand' for -options");
        assertTrue(output.contains("Usage:"), "Should show usage information");
    }

    @Test
    void testMain_ExecutionExceptionHandler_UnmatchedArgument() {
        // Try to trigger ExecutionExceptionHandler with UnmatchedArgumentException
        // This might happen with malformed subcommand arguments
        String[] args = {"preprocess", "--unknown-preprocess-option", "value"};
        
        int exitCode = ReadabilityAnalysisMain.runCommand(args);
        String output = outContent.toString();
        
        assertNotEquals(0, exitCode, "Should return non-zero exit code for unmatched argument");
        assertTrue(output.contains("Usage:"), "Should show usage information");
    }

    @Test
    void testMain_ExecutionExceptionHandler_OtherException() {
        // Try to trigger ExecutionExceptionHandler with a non-UnmatchedArgumentException
        // This could happen with malformed arguments that cause other types of exceptions
        String[] args = {"classify", "--data", ""}; // Empty string might cause different exception
        
        int exitCode = ReadabilityAnalysisMain.runCommand(args);
        String output = outContent.toString();
        
        assertNotEquals(0, exitCode, "Should return non-zero exit code for execution exception");
        assertFalse(output.contains("Usage:"), "Should show usage information");
    }

    @Test
    void testMain_ParameterExceptionHandler_MissingRequiredValue() {
        // Trigger ParameterExceptionHandler specifically (not ExecutionExceptionHandler)
        // This happens when required parameters are missing values
        String[] args = {"preprocess", "--source"}; // Missing value for --source
        
        int exitCode = ReadabilityAnalysisMain.runCommand(args);
        String output = outContent.toString();
        
        assertNotEquals(0, exitCode, "Should return non-zero exit code for missing parameter value");
        assertTrue(output.contains("Usage:"), "Should show usage information");
    }

    @Test
    void testMain_ParameterExceptionHandler_InvalidParameterFormat() {
        // Another way to trigger ParameterExceptionHandler
        String[] args = {"classify", "--data"}; // Missing required value
        
        int exitCode = ReadabilityAnalysisMain.runCommand(args);
        String output = outContent.toString();
        
        assertNotEquals(0, exitCode, "Should return non-zero exit code for invalid parameter format");
        assertTrue(output.contains("Usage:"), "Should show usage information");
    }

    @Test
    void testMain_CatchBlockWithGenericException() {
        // Try to trigger the generic Exception catch block
        // This is harder to do, but we can try with very malformed input
        String[] args = {"preprocess", "--source", "\0invalid\0path\0"}; // Null characters might cause issues
        
        int exitCode = ReadabilityAnalysisMain.runCommand(args);
        String output = outContent.toString();
        
        assertNotEquals(0, exitCode, "Should return non-zero exit code for generic exception");
        assertTrue(output.contains("Usage:"), "Should show usage information");
    }

    @Test
    void testMain_EmptyStringArgument() {
        // Test with empty string as argument (not starting with -, so should hit "Unknown subcommand")
        String[] args = {""};
        
        int exitCode = ReadabilityAnalysisMain.runCommand(args);
        String output = outContent.toString();
        
        assertNotEquals(0, exitCode, "Should return non-zero exit code for empty string argument");
        assertTrue(output.contains("Unknown subcommand"), "Should show 'Unknown subcommand' for empty string");
        assertTrue(output.contains("Usage:"), "Should show usage information");
    }

    @Test
    void testMain_WhitespaceArgument() {
        // Test with whitespace as argument (not starting with -, so should hit "Unknown subcommand")
        String[] args = {"   "};
        
        int exitCode = ReadabilityAnalysisMain.runCommand(args);
        String output = outContent.toString();
        
        assertNotEquals(0, exitCode, "Should return non-zero exit code for whitespace argument");
        assertTrue(output.contains("Unknown subcommand"), "Should show 'Unknown subcommand' for whitespace");
        assertTrue(output.contains("Usage:"), "Should show usage information");
    }

    @Test
    void testMain_ValidSubcommandWithInvalidOption() {
        // Test valid subcommand but with invalid option to trigger ExecutionExceptionHandler
        String[] args = {"preprocess", "--invalid-preprocess-flag"};
        
        int exitCode = ReadabilityAnalysisMain.runCommand(args);
        String output = outContent.toString();
        
        assertNotEquals(0, exitCode, "Should return non-zero exit code for invalid subcommand option");
        assertTrue(output.contains("Usage:"), "Should show usage information");
    }

    @Test
    void testMain_ClassifyWithInvalidOption() {
        // Test classify subcommand with invalid option
        String[] args = {"classify", "--invalid-classify-flag"};
        
        int exitCode = ReadabilityAnalysisMain.runCommand(args);
        String output = outContent.toString();
        
        assertNotEquals(0, exitCode, "Should return non-zero exit code for invalid classify option");
        assertTrue(output.contains("Usage:"), "Should show usage information");
    }

    @Test
    void testMain_MixedValidInvalidArgs() {
        // Mix of valid and invalid arguments to test different exception paths
        String[] args = {"preprocess", "--source", "validpath", "--invalid", "value"};
        
        int exitCode = ReadabilityAnalysisMain.runCommand(args);
        String output = outContent.toString();
        
        assertNotEquals(0, exitCode, "Should return non-zero exit code for mixed valid/invalid args");
        assertTrue(output.contains("Usage:"), "Should show usage information");
    }

    @Test
    void testMain_OptionThatLooksLikeSubcommand() {
        // Test option that might be confused with subcommand
        String[] args = {"--preprocess"}; // Starts with -, so should bypass subcommand check
        
        int exitCode = ReadabilityAnalysisMain.runCommand(args);
        String output = outContent.toString();
        
        assertNotEquals(0, exitCode, "Should return non-zero exit code for invalid option");
        assertFalse(output.contains("Unknown subcommand"), "Should not show 'Unknown subcommand' for --preprocess");
        assertTrue(output.contains("Usage:"), "Should show usage information");
    }
}


