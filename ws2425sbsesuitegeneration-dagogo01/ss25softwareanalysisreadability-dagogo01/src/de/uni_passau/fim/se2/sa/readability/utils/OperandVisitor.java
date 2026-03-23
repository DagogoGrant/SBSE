package de.uni_passau.fim.se2.sa.readability.utils;

import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.HashMap;
import java.util.Map;

public class OperandVisitor extends VoidVisitorAdapter<Void> {

    private final Map<String, Integer> operandsPerMethod;

    public OperandVisitor() {
        operandsPerMethod = new HashMap<>();
    }

    private void count(String operand) {
        
            operandsPerMethod.put(operand, operandsPerMethod.getOrDefault(operand, 0) + 1);
    }

    @Override
    public void visit(SimpleName name, Void arg) {
        count(name.getIdentifier());
        super.visit(name, arg);
    }

    @Override
    public void visit(StringLiteralExpr n, Void arg) {

        // Treat "NULL" (case-insensitive) the same as null literal
        if (n.getValue().equals("NULL")) {
            count("null");
        } else {
            count(n.getValue());
        }
        super.visit(n, arg);
    }

    @Override
    public void visit(IntegerLiteralExpr expr, Void arg) {
        count(expr.getValue());
        super.visit(expr, arg);
    }

    @Override
    public void visit(BooleanLiteralExpr expr, Void arg) {
        count(String.valueOf(expr.getValue()));
        super.visit(expr, arg);
    }

    @Override
    public void visit(CharLiteralExpr expr, Void arg) {
        count(expr.getValue());
        super.visit(expr, arg);
    }

    @Override
    public void visit(DoubleLiteralExpr expr, Void arg) {
        count(expr.getValue());
        super.visit(expr, arg);
    }

    @Override
    public void visit(LongLiteralExpr expr, Void arg) {
        count(expr.getValue());
        super.visit(expr, arg);
    }

    @Override
    public void visit(NullLiteralExpr expr, Void arg) {
        count("null");
        super.visit(expr, arg);
    }

    public Map<String, Integer> getOperandsPerMethod() {
        return operandsPerMethod;
    }

    public int getTotalOperands() {
        return operandsPerMethod.values().stream().mapToInt(Integer::intValue).sum();
    }

    public int getUniqueOperandCount() {
        return operandsPerMethod.size();
    }
}