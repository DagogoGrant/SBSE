package de.uni_passau.fim.se2.sa.readability.utils;

import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class CyclomaticComplexityVisitor extends VoidVisitorAdapter<Void> {

    private int complexity = 1; // Start with 1 for the base path

    @Override
    public void visit(IfStmt n, Void arg) {
        complexity++; // Count if statement
        super.visit(n, arg);
    }

    @Override
    public void visit(ForStmt n, Void arg) {
        complexity++; // Count for loop
        super.visit(n, arg);
    }

    @Override
    public void visit(ForEachStmt n, Void arg) {
        complexity++; // Count for-each loop
        super.visit(n, arg);
    }

    @Override
    public void visit(WhileStmt n, Void arg) {
        complexity++; // Count while loop
        super.visit(n, arg);
    }

    @Override
    public void visit(DoStmt n, Void arg) {
        complexity++; // Count do-while loop
        super.visit(n, arg);
    }

    @Override
    public void visit(SwitchEntry n, Void arg) {
        if (!n.getLabels().isEmpty()) { // Don't count default case
            complexity++; // Count each case statement
        }
        super.visit(n, arg);
    }

    @Override
    public void visit(CatchClause n, Void arg) {
        complexity++; // Count catch clause
        super.visit(n, arg);
    }

    @Override
    public void visit(ConditionalExpr n, Void arg) {
        complexity++; // Count ternary operator
        super.visit(n, arg);
    }

    @Override
    public void visit(BinaryExpr n, Void arg) {
        // Count logical AND and OR operators for short-circuit evaluation
        if (n.getOperator() == BinaryExpr.Operator.AND || 
            n.getOperator() == BinaryExpr.Operator.OR) {
            complexity++;
        }
        super.visit(n, arg);
    }

    public int getComplexity() {
        return complexity;
    }

    public void reset() {
        complexity = 1;
    }
}