package de.uni_passau.fim.se2.se.test_prioritisation.utils;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.mutations.ShiftToBeginningMutation;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.nio.file.Files;


import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    void testIsRectangularMatrix() {
        boolean[][] rectangularMatrix = {
                {true, false, true},
                {false, true, false},
                {true, true, true}
        };
        boolean[][] nonRectangularMatrix = {
                {true, false},
                {false, true, false}
        };

        assertTrue(Utils.isRectangularMatrix(rectangularMatrix), "Matrix should be rectangular.");
        assertFalse(Utils.isRectangularMatrix(nonRectangularMatrix), "Matrix should not be rectangular.");
    }

    @Test
    void testGetTestCaseOrder() {
        String[] testCases = {"Test1", "Test2", "Test3"};
        int[] positions = {2, 0, 1};
        TestOrder testOrder = new TestOrder(new ShiftToBeginningMutation(new Random()), positions);

        String expectedOrder = "[Test3, Test1, Test2]";
        assertEquals(expectedOrder, Utils.getTestCaseOrder(testCases, testOrder), "Test case order does not match expected value.");
    }

    @Test
    void testDegreesOfFreedom() {
        assertEquals(0, Utils.degreesOfFreedom(1), "Degrees of freedom for 1 test case should be 0.");
        assertEquals(0, Utils.degreesOfFreedom(0), "Degrees of freedom for 0 test cases should be 0.");
        assertEquals(1, Utils.degreesOfFreedom(2), "Degrees of freedom for 2 test cases should be 1.");
        assertEquals(3, Utils.degreesOfFreedom(4), "Degrees of freedom for 4 test cases should be 3.");
    }

    @Test
    void testParseCoverageMatrix() throws IOException {
        File testFile = File.createTempFile("testMatrix", ".txt");
        testFile.deleteOnExit();
        String matrixContent = "[[true, false, true], [false, true, false], [true, true, true]]";
        Files.writeString(testFile.toPath(), matrixContent);

        boolean[][] expectedMatrix = {
                {true, false, true},
                {false, true, false},
                {true, true, true}
        };

        boolean[][] parsedMatrix = Utils.parseCoverageMatrix(testFile);

        assertArrayEquals(expectedMatrix, parsedMatrix, "Parsed matrix does not match expected matrix.");
    }
}
