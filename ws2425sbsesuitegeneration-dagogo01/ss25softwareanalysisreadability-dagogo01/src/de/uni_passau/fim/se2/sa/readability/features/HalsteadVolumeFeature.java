package de.uni_passau.fim.se2.sa.readability.features;

import com.github.javaparser.ast.body.BodyDeclaration;
import de.uni_passau.fim.se2.sa.readability.utils.OperandVisitor;
import de.uni_passau.fim.se2.sa.readability.utils.OperatorVisitor;
import de.uni_passau.fim.se2.sa.readability.utils.Parser;

/**
 * Computes the Halstead Volume metric for the given code snippet.
 * Volume = N * log2(n) where N = total operators + operands, n = unique operators + operands
 * N1 = total operators, N2 = total operands
 * n1 = unique operators, n2 = unique operands
 */
public class HalsteadVolumeFeature extends FeatureMetric {

    @Override
    public double computeMetric(String codeSnippet) {
        // Check for null or empty/whitespace-only input
        if (codeSnippet == null || codeSnippet.trim().isEmpty()) {
            return 0.0;
        }

        try {
            // Parse the code snippet using the existing Parser utility
            BodyDeclaration<?> bodyDeclaration = Parser.parseJavaSnippet(codeSnippet);

            // Create visitors to count operators and operands
            OperatorVisitor operatorVisitor = new OperatorVisitor();
            bodyDeclaration.accept(operatorVisitor, null);

            OperandVisitor operandVisitor = new OperandVisitor();
            bodyDeclaration.accept(operandVisitor, null);

            // Get counts from visitors
            int N1 = operatorVisitor.getTotalOperators();      // Total operators
            int n1 = operatorVisitor.getUniqueOperatorCount(); // Unique operators

            int N2 = operandVisitor.getTotalOperands();        // Total operands
            int n2 = operandVisitor.getUniqueOperandCount();   // Unique operands

            // Calculate program length and vocabulary
            int N = N1 + N2;  // Total operators + operands
            int n = n1 + n2;  // Unique operators + operands

            // Return 0 if no operators or operands found
            if (n == 0 || N == 0) {
                return 0.0;
            }

            // Calculate Halstead Volume: V = N * log2(n)
            double volume = N * (Math.log(n) / Math.log(2));
            
            return volume;

        } catch (Exception e) {
            return 0.0;
        }
    }

    @Override
    public String getIdentifier() {
        return "H_VOLUME";
    }
}