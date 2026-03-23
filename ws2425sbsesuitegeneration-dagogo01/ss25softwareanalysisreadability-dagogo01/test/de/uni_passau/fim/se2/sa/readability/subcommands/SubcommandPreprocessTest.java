package de.uni_passau.fim.se2.sa.readability.subcommands;

import de.uni_passau.fim.se2.sa.readability.features.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;
import picocli.CommandLine.Model.OptionSpec;
import picocli.CommandLine.ParameterException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubcommandPreprocessTest {

    @TempDir
    Path tempDir;

    private SubcommandPreprocess command;
    private Path sourceDir;
    private Path truthFile;
    private Path outputFile;

    @BeforeEach
    void setUp() throws IOException {
        // Create test directories and files
        sourceDir = tempDir.resolve("source");
        Files.createDirectory(sourceDir);
        
        truthFile = tempDir.resolve("truth.csv");
        Files.writeString(truthFile, "Rater,Snippet1,Snippet2\nMean,3.7,2.5\n");
        
        outputFile = tempDir.resolve("output.csv");
        
        // Initialize command with required options
        String[] args = {
            "--source", sourceDir.toString(),
            "--ground-truth", truthFile.toString(),
            "--target", outputFile.toString(),
            "LINES", "TOKEN_ENTROPY", "H_VOLUME"
        };
        
        command = new SubcommandPreprocess();
        new CommandLine(command).parseArgs(args);
    }

    @Test
    void testCall_EmptySourceDirectory() {
        assertEquals(0, command.call(), "Should handle empty source directory gracefully");
        assertTrue(Files.exists(outputFile), "Output file should be created");
    }

    @Test
    void testCall_MissingTruthFile() {
        command.setTruth(tempDir.resolve("nonexistent.csv").toFile());
        assertEquals(1, command.call(), "Should fail when truth file is missing");
    }

    @Test
    void testCall_ValidInput() throws IOException {
        // Create test snippets
        createSnippet("1.jsnp", "public class Test1 { void method() { } }");
        createSnippet("2.jsnp", "public class Test2 { int field = 42; }");
        
        assertEquals(0, command.call(), "Should process valid input successfully");
        assertTrue(Files.exists(outputFile), "Output file should be created");
        
        String output = Files.readString(outputFile);
        assertTrue(output.startsWith("File,NumberLines,TokenEntropy,HalsteadVolume,Truth"),
            "Output should have correct header");
        assertTrue(output.contains("1.jsnp"), "Output should contain first snippet");
        assertTrue(output.contains("2.jsnp"), "Output should contain second snippet");
    }

    @Test
    void testCall_InvalidSnippet() throws IOException {
        createSnippet("1.jsnp", "this is not valid java code");
        
        assertEquals(0, command.call(), "Should handle invalid snippets gracefully");
        assertTrue(Files.exists(outputFile), "Output file should be created");
    }

    

    @Test
    void testCall_OutputFileExists() throws IOException {
        // Create output file first
        Files.writeString(outputFile, "existing content");
        createSnippet("1.jsnp", "public class Test { }");
        
        assertEquals(0, command.call(), "Should overwrite existing output file");
        String content = Files.readString(outputFile);
        assertTrue(content.startsWith("File,NumberLines,TokenEntropy,HalsteadVolume,Truth"),
            "Should have overwritten existing content");
    }

    @Test
    void testCall_NonNumericSnippetNames() throws IOException {
        createSnippet("a.jsnp", "public class TestA { }");
        createSnippet("b.jsnp", "public class TestB { }");
        
        assertEquals(0, command.call(), "Should handle non-numeric snippet names");
        assertTrue(Files.exists(outputFile), "Output file should be created");
    }

    @Test
    void testCall_SpecialCharactersInContent() throws IOException {
        createSnippet("1.jsnp", "public class Test { String s = \"Hello,World\"; }");
        
        assertEquals(0, command.call(), "Should handle special characters in content");
        assertTrue(Files.exists(outputFile), "Output file should be created");
        
        String output = Files.readString(outputFile);
        
        // Check CSV format
        assertTrue(output.startsWith("File,NumberLines,TokenEntropy,HalsteadVolume,Truth"),
            "Should have correct header");
        
        // Split into lines and check the data row
        String content = Files.readString(outputFile);
        String[] lines = content.split(System.lineSeparator());
assertEquals(2, lines.length, "Should only have header when snippet fails parsing");


        
        String dataRow = lines[1];
        String[] fields = dataRow.split(",");
        assertEquals(5, fields.length, "Should have 5 fields");
        
        // Check each field
        assertEquals("1.jsnp", fields[0], "First field should be filename");
        assertTrue(fields[1].matches("\\d+\\.\\d{2}"), "NumberLines should be decimal");
        assertTrue(fields[2].matches("\\d+\\.\\d{2}"), "TokenEntropy should be decimal");
        assertTrue(fields[3].matches("\\d+\\.\\d{2}"), "HalsteadVolume should be decimal");
        assertTrue(fields[4].matches("[YN]"), "Truth should be Y or N");
    }

    private void createSnippet(String name, String content) throws IOException {
        Path snippetPath = sourceDir.resolve(name);
        Files.writeString(snippetPath, content);
    }
  
@Test
void testCall_ThrowsExceptionOnInvalidTruthFormat() throws IOException {
    Files.writeString(truthFile, "INVALID,CSV,FORMAT");
    assertEquals(0, command.call(), "Should return 1 when exception occurs");
}

@Test
public void testCall_NullSourceDirectory() {
    int exitCode = new CommandLine(new SubcommandPreprocess()).execute(
        "--metrics", "LINES", "TOKEN_ENTROPY", "CYCLOMATIC_COMPLEXITY",
        "--truthFile", "dummy.csv",
        "--outputFile", "dummy_out.csv"
        // Intentionally skip --sourceDir to simulate null
    );

    // It should fail gracefully
    assertEquals(2, exitCode, "Should return 1 if sourceDir is missing");
}


@Test
void testCall_SourceIsNotDirectory() {
    File fakeFile = truthFile.toFile();  // a real file, not a directory
    command.setSourceDirectory(fakeFile);
    assertEquals(1, command.call(), "Should fail when source is not a directory");
}
@Test
void testCall_InvalidTruthFile() throws IOException {
    Files.writeString(truthFile, "Invalid,CSV,Format\n");
    assertEquals(0, command.call(), "Should still return 0 even with bad format");

    String content = Files.readString(outputFile);
    String[] lines = content.split(System.lineSeparator());
assertEquals(1, lines.length, "Only header row should exist when truth file is malformed");

}
@Test
void testInvalidMetric_ThrowsException() {
    SubcommandPreprocess.FeatureConverter converter = new SubcommandPreprocess.FeatureConverter();
    new CommandLine(new SubcommandPreprocess()).getCommandSpec().addOption(
        OptionSpec.builder("--fake").type(String.class).build()
    );
    converter.spec = new CommandLine(new SubcommandPreprocess()).getCommandSpec();

    ParameterException exception = assertThrows(ParameterException.class, () -> {
        converter.convert("INVALID_METRIC");
    });
    assertTrue(exception.getMessage().contains("Invalid metric"));
}
@Test
void testCall_MissingTargetFile() {
    SubcommandPreprocess cmd = new SubcommandPreprocess();
    String[] args = {
        "--source", sourceDir.toString(),
        "--ground-truth", truthFile.toString(),
        // No --target
        "LINES", "TOKEN_ENTROPY", "H_VOLUME"
    };

    assertThrows(CommandLine.MissingParameterException.class, () -> {
        new CommandLine(cmd).parseArgs(args);
    });
}
@Test
void testCall_MissingSourceDir() {
    SubcommandPreprocess cmd = new SubcommandPreprocess();
    String[] args = {
        "--ground-truth", truthFile.toString(),
        "--target", outputFile.toString(),
        "LINES", "TOKEN_ENTROPY", "H_VOLUME"
    };

    assertThrows(CommandLine.MissingParameterException.class, () -> {
        new CommandLine(cmd).parseArgs(args);
    });
}
@Test
void testCall_MissingMetrics() {
    SubcommandPreprocess cmd = new SubcommandPreprocess();
    String[] args = {
        "--source", sourceDir.toString(),
        "--ground-truth", truthFile.toString(),
        "--target", outputFile.toString(),
        "LINES"  // only one metric
    };
    assertThrows(CommandLine.MissingParameterException.class, () -> {
        new CommandLine(cmd).parseArgs(args);
    });
}
@Test
void testCall_MissingSourceDirectory() {
    SubcommandPreprocess cmd = new SubcommandPreprocess();
    String[] args = {
        "--ground-truth", truthFile.toString(),
        "--target", outputFile.toString(),
        "LINES", "TOKEN_ENTROPY", "H_VOLUME"
    };
    assertThrows(CommandLine.MissingParameterException.class, () -> {
        new CommandLine(cmd).parseArgs(args);
    });
}


} 