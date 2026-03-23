package de.uni_passau.fim.se2.se.test_prioritisation.utils;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Utils {

    /**
     * Constructs a comma-separated string of test case names based on their order in a TestOrder object.
     *
     * @param testCaseNames The names of the test cases.
     * @param testOrder     The TestOrder object specifying the ordering.
     * @return A string representing the ordered test cases.
     */
    public static String getTestCaseOrder(String[] testCaseNames, TestOrder testOrder) {
        int[] positions = testOrder.getPositions();
        StringBuilder orderedTestCases = new StringBuilder();

        for (int i = 0; i < positions.length; i++) {
            if (i > 0) {
                orderedTestCases.append(", ");
            }
            if (positions[i] < 0 || positions[i] >= testCaseNames.length) {
                throw new IllegalArgumentException("Invalid test order index: " + positions[i]);
            }
            orderedTestCases.append(testCaseNames[positions[i]]);
        }

        return orderedTestCases.toString();
    }

    /**
     * Checks if a 2D boolean array is a rectangular matrix.
     *
     * @param matrix The 2D boolean array to check.
     * @return True if the matrix is rectangular, false otherwise.
     */
    public static boolean isRectangularMatrix(boolean[][] matrix) {
        if (matrix == null || matrix.length == 0) {
            return false;
        }
        int columnLength = matrix[0].length;
        for (boolean[] row : matrix) {
            if (row == null || row.length != columnLength) {
                return false;
            }
        }
        return true;
    }

    /**
     * Parses a file into a 2D boolean array representing a coverage matrix.
     * Each row in the file corresponds to a test case, and each column corresponds to a requirement.
     * 
     * The file should be formatted such that:
     * - Each row contains `0` or `1`, separated by spaces or commas.
     *
     * @param file The file to parse.
     * @return A 2D boolean array representing the coverage matrix.
     * @throws IOException If an I/O error occurs.
     */
    public static boolean[][] parseCoverageMatrix(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean[][] matrix = null;
            int rowIndex = 0;

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("[,\\s]+"); // Split by commas or whitespace
                if (matrix == null) {
                    matrix = new boolean[100][tokens.length]; // Assumes max 100 rows initially; dynamically resized if needed
                }
                if (rowIndex >= matrix.length) {
                    boolean[][] resizedMatrix = new boolean[matrix.length * 2][tokens.length];
                    System.arraycopy(matrix, 0, resizedMatrix, 0, matrix.length);
                    matrix = resizedMatrix;
                }

                for (int colIndex = 0; colIndex < tokens.length; colIndex++) {
                    matrix[rowIndex][colIndex] = tokens[colIndex].equals("1");
                }
                rowIndex++;
            }

            // Trim to the actual number of rows
            boolean[][] trimmedMatrix = new boolean[rowIndex][matrix[0].length];
            System.arraycopy(matrix, 0, trimmedMatrix, 0, rowIndex);

            return trimmedMatrix;
        }
    }
}
