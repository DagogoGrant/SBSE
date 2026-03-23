package de.uni_passau.fim.se2.sa.readability.subcommands;

import de.uni_passau.fim.se2.sa.readability.features.*;
import de.uni_passau.fim.se2.sa.readability.utils.Preprocess;
import picocli.CommandLine.*;
import picocli.CommandLine.Model.CommandSpec;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;

@Command(
        name = "preprocess",
        description = "Preprocesses a directory of java snippet files using the optimal 3 metrics: LINES, TOKEN_ENTROPY, H_VOLUME (CyclomaticComplexity optional but not recommended)"
)
public class SubcommandPreprocess implements Callable<Integer> {

    @Spec
    CommandSpec spec;

    private Path sourceDir;
    private File truth;
    private File targetFile;

    @Option(
            names = {"-s", "--source"},
            description = "The directory containing java snippet (.jsnp) files",
            required = true
    )
    public void setSourceDirectory(final File sourceDir) {
        // Convert to canonical path to handle relative paths consistently
        try {
            this.sourceDir = sourceDir.getCanonicalFile().toPath();
        } catch (IOException e) {
            this.sourceDir = sourceDir.getAbsoluteFile().toPath();
        }
    }

    @Option(
            names = {"-g", "--ground-truth"},
            description = "The ground truth csv file containing the human readability ratings of the code snippets",
            required = true
    )
    public void setTruth(final File truth) {
        // Convert to canonical path to handle relative paths consistently
        try {
            this.truth = truth.getCanonicalFile();
        } catch (IOException e) {
            this.truth = truth.getAbsoluteFile();
        }
    }

    @Option(
            names = {"-t", "--target"},
            description = {"The target file where the preprocessed data will be saved"},
            required = true
    )
    public void setTargetFile(final File targetFile) {
        // Convert to canonical path to handle relative paths consistently
        try {
            this.targetFile = targetFile.getCanonicalFile();
        } catch (IOException e) {
            this.targetFile = targetFile.getAbsoluteFile();
        }
    }

    @Parameters(
            paramLabel = "featureMetrics",
            description = "The optimal 3 feature metrics: LINES TOKEN_ENTROPY H_VOLUME (CyclomaticComplexity optional)",
            arity = "3..4",
            converter = FeatureConverter.class
    )
    private List<FeatureMetric> featureMetrics;

    @Override
public Integer call() {
    try {
        if (sourceDir == null || !sourceDir.toFile().exists() || !sourceDir.toFile().isDirectory()) {
            System.err.println("Error: Source directory does not exist or is not a directory.");
            return 1;
        }

        if (truth == null || !truth.exists() || !truth.isFile()) {
            System.err.println("Error: Ground truth file does not exist or is not a valid file.");
            return 1;
        }

        System.out.println(">>> ENTERED PREPROCESS COMMAND");
        Preprocess.collectCSVBody(sourceDir, truth, targetFile, featureMetrics);
        return 0;
    } catch (Exception e) {
        System.err.println("Error during preprocessing: " + e.getMessage());
        e.printStackTrace();
        return 1;
    }
}


    /**
     * Converts supplied cli parameters to the respective {@link FeatureMetric}.
     */
    static class FeatureConverter implements ITypeConverter<FeatureMetric> {
        @Spec
        CommandSpec spec;

        public FeatureConverter() {}

        @Override
        public FeatureMetric convert(String metric) {
            // Normalize input to match expected format
            String upperMetric = metric.toUpperCase().trim();
            
            // Create exact instances for the required metrics
            if (upperMetric.equals("LINES")) {
                return new NumberLinesFeature();
            } else if (upperMetric.equals("TOKEN_ENTROPY")) {
                return new TokenEntropyFeature();
            } else if (upperMetric.equals("H_VOLUME")) {
                return new HalsteadVolumeFeature();
            } else if (upperMetric.equals("CYCLOMATICCOMPLEXITY")) {
                return new CyclomaticComplexityFeature();
            }
            
            // For any other metrics, throw an exception
            throw new ParameterException(spec != null ? spec.commandLine() : null, 
                "Invalid metric: " + metric + ". Must be one of: LINES, TOKEN_ENTROPY, H_VOLUME, CyclomaticComplexity");
        }
    }
}