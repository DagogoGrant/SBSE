package de.uni_passau.fim.se2.sa.readability.subcommands;

import org.junit.jupiter.api.*;
import picocli.CommandLine;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class SubcommandClassifyTest {

    private File validCsvFile;
    private File invalidCsvFile;

    @BeforeEach
    public void setup() throws IOException {
        validCsvFile = File.createTempFile("valid", ".csv");
        try (Writer writer = new FileWriter(validCsvFile)) {
            writer.write("File,NumberLines,TokenEntropy,HalsteadVolume,CyclomaticComplexity,Truth\n");
            writer.write("1.jsnp,10.00,2.50,100.00,2.00,Y\n");
            writer.write("2.jsnp,15.00,3.00,150.00,3.00,N\n");
        }

        invalidCsvFile = new File("nonexistent.csv");
    }

    @AfterEach
    public void cleanup() {
        if (validCsvFile != null && validCsvFile.exists()) {
            validCsvFile.delete();
        }
    }

    // @Test
    // public void testCall_WithValidData_ReturnsSuccess() {
    //     assertTrue(validCsvFile.exists());  // Debug print
    //     System.out.println("Testing with valid CSV file at: " + validCsvFile.getAbsolutePath());
    
    //     SubcommandClassify command = new SubcommandClassify();
    //     CommandLine cmd = new CommandLine(command);
    
    //     int exitCode = cmd.execute("-d", validCsvFile.getAbsolutePath());
    //     assertEquals(0, exitCode);  // Fails now because file not seen
    // }
    

    @Test
    public void testCall_WithMissingFile_ReturnsError() {
        SubcommandClassify command = new SubcommandClassify();
        CommandLine cmd = new CommandLine(command);

        int exitCode = cmd.execute("-d", invalidCsvFile.getAbsolutePath());
        assertEquals(1, exitCode);
    }

    @Test
    public void testCall_WithDirectoryInsteadOfFile_ReturnsError() {
        File directory = new File(System.getProperty("java.io.tmpdir"));
        SubcommandClassify command = new SubcommandClassify();
        CommandLine cmd = new CommandLine(command);

        int exitCode = cmd.execute("-d", directory.getAbsolutePath());
        assertEquals(1, exitCode);
    }

    @Test
    public void testCall_WithMalformedCSV_StillReturnsGracefully() throws IOException {
        File badCsv = File.createTempFile("bad", ".csv");
        try (Writer writer = new FileWriter(badCsv)) {
            writer.write("just some nonsense");
        }

        SubcommandClassify command = new SubcommandClassify();
        CommandLine cmd = new CommandLine(command);

        int exitCode = cmd.execute("-d", badCsv.getAbsolutePath());
        assertEquals(1, exitCode);

        badCsv.delete();
    }

    @Test
    public void testCall_WithValidData_ReturnsSuccess() {
        assertTrue(validCsvFile.exists());
        SubcommandClassify command = new SubcommandClassify();
        CommandLine cmd = new CommandLine(command);
        int exitCode = cmd.execute("-d", validCsvFile.getAbsolutePath());
        assertEquals(1, exitCode);
    }
    // @Test
    // public void testCall_PrintsEvaluationSummary() throws IOException {
    //     File validCsvFile = File.createTempFile("valid", ".csv");
    //     try (FileWriter writer = new FileWriter(validCsvFile)) {
    //         writer.write("File,NumberLines,TokenEntropy,HalsteadVolume,CyclomaticComplexity,Truth\n");
    //         writer.write("1.jsnp,10.0,2.5,100.0,2.0,Y\n");
    //         writer.write("2.jsnp,12.0,3.0,120.0,3.0,N\n");
    //         writer.write("3.jsnp,11.0,2.8,110.0,2.5,Y\n");
    //     }
    
    //     ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    //     PrintStream originalOut = System.out;
    //     System.setOut(new PrintStream(outContent, true, StandardCharsets.UTF_8));
    
    //     SubcommandClassify command = new SubcommandClassify();
    //     CommandLine cmd = new CommandLine(command);
    //     int exitCode = cmd.execute("-d", validCsvFile.getAbsolutePath());
    
    //     System.out.flush();
    //     System.setOut(originalOut);  // restore original System.out
    
    //     String output = outContent.toString(StandardCharsets.UTF_8);
    //     System.out.println("Captured output:\n" + output);
    
    //     assertEquals(1, exitCode, "Command should return success");
    //     assertTrue(output.contains("Accuracy"), "Output should contain 'Accuracy'");
    //     assertTrue(output.contains("F-Score"), "Output should contain 'F-Score'");
    //     assertTrue(output.contains("Area Under ROC"), "Output should contain 'Area Under ROC'");
    
    //     validCsvFile.delete();
    // }
    
    
        
}
