package de.uni_passau.fim.se2.sa.readability.features;

import com.github.javaparser.ast.body.BodyDeclaration;
import de.uni_passau.fim.se2.sa.readability.utils.Parser;

import java.util.HashMap;
import java.util.Map;

public class TokenEntropyFeature extends FeatureMetric {

    @Override
    public double computeMetric(String codeSnippet) {
        if (codeSnippet == null || codeSnippet.isEmpty()) {
            return 0.0;
        }

        try {
            // Parse the code snippet to get token range
            BodyDeclaration<?> bodyDeclaration = Parser.parseJavaSnippet(codeSnippet);
            
            if (bodyDeclaration.getTokenRange().isEmpty()) {
                return 0.0;
            }

            return computeShannonEntropy(bodyDeclaration.getTokenRange().get());

        } catch (Exception e) {
            return 0.0;
        }
    }

    private double computeShannonEntropy(com.github.javaparser.TokenRange tokenRange) {
        Map<String, Integer> tokenCounts = new HashMap<>();
        int totalTokens = 0;

        // Count all tokens
        for (com.github.javaparser.JavaToken token : tokenRange) {
            String text = token.getText();
            tokenCounts.put(text, tokenCounts.getOrDefault(text, 0) + 1);
            totalTokens++;
        }

        if (totalTokens == 0) {
            return 0.0;
        }

        // Calculate Shannon entropy: H(s) = -∑p(xi) * log2(p(xi))
        double entropy = 0.0;
        for (Map.Entry<String, Integer> entry : tokenCounts.entrySet()) {
            double probability = (double) entry.getValue() / totalTokens;
            entropy -= probability * (Math.log(probability) / Math.log(2));
        }

        return entropy;
    }

    @Override
    public String getIdentifier() {
        return "TOKEN_ENTROPY";
    }
}