package de.uni_passau.fim.se2.se.test_prioritisation.utils;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;
import org.jacoco.core.instr.Instrumenter;
import org.jacoco.core.runtime.LoggerRuntime;
import org.jacoco.core.runtime.RuntimeData;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Measures code coverage of a class under test by a given test suite.
 */
public class CoverageTracker {

    private final Class<?> classUnderTest;
    private final String classUnderTestName;
    private final boolean[][] coverageMatrix;
    private String[] testCases;

    /**
     * Constructor for CoverageTracker.
     *
     * @param classUnderTest The class under test.
     */
    public CoverageTracker(Class<?> classUnderTest) {
        this.classUnderTest = classUnderTest;
        this.classUnderTestName = classUnderTest.getName();
        this.coverageMatrix = new boolean[0][]; // Initialize, populated later.
    }

    /**
     * Retrieves test cases from the given test suite class.
     *
     * @return Array of test case names.
     */
    public String[] getTestCases() {
        if (testCases == null) {
            testCases = extractTestCases(classUnderTest);
        }
        return testCases;
    }

    /**
     * Extracts test case names from a class.
     *
     * @param testClass The test class.
     * @return An array of test case names.
     */
    private String[] extractTestCases(Class<?> testClass) {
        return Arrays.stream(testClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(org.junit.jupiter.api.Test.class))
                .map(Method::getName)
                .toArray(String[]::new);
    }

    /**
     * Retrieves the coverage matrix.
     *
     * @return A boolean matrix representing coverage.
     * @throws Exception If coverage measurement fails.
     */
    public boolean[][] getCoverageMatrix() throws Exception {
        // Measure coverage if not already done.
        measureCoverage();
        return coverageMatrix;
    }

    /**
     * Measures the line coverage of the class under test.
     *
     * @throws Exception If measurement fails.
     */
    private void measureCoverage() throws Exception {
        LoggerRuntime runtime = new LoggerRuntime();
        Instrumenter instrumenter = new Instrumenter(runtime);
        MemoryClassLoader loader = new MemoryClassLoader();

        // Instrument and load the class under test.
        byte[] instrumentedBytecode = instrument(classUnderTest, instrumenter);
        loader.addDefinition(classUnderTestName, instrumentedBytecode);

        RuntimeData runtimeData = new RuntimeData();
        runtime.startup(runtimeData);

        // Execute each test case and collect coverage data.
        ExecutionDataStore executionData = new ExecutionDataStore();
        for (String testCase : getTestCases()) {
            executeTestCase(testCase, loader.loadClass(classUnderTestName));
            runtimeData.collect(executionData, new SessionInfoStore(), false);
        }
        runtime.shutdown();

        // Analyze collected coverage data.
        analyzeCoverage(executionData);
    }

    /**
     * Executes a specific test case.
     *
     * @param testCase The test case name.
     * @param testClass The test class.
     * @throws ReflectiveOperationException If reflection fails.
     */
    private void executeTestCase(String testCase, Class<?> testClass) throws ReflectiveOperationException {
        Object testInstance = testClass.getDeclaredConstructor().newInstance();
        Method testMethod = testClass.getDeclaredMethod(testCase);
        testMethod.setAccessible(true);
        testMethod.invoke(testInstance);
    }

    /**
     * Analyzes coverage data and updates the coverage matrix.
     *
     * @param executionData The collected execution data.
     * @throws IOException If analysis fails.
     */
    private void analyzeCoverage(ExecutionDataStore executionData) throws IOException {
        CoverageBuilder builder = new CoverageBuilder();
        Analyzer analyzer = new Analyzer(executionData, builder);
        try (InputStream originalCut = getClassStream(classUnderTestName)) {
            analyzer.analyzeClass(originalCut, classUnderTestName);
        }

        // Populate the coverage matrix.
        builder.getClasses().forEach(classCoverage -> {
            int firstLine = classCoverage.getFirstLine();
            int lastLine = classCoverage.getLastLine();
            boolean[] linesCovered = new boolean[lastLine - firstLine + 1];
            for (int i = firstLine; i <= lastLine; i++) {
                linesCovered[i - firstLine] = classCoverage.getLine(i).getStatus() != ICounter.NOT_COVERED;
            }
            // Assuming single class coverage, update matrix.
            System.arraycopy(linesCovered, 0, coverageMatrix, 0, linesCovered.length);
        });
    }

    /**
     * Reads the class bytecode as a stream.
     *
     * @param className Fully qualified class name.
     * @return InputStream for the class bytecode.
     */
    private InputStream getClassStream(String className) {
        String resourcePath = '/' + className.replace('.', '/') + ".class";
        return getClass().getResourceAsStream(resourcePath);
    }

    /**
     * Instruments a class for coverage tracking.
     *
     * @param clazz The class to instrument.
     * @param instrumenter The instrumenter.
     * @return Instrumented bytecode.
     * @throws IOException If instrumentation fails.
     */
    private byte[] instrument(Class<?> clazz, Instrumenter instrumenter) throws IOException {
        try (InputStream classStream = getClassStream(clazz.getName())) {
            return instrumenter.instrument(classStream, clazz.getName());
        }
    }

    /**
     * A class loader for in-memory bytecode.
     */
    public static class MemoryClassLoader extends ClassLoader {
        private final Map<String, byte[]> definitions = new HashMap<>();

        public void addDefinition(String name, byte[] bytes) {
            definitions.put(name, bytes);
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            byte[] bytecode = definitions.get(name);
            if (bytecode != null) {
                return defineClass(name, bytecode, 0, bytecode.length);
            }
            return super.loadClass(name, resolve);
        }
    }
}
