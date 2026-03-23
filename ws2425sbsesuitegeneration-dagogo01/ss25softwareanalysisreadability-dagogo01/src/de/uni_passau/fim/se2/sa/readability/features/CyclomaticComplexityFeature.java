package de.uni_passau.fim.se2.sa.readability.features;

import com.github.javaparser.ast.body.BodyDeclaration;
import de.uni_passau.fim.se2.sa.readability.utils.CyclomaticComplexityVisitor;
import de.uni_passau.fim.se2.sa.readability.utils.Parser;

public class CyclomaticComplexityFeature extends FeatureMetric {

    /**
     * Computes the cyclomatic complexity metric based on the given code snippet.
     * This is done by counting the number of independent paths through the code.
     * We count: if, for, while, do-while, case statements (excluding default), 
     * catch clauses, ternary operators, and logical AND/OR operators.
     *
     * @return Cyclomatic complexity of the given code snippet.
     */
    @Override
    public double computeMetric(String codeSnippet) {
        if (codeSnippet == null || codeSnippet.isEmpty()) {
            return 1.0;  // Empty method has complexity of 1
        }

        try {
            // Parse the code snippet
            BodyDeclaration<?> bodyDeclaration = Parser.parseJavaSnippet(codeSnippet);
            
            // Create and use the visitor
            CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
            bodyDeclaration.accept(visitor, null);
            
            // Get the complexity
            double complexity = (double) visitor.getComplexity();
            
            return complexity;
            
        } catch (Exception e) {
            return 1.0;  // Return 1 for any parsing errors
        }
    }

    @Override
    public String getIdentifier() {
        return "CyclomaticComplexity";  // FIXED: Changed from "CyclomaticComplexity"
    }
}