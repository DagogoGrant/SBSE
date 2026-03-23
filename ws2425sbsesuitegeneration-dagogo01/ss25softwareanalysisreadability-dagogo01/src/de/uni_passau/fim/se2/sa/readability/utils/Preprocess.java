package de.uni_passau.fim.se2.sa.readability.utils;

import de.uni_passau.fim.se2.sa.readability.features.FeatureMetric;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class Preprocess {

    /**
     * The threshold for determining readability classification.
     * Values >= threshold are classified as "Y" (readable), values < threshold as "N" (not readable).
     */
    private static final double TRUTH_THRESHOLD = 3.6;

    /**
     * Formats a decimal number to 2 decimal places using US locale for consistency.
     */
    private static String formatDecimal(double value) {
        DecimalFormat df = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.US));
        df.setMinimumFractionDigits(2);
        df.setMaximumFractionDigits(2);
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df.format(value);
    }

    /**
     * Loads and processes the truth scores from the CSV file.
     * Converts Snippet column names to .jsnp filenames and calculates average scores.
     */
    public static Map<String, Double> loadTruthMap(Path csvPath) throws IOException {
        Map<String, Double> truthMap = new HashMap<>();
        List<String> lines = Files.readAllLines(csvPath);
        
        if (lines.isEmpty()) {
            return truthMap;
        }
        
        String[] headers = lines.get(0).split(",");
        int columnCount = headers.length;
        
        double[] sums = new double[columnCount - 1];
        int[] counts = new int[columnCount - 1];

        // Process each data row
        for (int i = 1; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(",");
            for (int j = 1; j < columnCount && j < parts.length; j++) {
                if (!parts[j].trim().isEmpty()) {
                    try {
                        double score = Double.parseDouble(parts[j].trim());
                        sums[j - 1] += score;
                        counts[j - 1]++;
                    } catch (NumberFormatException ignored) {
                        // Skip invalid numbers
                    }
                }
            }
        }

        // Calculate averages and create filename mappings
        for (int j = 1; j < columnCount; j++) {
            String snippetName = headers[j].replace("Snippet", "") + ".jsnp";
            if (counts[j - 1] > 0) {
                double avgScore = sums[j - 1] / counts[j - 1];
                truthMap.put(snippetName, avgScore);
            }
        }
        
        return truthMap;
    }

    /**
     * Main method for collecting CSV body data.
     * Processes all .jsnp files in the source directory and generates CSV rows.
     */
    public static void collectCSVBody(Path sourceDir, File truth, File target, List<FeatureMetric> featureMetrics) throws IOException {
        Map<String, Double> truthMap = loadTruthMap(truth.toPath());
        
        // Build header row
        StringBuilder csv = new StringBuilder();
        csv.append("File,");
        
        // Add feature columns in the correct order
        for (FeatureMetric metric : featureMetrics) {
            if (metric.getIdentifier().equals("LINES")) {
                csv.append("NumberLines,");
            } else if (metric.getIdentifier().equals("TOKEN_ENTROPY")) {
                csv.append("TokenEntropy,");
            } else if (metric.getIdentifier().equals("H_VOLUME")) {
                csv.append("HalsteadVolume,");
            } else if (metric.getIdentifier().equals("CyclomaticComplexity")) {
                csv.append("CyclomaticComplexity,");
            } else {
                csv.append(metric.getIdentifier()).append(",");
            }
        }
        
        csv.append("Truth\n");
        
        // Get all .jsnp files and sort them numerically
        List<Path> allFiles = Files.walk(sourceDir)
                .filter(path -> path.toString().endsWith(".jsnp"))
                .sorted((p1, p2) -> {
                    // Extract numbers from filenames for proper sorting (1.jsnp, 2.jsnp, ..., 10.jsnp, etc.)
                    String name1 = p1.getFileName().toString().replace(".jsnp", "");
                    String name2 = p2.getFileName().toString().replace(".jsnp", "");
                    try {
                        int num1 = Integer.parseInt(name1);
                        int num2 = Integer.parseInt(name2);
                        return Integer.compare(num1, num2);
                    } catch (NumberFormatException e) {
                        // Fallback to lexicographic sorting
                        return name1.compareTo(name2);
                    }
                })
                .collect(Collectors.toList());
        
        // Process each file
        for (Path path : allFiles) {
            String fileName = path.getFileName().toString();
            
            try {
                String code = Files.readString(path, StandardCharsets.UTF_8);
                StringBuilder row = new StringBuilder();
                row.append(fileName).append(",");
                
                // Compute each feature metric
                boolean allMetricsComputed = true;
                for (FeatureMetric metric : featureMetrics) {
                    try {
                        double value = metric.computeMetric(code);
                        row.append(formatDecimal(value)).append(",");
                    } catch (Exception e) {
                        System.err.println("Error computing metric " + metric.getIdentifier() + " for " + fileName + ": " + e.getMessage());
                        allMetricsComputed = false;
                        break;
                    }
                }
                
                if (allMetricsComputed) {
                    // Add truth value
                    double avgScore = truthMap.getOrDefault(fileName, 0.0);
                    String truthValue = avgScore >= TRUTH_THRESHOLD ? "Y" : "N";
                    row.append(truthValue);
                    
                    csv.append(row).append("\n");
                }
                
            } catch (IOException e) {
                System.err.println("Error reading file " + fileName + ": " + e.getMessage());
            }
        }
        
        // Write to target file
        Files.writeString(target.toPath(), csv.toString(), StandardCharsets.UTF_8);
        System.out.println("Preprocessing complete. CSV file generated at: " + target.getAbsolutePath());
    }

    /**
     * Alternative method signature for StringBuilder output (for compatibility).
     */
    public static void collectCSVBody(Path sourceDir, File truth, StringBuilder csv, List<FeatureMetric> featureMetrics) throws IOException {
        // Create a temporary file to use the main method
        File tempFile = File.createTempFile("temp_csv", ".csv");
        try {
            collectCSVBody(sourceDir, truth, tempFile, featureMetrics);
            
            // Read the content and append to StringBuilder (skip header)
            List<String> lines = Files.readAllLines(tempFile.toPath());
            for (int i = 1; i < lines.size(); i++) { // Skip header row
                csv.append(lines.get(i)).append("\n");
            }
        } finally {
            tempFile.delete();
        }
    }
    
}
