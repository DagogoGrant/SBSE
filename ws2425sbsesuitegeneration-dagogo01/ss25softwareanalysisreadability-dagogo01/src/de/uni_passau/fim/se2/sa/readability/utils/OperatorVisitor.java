package de.uni_passau.fim.se2.sa.readability.utils;

import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.HashMap;
import java.util.Map;

public class OperatorVisitor extends VoidVisitorAdapter<Void> {

   public enum OperatorType {
       ASSIGNMENT, BINARY, UNARY, CONDITIONAL, TYPE_COMPARISON
   }

   private final Map<OperatorType, Integer> operatorsPerMethod;

   public OperatorVisitor() {
       operatorsPerMethod = new HashMap<>();
   }

   private void incrementOperator(OperatorType type) {
       operatorsPerMethod.put(type, operatorsPerMethod.getOrDefault(type, 0) + 1);
   }

   @Override
   public void visit(VariableDeclarator declarator, Void arg) {
       incrementOperator(OperatorType.ASSIGNMENT);
       super.visit(declarator, arg);
   }

   @Override
   public void visit(AssignExpr expr, Void arg) {
       incrementOperator(OperatorType.ASSIGNMENT);
       super.visit(expr, arg);
   }

   @Override
   public void visit(BinaryExpr expr, Void arg) {
       incrementOperator(OperatorType.BINARY);
       super.visit(expr, arg);
   }

   @Override
   public void visit(UnaryExpr expr, Void arg) {
       incrementOperator(OperatorType.UNARY);
       super.visit(expr, arg);
   }

   @Override
   public void visit(ConditionalExpr expr, Void arg) {
       incrementOperator(OperatorType.CONDITIONAL);
       super.visit(expr, arg);
   }

   @Override
   public void visit(InstanceOfExpr expr, Void arg) {
       incrementOperator(OperatorType.TYPE_COMPARISON);
       super.visit(expr, arg);
   }

   public Map<OperatorType, Integer> getOperatorsPerMethod() {
       return operatorsPerMethod;
   }

   public int getTotalOperators() {
       return operatorsPerMethod.values().stream().mapToInt(Integer::intValue).sum();
   }

   public int getUniqueOperatorCount() {
       // Count only the operator types that have been used (count > 0)
       return (int) operatorsPerMethod.values().stream().filter(count -> count > 0).count();
   }
}
