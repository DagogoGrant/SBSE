package de.uni_passau.fim.se2.se.test_prioritisation.examples;

public class AddNumbers {
    public int add(int a, int b) {
        return a + b;
    }

    public static void main(String[] args) {
        AddNumbers calculator = new AddNumbers();
        int result = calculator.add(3, 5);
        System.out.println("The sum is: " + result);

        // Remove or use this variable if needed
        // int dings = 42; // Example of unused variable

        // Dead code example (commented out or removed)
        // if (false) {
        //     System.out.println("This code will never run");
        // }
    }
}
