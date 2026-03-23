package de.uni_passau.fim.se2.se.test_prioritisation.examples;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Lift class.
 */
public class LiftTest {

    @Test
    public void test0() {
        Lift lift = new Lift(10);
        assertNotNull(lift); // Verify the lift object is created
    }

    @Test
    public void test1() {
        Lift lift = new Lift(10);
        assertFalse(lift.isFull());
        assertEquals(10, lift.getTopFloor());
    }

    @Test
    public void test2() {
        Lift lift = new Lift(2);
        lift.call(1);
        assertEquals(1, lift.getCurrentFloor());
    }

    @Test
    public void test3() {
        Lift lift = new Lift(3, 4);
        lift.addRiders(3);
        assertEquals(3, lift.getNumRiders());
    }

    @Test
    public void test4() {
        Lift lift = new Lift(3);
        assertEquals(0, lift.getCurrentFloor());
        lift.goUp();
        lift.goUp();
        lift.goUp();
        assertEquals(3, lift.getCurrentFloor());
    }

    @Test
    public void test5() {
        Lift lift = new Lift(1, 1);
        assertEquals(1, lift.getTopFloor());
        assertEquals(1, lift.getCapacity());
    }

    @Test
    public void test6() {
        Lift lift = new Lift(1);
        lift.goUp();
        assertEquals(1, lift.getCurrentFloor());
        lift.goUp();
        assertEquals(1, lift.getCurrentFloor());
    }

    @Test
    public void test7() {
        Lift lift = new Lift(1, 2);
        lift.addRiders(2);
        assertEquals(2, lift.getNumRiders());
    }

    @Test
    public void test8() {
        Lift lift = new Lift(1);
        lift.goDown();
        assertEquals(0, lift.getCurrentFloor());
        lift.goUp();
        lift.goDown();
        assertEquals(0, lift.getCurrentFloor());
    }

    @Test
    public void test9() {
        Lift lift = new Lift(2);
        lift.call(1);
        assertEquals(1, lift.getCurrentFloor());
    }

    @Test
    public void test10() {
        Lift lift = new Lift(1);
        lift.call(-1);
        assertEquals(0, lift.getCurrentFloor());
    }

    @Test
    public void test11() {
        Lift lift = new Lift(3, 3);
        lift.call(3);
        assertEquals(3, lift.getCurrentFloor());
    }

    @Test
    public void test12() {
        Lift lift = new Lift(3, 5);
        assertFalse(lift.isFull());
        lift.addRiders(5);
        assertTrue(lift.isFull());
    }

    @Test
    public void test13() {
        Lift lift = new Lift(3, 4);
        assertEquals(4, lift.getCapacity());
        lift.addRiders(3);
        assertEquals(3, lift.getNumRiders());
    }

    @Test
    public void test14() {
        Lift lift = new Lift(2);
        lift.goUp();
        assertEquals(1, lift.getCurrentFloor());
    }

    @Test
    public void test15() {
        Lift lift = new Lift(4);
        lift.call(2);
        assertEquals(2, lift.getCurrentFloor());
    }

    @Test
    public void test16() {
        Lift lift = new Lift(1);
        assertEquals(10, lift.getCapacity());
    }

    @Test
    public void test17() {
        Lift lift = new Lift(11, 12);
        lift.call(11);
        lift.addRiders(12);
        assertEquals(11, lift.getCurrentFloor());
        assertEquals(12, lift.getNumRiders());
    }

    @Test
    public void test18() {
        Lift lift = new Lift(21, 22);
        assertEquals(22, lift.getCapacity());
        lift.addRiders(23);
        assertEquals(22, lift.getNumRiders());
    }

    @Test
    public void test19() {
        Lift lift = new Lift(21, 22);
        lift.call(21);
        assertEquals(21, lift.getCurrentFloor());
    }

    @Test
    public void test20() {
        Lift lift = new Lift(30);
        lift.call(20);
        lift.goDown();
        assertEquals(19, lift.getCurrentFloor());
    }

    @Test
    public void test21() {
        Lift lift = new Lift(11, 12);
        lift.call(11);
        assertEquals(11, lift.getCurrentFloor());
    }

    @Test
    public void test22() {
        Lift lift = new Lift(1);
        assertEquals(10, lift.getCapacity());
    }

    @Test
    public void test23() {
        Lift lift = new Lift(11, 12);
        lift.addRiders(12);
        assertTrue(lift.isFull());
    }

    @Test
    public void test24() {
        Lift lift = new Lift(1, 2);
        lift.addRiders(1);
        assertEquals(1, lift.getNumRiders());
    }

    @Test
    public void test25() {
        Lift lift = new Lift(10);
        lift.call(10);
        assertEquals(10, lift.getCurrentFloor());
    }

    @Test
    public void test26() {
        Lift lift = new Lift(11, 12);
        lift.call(11);
        assertEquals(11, lift.getCurrentFloor());
    }

    @Test
    public void test27() {
        Lift lift = new Lift(1, 5);
        lift.addRiders(6);
        assertEquals(5, lift.getNumRiders());
    }

    @Test
    public void test28() {
        Lift lift = new Lift(5);
        assertEquals(10, lift.getCapacity());
    }

    @Test
    public void test29() {
        Lift lift = new Lift(3);
        lift.call(3);
        assertEquals(3, lift.getCurrentFloor());
    }

    @Test
    public void test30() {
        Lift lift = new Lift(1, 3);
        assertEquals(3, lift.getCapacity());
    }

    @Test
    public void test31() {
        Lift lift = new Lift(1);
        assertEquals(0, lift.getCurrentFloor());
    }

    @Test
    public void test32() {
        Lift lift = new Lift(3);
        lift.goUp();
        assertEquals(1, lift.getCurrentFloor());
    }
}
