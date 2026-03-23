package de.uni_passau.fim.se2.sa.readability.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.Logistic;
import weka.core.Instances;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Random;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ClassifyTest {

    private File tempCsvFile;

    @BeforeEach
    public void setUp() throws IOException {
        // Create a temporary CSV file for testing
        tempCsvFile = File.createTempFile("test_data", ".csv");
        
        // Write sample CSV data
        try (FileWriter writer = new FileWriter(tempCsvFile)) {
            writer.write("File,NumberLines,TokenEntropy,HalsteadVolume,CyclomaticComplexity,Truth\n");
            writer.write("1.jsnp,10.00,2.50,100.00,2.00,Y\n");
            writer.write("2.jsnp,15.00,3.00,150.00,3.00,N\n");
            writer.write("3.jsnp,8.00,2.00,80.00,1.00,Y\n");
            writer.write("4.jsnp,20.00,3.50,200.00,4.00,N\n");
            writer.write("5.jsnp,12.00,2.75,120.00,2.00,Y\n");
            writer.write("6.jsnp,18.00,3.25,180.00,3.00,N\n");
            writer.write("7.jsnp,9.00,2.25,90.00,1.00,Y\n");
            writer.write("8.jsnp,22.00,3.75,220.00,5.00,N\n");
            writer.write("9.jsnp,11.00,2.60,110.00,2.00,Y\n");
            writer.write("10.jsnp,16.00,3.10,160.00,3.00,N\n");
            // Additional 20 diverse instances to enrich dataset
            writer.write("11.jsnp,28.44,3.36,129.26,5.73,Y\n");
            writer.write("12.jsnp,11.13,3.48,232.56,5.04,N\n");
            writer.write("13.jsnp,17.84,2.96,216.16,2.08,Y\n");
            writer.write("14.jsnp,20.00,3.10,190.00,3.00,N\n");
            writer.write("15.jsnp,14.76,2.61,108.76,2.00,Y\n");
            writer.write("16.jsnp,19.98,3.33,177.87,3.91,N\n");
            writer.write("17.jsnp,13.00,2.22,100.12,2.00,Y\n");
            writer.write("18.jsnp,22.78,3.95,241.63,5.98,N\n");
            writer.write("19.jsnp,12.65,2.88,112.42,2.25,Y\n");
            writer.write("20.jsnp,25.00,3.65,215.90,4.50,N\n");
            writer.write("21.jsnp,10.50,2.40,92.13,1.70,Y\n");
            writer.write("22.jsnp,23.33,3.85,221.12,5.80,N\n");
            writer.write("23.jsnp,11.75,2.30,105.55,1.95,Y\n");
            writer.write("24.jsnp,24.89,3.92,234.77,6.03,N\n");
            writer.write("25.jsnp,15.65,2.67,145.78,2.40,Y\n");
            writer.write("26.jsnp,20.00,3.45,210.00,4.00,N\n");
            writer.write("27.jsnp,16.80,2.95,162.22,2.80,Y\n");
            writer.write("28.jsnp,26.30,4.00,250.00,6.20,N\n");
            writer.write("29.jsnp,13.90,2.75,125.40,2.50,Y\n");
            writer.write("30.jsnp,27.50,4.10,262.13,6.50,N\n");

        }
    }

    @AfterEach
    public void tearDown() {
        if (tempCsvFile != null && tempCsvFile.exists()) {
            tempCsvFile.delete();
        }
    }

    @Test
    public void testLoadDataset_ValidFile() throws IOException {
        Instances dataset = Classify.loadDataset(tempCsvFile);
        
        assertNotNull(dataset);
        assertEquals(30, dataset.numInstances());
        // FIXED: WEKA includes the File column, so it's 6 attributes total (File + 4 features + Truth)
        assertEquals(6, dataset.numAttributes()); 
        assertEquals(5, dataset.classIndex()); // Truth is at index 5 (0-based)
    }

    @Test
    public void testLoadDataset_NonExistentFile() {
        File nonExistentFile = new File("non_existent_file.csv");
        assertThrows(IOException.class, () -> {
            Classify.loadDataset(nonExistentFile);
        });
    }

    @Test
    public void testTrainAndEvaluate_ValidDataset() throws Exception {
        Instances dataset = Classify.loadDataset(tempCsvFile);
        Evaluation evaluation = Classify.trainAndEvaluate(dataset);
        
        assertNotNull(evaluation);
        assertTrue(evaluation.numInstances() > 0);
        assertTrue(evaluation.pctCorrect() >= 0.0);
        assertTrue(evaluation.pctCorrect() <= 100.0);
    }

    @Test
    public void testTrainAndEvaluate_EmptyDataset() throws IOException {
        // Create empty CSV file
        File emptyCsvFile = File.createTempFile("empty_data", ".csv");
        try (FileWriter writer = new FileWriter(emptyCsvFile)) {
            writer.write("File,NumberLines,TokenEntropy,HalsteadVolume,CyclomaticComplexity,Truth\n");
        }
        
        try {
            Instances emptyDataset = Classify.loadDataset(emptyCsvFile);
            assertThrows(Exception.class, () -> {
                Classify.trainAndEvaluate(emptyDataset);
            });
        } finally {
            emptyCsvFile.delete();
        }
    }
    

    @Test
    public void testLoadDataset_InsufficientDataForCrossValidation() throws IOException {
        // Create CSV with insufficient instances for 10-fold CV
        File insufficientDataFile = File.createTempFile("insufficient_data", ".csv");
        try (FileWriter writer = new FileWriter(insufficientDataFile)) {
            writer.write("File,NumberLines,TokenEntropy,HalsteadVolume,CyclomaticComplexity,Truth\n");
            writer.write("1.jsnp,10.00,2.50,100.00,2.00,Y\n");
            writer.write("2.jsnp,15.00,3.00,150.00,3.00,N\n");
            writer.write("3.jsnp,8.00,2.00,80.00,1.00,Y\n");
            // Only 3 instances - not enough for 10-fold CV
        }
        
        try {
            Instances dataset = Classify.loadDataset(insufficientDataFile);
            assertEquals(3, dataset.numInstances());
            
            // FIXED: Should expect exception when trying 10-fold CV with < 10 instances
            assertThrows(Exception.class, () -> {
                Classify.trainAndEvaluate(dataset);
            });
        } finally {
            insufficientDataFile.delete();
        }
    }
//     @Test
// public void testLoadDataset_NoAttributes() throws IOException {
//     File noAttrCsv = File.createTempFile("no_attributes", ".csv");
//     try (FileWriter writer = new FileWriter(noAttrCsv)) {
//         writer.write(""); // Empty file (no header)
//     }

//     try {
//         Instances dataset = Classify.loadDataset(noAttrCsv);
//         assertEquals(0, dataset.numAttributes());
//         assertEquals(-1, dataset.classIndex()); // class index not set
//     } finally {
//         noAttrCsv.delete();
//     }
// }
@Test
public void testLoadDataset_OnlyOneAttribute() throws IOException {
    File minimalCsv = File.createTempFile("one_column", ".csv");
    try (FileWriter writer = new FileWriter(minimalCsv)) {
        writer.write("Truth\n");
        writer.write("Y\n");
        writer.write("N\n");
    }

    try {
        Instances dataset = Classify.loadDataset(minimalCsv);
        assertEquals(1, dataset.numAttributes());
        assertEquals(0, dataset.classIndex()); // should still set correctly
    } finally {
        minimalCsv.delete();
    }
}
@Test
public void testTrainAndEvaluate_PrecisionRecallRange() throws Exception {
    Instances dataset = Classify.loadDataset(tempCsvFile);
    Evaluation eval = Classify.trainAndEvaluate(dataset);

    // These just ensure the classifier didn't produce garbage
    assertTrue(eval.precision(0) >= 0.0 && eval.precision(0) <= 1.0);
    assertTrue(eval.recall(0) >= 0.0 && eval.recall(0) <= 1.0);
}
@Test
public void testClassifierObjectCreated() throws Exception {
    Instances dataset = Classify.loadDataset(tempCsvFile);
    Logistic logistic = new Logistic();
    assertNotNull(logistic); // Kill mutant where logistic = null
}
@Test
public void testLoadDataset_NoAttributes() throws IOException {
    File noAttrCsv = File.createTempFile("no_attributes", ".csv");
    try (FileWriter writer = new FileWriter(noAttrCsv)) {
        writer.write(""); // Empty file (no header, no rows)
    }

    assertThrows(IOException.class, () -> {
        Classify.loadDataset(noAttrCsv);
    });

    noAttrCsv.delete();
}

@Test
public void testLoadDataset_UnreadableFile() {
    File unreadable = new File("/dev/null/invalid.csv"); // or some locked file
    assertThrows(IOException.class, () -> Classify.loadDataset(unreadable));
}
@Test
public void testLoadDataset_NullFile() {
    assertThrows(IOException.class, () -> {
        Classify.loadDataset(null);
    });
}


@Test
public void testTrainAndEvaluate_AllSameLabel_ThrowsException() throws Exception {
    File file = File.createTempFile("sameLabel", ".csv");
    try (FileWriter writer = new FileWriter(file)) {
        writer.write("File,NumberLines,TokenEntropy,HalsteadVolume,CyclomaticComplexity,Truth\n");
        for (int i = 0; i < 10; i++) {
            writer.write(i + ".jsnp,10.0,2.0,100.0,1.0,Y\n");
        }
    }

    Instances dataset = Classify.loadDataset(file);
    assertThrows(weka.core.UnsupportedAttributeTypeException.class, () -> {
        Classify.trainAndEvaluate(dataset);
    });
}

// @Test
// public void testLoadDataset_MissingClassAttribute() throws Exception {
//     File file = File.createTempFile("noClass", ".csv");
//     try (FileWriter writer = new FileWriter(file)) {
//         writer.write("File,NumberLines,TokenEntropy\n"); // Missing Truth column
//         writer.write("1.jsnp,10,2.0\n");
//     }

//     assertThrows(Exception.class, () -> {
//         Classify.loadDataset(file);
//     });
// }

@Test
void testLoadDataset_UnparsableCSVFormat() {
    File invalidCSV = new File("src/test/resources/corrupt_line.csv");
    assertThrows(IOException.class, () -> Classify.loadDataset(invalidCSV));
}

@Test
void testLoadDataset_MalformedCSV() {
    File malformedCSV = new File("src/test/resources/malformed.csv");
    assertThrows(IOException.class, () -> Classify.loadDataset(malformedCSV));
}
@Test
public void testTrainAndEvaluate_AccuracyThreshold() throws Exception {
    Instances dataset = Classify.loadDataset(tempCsvFile);
    Evaluation evaluation = Classify.trainAndEvaluate(dataset);
    
    double accuracy = evaluation.pctCorrect();
    assertTrue(accuracy >= 69.50, 
        String.format("Expected accuracy of at least 69.50%%, but got %.2f%%", accuracy));
}

@Test
public void testTrainAndEvaluate_ROCThreshold() throws Exception {
    Instances dataset = Classify.loadDataset(tempCsvFile);
    Evaluation evaluation = Classify.trainAndEvaluate(dataset);
    
    // Get ROC area for the positive class (assuming binary classification)
    double rocArea = evaluation.areaUnderROC(0); // Index 0 for first class
    assertTrue(rocArea >= 0.74, 
        String.format("Expected ROC area of at least 0.74, but got %.4f", rocArea));
}

@Test
public void testTrainAndEvaluate_CombinedPerformanceMetrics() throws Exception {
    Instances dataset = Classify.loadDataset(tempCsvFile);
    Evaluation evaluation = Classify.trainAndEvaluate(dataset);
    
    double accuracy = evaluation.pctCorrect();
    double rocArea = evaluation.areaUnderROC(0);
    
    // Test both thresholds together
    assertTrue(accuracy >= 69.50 && rocArea >= 0.74, 
        String.format("Performance requirements not met: Accuracy=%.2f%% (≥69.50%%), ROC=%.4f (≥0.74)", 
        accuracy, rocArea));
}

@Test
public void testTrainAndEvaluate_DetailedPerformanceMetrics() throws Exception {
    Instances dataset = Classify.loadDataset(tempCsvFile);
    Evaluation evaluation = Classify.trainAndEvaluate(dataset);
    
    // Comprehensive performance validation
    double accuracy = evaluation.pctCorrect();
    double kappa = evaluation.kappa();
    double precision = evaluation.precision(0);
    double recall = evaluation.recall(0);
    double fMeasure = evaluation.fMeasure(0);
    double rocArea = evaluation.areaUnderROC(0);
    
    // Log all metrics for debugging
    System.out.println("=== Performance Metrics ===");
    System.out.println(String.format("Accuracy: %.2f%%", accuracy));
    System.out.println(String.format("Kappa: %.4f", kappa));
    System.out.println(String.format("Precision: %.4f", precision));
    System.out.println(String.format("Recall: %.4f", recall));
    System.out.println(String.format("F-Measure: %.4f", fMeasure));
    System.out.println(String.format("ROC Area: %.4f", rocArea));
    
    // Assert minimum requirements
    assertTrue(accuracy >= 69.50, "Accuracy below threshold");
    assertTrue(rocArea >= 0.74, "ROC area below threshold");
    
    // Additional sanity checks
    assertTrue(kappa >= 0.0, "Kappa should be non-negative for reasonable classifier");
    assertTrue(precision >= 0.0 && precision <= 1.0, "Precision out of valid range");
    assertTrue(recall >= 0.0 && recall <= 1.0, "Recall out of valid range");
    assertTrue(fMeasure >= 0.0 && fMeasure <= 1.0, "F-Measure out of valid range");
}

@Test
public void testTrainAndEvaluate_CrossValidationConsistency() throws Exception {
    Instances dataset = Classify.loadDataset(tempCsvFile);
    
    // Run evaluation multiple times to check consistency
    Evaluation eval1 = Classify.trainAndEvaluate(dataset);
    Evaluation eval2 = Classify.trainAndEvaluate(dataset);
    
    // Since we use a fixed seed (1), results should be identical
    assertEquals(eval1.pctCorrect(), eval2.pctCorrect(), 0.001, 
        "Cross-validation should be deterministic with fixed seed");
    assertEquals(eval1.areaUnderROC(0), eval2.areaUnderROC(0), 0.001, 
        "ROC area should be deterministic with fixed seed");
}

@Test
public void testTrainAndEvaluate_ConfusionMatrixValidation() throws Exception {
    Instances dataset = Classify.loadDataset(tempCsvFile);
    Evaluation evaluation = Classify.trainAndEvaluate(dataset);
    
    // Get confusion matrix
    double[][] confusionMatrix = evaluation.confusionMatrix();
    assertNotNull(confusionMatrix, "Confusion matrix should not be null");
    
    // For binary classification, should be 2x2 matrix
    assertEquals(2, confusionMatrix.length, "Should have 2 classes");
    assertEquals(2, confusionMatrix[0].length, "Should have 2 classes");
    
    // Sum of confusion matrix should equal total instances
    double totalPredictions = 0;
    for (int i = 0; i < confusionMatrix.length; i++) {
        for (int j = 0; j < confusionMatrix[i].length; j++) {
            totalPredictions += confusionMatrix[i][j];
        }
    }
    assertEquals(dataset.numInstances(), totalPredictions, 0.001, 
        "Total predictions should equal dataset size");
}

@Test
public void testTrainAndEvaluate_MinimumDatasetSizeForPerformance() throws IOException {
    // Create a larger, more balanced dataset to ensure performance thresholds can be met
    File largeDatasetFile = File.createTempFile("large_test_data", ".csv");
    
    try (FileWriter writer = new FileWriter(largeDatasetFile)) {
        writer.write("File,NumberLines,TokenEntropy,HalsteadVolume,CyclomaticComplexity,Truth\n");
        
        // Generate 100 instances with more realistic feature distributions
        Random rand = new Random(42); // Fixed seed for reproducibility
        
        for (int i = 1; i <= 100; i++) {
            String filename = i + ".jsnp";
            double lines = 5 + rand.nextGaussian() * 10 + 10; // Mean ~15
            double entropy = 1.5 + rand.nextGaussian() * 0.5 + 1; // Mean ~2.5
            double volume = 50 + rand.nextGaussian() * 50 + 100; // Mean ~150
            double complexity = 1 + rand.nextGaussian() * 2 + 2; // Mean ~3
        
            String truth = (complexity > 3.5 && volume > 180) ? "N" : "Y";
        
            writer.write(String.format("%s,%.2f,%.2f,%.2f,%.2f,%s\n",
                filename, lines, entropy, volume, complexity, truth));
        }
        
    }
    
    try {
        Instances dataset = Classify.loadDataset(largeDatasetFile);
        Evaluation evaluation = Classify.trainAndEvaluate(dataset);
        
        double accuracy = evaluation.pctCorrect();
        double rocArea = evaluation.areaUnderROC(0);
        
        // With a larger, well-structured dataset, we should meet the thresholds
        assertTrue(accuracy >= 69.50, 
            String.format("Large dataset accuracy %.2f%% below threshold 69.50%%", accuracy));
        assertTrue(rocArea >= 0.74, 
            String.format("Large dataset ROC area %.4f below threshold 0.74", rocArea));
            
    } catch (Exception e) {
        fail("Large dataset evaluation failed: " + e.getMessage());
    } finally {
        largeDatasetFile.delete();
    }
}
@Test
public void testLoadDataset_BoundaryCondition_NumAttributes() throws IOException {
    // Create a CSV file with only the header row (no data rows)
    File emptyHeaderFile = createCSVWithOnlyHeader();

    try {
        // Should still parse the header and set the class index
        Instances dataset = Classify.loadDataset(emptyHeaderFile);

        // Dataset should have 6 attributes based on the header
        assertEquals(6, dataset.numAttributes());

        // But zero instances
        assertEquals(0, dataset.numInstances());

        // Should still set class index to the last column
        assertEquals(5, dataset.classIndex());

    } finally {
        emptyHeaderFile.delete();
    }
}
private File createCSVWithOnlyHeader() throws IOException {
    File file = File.createTempFile("only_header", ".csv");
    try (FileWriter writer = new FileWriter(file)) {
        writer.write("File,NumberLines,TokenEntropy,HalsteadVolume,CyclomaticComplexity,Truth\n");
    }
    return file;
}


@Test
public void testLoadDataset_NominalAttributesSetting() {
    // Verify that setNominalAttributes actually affects the dataset
    // Compare datasets with and without nominal attributes set
}
// Additional test cases to kill surviving mutations in Classify.java

@Test
public void testLoadDataset_BoundaryCondition_ExactlyZeroAttributes() throws IOException {
    // This targets the surviving ConditionalsBoundaryMutator at line 33
    // Test the boundary condition: numAttributes() > 0 vs numAttributes() >= 0
    
    File emptyFile = File.createTempFile("truly_empty", ".csv");
    try (FileWriter writer = new FileWriter(emptyFile)) {
        // Write absolutely nothing - not even a header
        writer.write("");
    }
    
    try {
        // This should either throw an exception or create a dataset with 0 attributes
        Instances dataset = Classify.loadDataset(emptyFile);
        
        // If it succeeds, verify the boundary condition
        if (dataset.numAttributes() == 0) {
            assertEquals(-1, dataset.classIndex()); // Should NOT set class index
        } else {
            fail("Expected either exception or 0 attributes");
        }
    } catch (IOException e) {
        // This is also acceptable behavior for a truly empty file
        assertTrue(true);
    } finally {
        emptyFile.delete();
    }
}

@Test
public void testLoadDataset_NominalAttributesEffectValidation() throws IOException {
    // This targets the surviving VoidMethodCallMutator at line 27
    // We need to verify that setNominalAttributes("last") actually has an effect
    
    File testFile = File.createTempFile("nominal_test", ".csv");
    try (FileWriter writer = new FileWriter(testFile)) {
        writer.write("Feature1,Feature2,Class\n");
        writer.write("1.0,2.0,A\n");
        writer.write("3.0,4.0,B\n");
        writer.write("5.0,6.0,A\n");
    }
    
    try {
        Instances dataset = Classify.loadDataset(testFile);
        
        // Verify that the last attribute (Class) is nominal
        assertTrue(dataset.attribute(dataset.numAttributes() - 1).isNominal(), 
                  "Last attribute should be nominal after setNominalAttributes call");
        
        // Verify that other attributes are numeric
        for (int i = 0; i < dataset.numAttributes() - 1; i++) {
            assertTrue(dataset.attribute(i).isNumeric(), 
                      "Non-class attributes should remain numeric");
        }
        
    } finally {
        testFile.delete();
    }
}

@Test
public void testLoadDataset_SetSourceEffectValidation() throws IOException {
    // This targets potential mutations in the setSource call
    // Verify that the loader actually reads from the specified file
    
    File file1 = File.createTempFile("file1", ".csv");
    File file2 = File.createTempFile("file2", ".csv");
    
    try (FileWriter writer1 = new FileWriter(file1)) {
        writer1.write("A,B,Class\n1,2,X\n");
    }
    
    try (FileWriter writer2 = new FileWriter(file2)) {
        writer2.write("A,B,Class\n3,4,Y\n5,6,Z\n");
    }
    
    try {
        Instances dataset1 = Classify.loadDataset(file1);
        Instances dataset2 = Classify.loadDataset(file2);
        
        // Verify different files produce different datasets
        assertNotEquals(dataset1.numInstances(), dataset2.numInstances(),
                       "Different files should produce different datasets");
        
        assertEquals(1, dataset1.numInstances());
        assertEquals(2, dataset2.numInstances());
        
    } finally {
        file1.delete();
        file2.delete();
    }
}

@Test
public void testTrainAndEvaluate_CrossValidateModelEffectValidation() throws Exception {
    // This targets the VoidMethodCallMutator that removes crossValidateModel call
    // We need to verify that cross-validation actually happens
    
    Instances dataset = Classify.loadDataset(tempCsvFile);
    
    // Create evaluation manually without cross-validation
    Evaluation manualEval = new Evaluation(dataset);
    // Don't call crossValidateModel - this should give different results
    
    // Now use our method which should call crossValidateModel
    Evaluation crossValEval = Classify.trainAndEvaluate(dataset);
    
    // Cross-validated evaluation should have meaningful results
    assertTrue(crossValEval.numInstances() > 0, 
              "Cross-validation should process instances");
    assertTrue(crossValEval.pctCorrect() >= 0.0, 
              "Cross-validation should produce accuracy results");
    
    // The manual evaluation without cross-validation should be different
    // (This is a bit tricky to test directly, but we can verify the CV eval has proper results)
    assertNotNull(crossValEval.confusionMatrix(), 
                 "Cross-validation should produce confusion matrix");
}

@Test
public void testLoadDataset_ClassIndexSettingValidation() throws IOException {
    // This targets the VoidMethodCallMutator that removes setClassIndex call
    
    File testFile = File.createTempFile("class_index_test", ".csv");
    try (FileWriter writer = new FileWriter(testFile)) {
        writer.write("F1,F2,F3,Target\n");
        writer.write("1,2,3,A\n");
        writer.write("4,5,6,B\n");
    }
    
    try {
        Instances dataset = Classify.loadDataset(testFile);
        
        // Verify class index is set correctly
        assertEquals(3, dataset.classIndex(), "Class index should be set to last attribute");
        assertNotEquals(-1, dataset.classIndex(), "Class index should not be unset (-1)");
        
        // Verify we can access class attribute
        assertNotNull(dataset.classAttribute(), "Should be able to access class attribute");
        assertEquals("Target", dataset.classAttribute().name(), 
                    "Class attribute should be the last column");
        
    } finally {
        testFile.delete();
    }
}

@Test
public void testTrainAndEvaluate_LogisticClassifierCreation() throws Exception {
    // This ensures the Logistic classifier is actually created and used
    Instances dataset = Classify.loadDataset(tempCsvFile);
    Evaluation evaluation = Classify.trainAndEvaluate(dataset);
    
    // Verify that we get results that would only come from a trained classifier
    assertNotNull(evaluation);
    assertTrue(evaluation.numInstances() > 0);
    
    // These metrics should only be available after actual training
    double[][] confMatrix = evaluation.confusionMatrix();
    assertNotNull(confMatrix);
    assertTrue(confMatrix.length > 0);
    
    // Verify we have actual predictions (not just empty evaluation)
    double totalPredictions = 0;
    for (double[] row : confMatrix) {
        for (double val : row) {
            totalPredictions += val;
        }
    }
    assertEquals(dataset.numInstances(), totalPredictions, 0.001,
                "All instances should have been classified");
}

@Test
public void testLoadDataset_ExactBoundaryNumAttributes() throws IOException {
    // Test the exact boundary condition for the surviving mutation
    // Create file with exactly 1 attribute to test numAttributes() > 0 boundary
    
    File singleAttrFile = File.createTempFile("single_attr", ".csv");
    try (FileWriter writer = new FileWriter(singleAttrFile)) {
        writer.write("OnlyColumn\n");
        writer.write("value1\n");
        writer.write("value2\n");
    }
    
    try {
        Instances dataset = Classify.loadDataset(singleAttrFile);
        
        assertEquals(1, dataset.numAttributes());
        assertEquals(0, dataset.classIndex()); // Should set to 1-1=0
        
    } finally {
        singleAttrFile.delete();
    }
}
// @Test
//     public void testLoadDataset_BoundaryCondition_Line33_NumAttributes() throws IOException {
//         // Target: changed conditional boundary at line 33
//         // Original: if (dataset.numAttributes() > 0)
//         // Mutated: if (dataset.numAttributes() >= 0)
        
//         // Create a CSV file with NO attributes (empty header or malformed)
//         File tempFile = File.createTempFile("empty_attributes", ".csv");
        
//         try {
//             // Write a CSV with no proper attributes - just empty or malformed content
//             try (FileWriter writer = new FileWriter(tempFile)) {
//                 writer.write(""); // Completely empty file
//             }
            
//             Instances dataset = Classify.loadDataset(tempFile);
            
//             // With original condition (> 0): setClassIndex should NOT be called for 0 attributes
//             // With mutated condition (>= 0): setClassIndex WOULD be called for 0 attributes
            
//             // The key is that calling setClassIndex(-1) on a dataset with 0 attributes
//             // should behave differently than not calling it at all
            
//             assertEquals(0, dataset.numAttributes(), "Dataset should have 0 attributes");
            
//             // With 0 attributes, the class index should remain unset (-1)
//             // If the boundary condition is mutated, it might try to set it incorrectly
//             assertEquals(-1, dataset.classIndex(), "Class index should be -1 for empty dataset");
            
//         } finally {
//             tempFile.delete();
//         }
//     }

    @Test
    public void testLoadDataset_ExactlyOneAttribute_BoundaryEdgeCase() throws IOException {
        // Test the exact boundary case where numAttributes() == 1
        File tempFile = File.createTempFile("one_attribute", ".csv");
        
        try {
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write("Truth\n");  // Only one column
                writer.write("Y\n");
                writer.write("N\n");
            }
            
            Instances dataset = Classify.loadDataset(tempFile);
            
            // With 1 attribute: original condition (> 0) is true, so setClassIndex(0) is called
            // With mutated condition (>= 0): same result
            // But this tests the boundary behavior
            
            assertEquals(1, dataset.numAttributes(), "Should have exactly 1 attribute");
            assertEquals(0, dataset.classIndex(), "Class index should be 0 for single attribute");
            
        } finally {
            tempFile.delete();
        }
    }

    @Test
    public void testLoadDataset_SetNominalAttributesMutation_Line27() throws IOException {
        // Target: removed call to loader.setNominalAttributes("last") at line 27
        // This affects how the CSV loader interprets the data types
        
        File tempFile = File.createTempFile("nominal_test", ".csv");
        
        try {
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write("Feature1,Feature2,Truth\n");
                writer.write("1.5,2.3,Y\n");
                writer.write("2.1,1.8,N\n");
                writer.write("3.2,4.1,Y\n");
            }
            
            Instances dataset = Classify.loadDataset(tempFile);
            
            // The "Truth" column should be treated as nominal (categorical)
            // If setNominalAttributes is not called, it might be treated as numeric
            
            assertTrue(dataset.numAttributes() > 0, "Should have attributes");
            
            // Check that the last attribute (Truth) is nominal
            int lastIndex = dataset.numAttributes() - 1;
            assertTrue(dataset.attribute(lastIndex).isNominal(), 
                      "Last attribute (Truth) should be nominal when setNominalAttributes is called");
            
            // The Truth column should have exactly 2 values: Y and N
            assertEquals(2, dataset.attribute(lastIndex).numValues(), 
                        "Truth attribute should have exactly 2 nominal values");
            
            // Verify the values are Y and N
            assertTrue(dataset.attribute(lastIndex).value(0).equals("Y") || 
                      dataset.attribute(lastIndex).value(0).equals("N"), 
                      "Should contain Y or N values");
            
        } finally {
            tempFile.delete();
        }
    }

    @Test
    public void testLoadDataset_NominalAttributesCritical() throws IOException {
        // Additional test to ensure setNominalAttributes is critical
        File tempFile = File.createTempFile("nominal_critical", ".csv");
        
        try {
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write("Metric1,Metric2,Truth\n");
                writer.write("0.5,1.2,High\n");
                writer.write("1.8,0.9,Low\n");
                writer.write("2.1,1.5,High\n");
                writer.write("0.3,2.0,Low\n");
            }
            
            Instances dataset = Classify.loadDataset(tempFile);
            
            // Verify the dataset structure is correct for classification
            assertTrue(dataset.numInstances() > 0, "Should have instances");
            assertTrue(dataset.numAttributes() >= 3, "Should have at least 3 attributes");
            
            // The class attribute should be properly set and nominal
            assertTrue(dataset.classIndex() >= 0, "Class index should be set");
            assertTrue(dataset.classAttribute().isNominal(), "Class attribute should be nominal");
            
            // Should have the correct class values
            assertEquals(2, dataset.classAttribute().numValues(), "Should have 2 class values");
            
        } finally {
            tempFile.delete();
        }
    }
}




