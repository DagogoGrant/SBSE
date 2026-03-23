package de.uni_passau.fim.se2.se.test_prioritisation.examples;

import java.util.HashMap;

/**
 * A simplified implementation of a hash map with integer keys.
 */
public class IntHashMap {

    /**
     * Inner class representing an entry in the map.
     */
    static class Entry {
        int key;
        String value;

        Entry(int key, String value) {
            this.key = key;
            this.value = value;
        }

        public int getKey() {
            return key;
        }

        public void setKey(int key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Entry{key=" + key + ", value='" + value + "'}";
        }
    }

    private final HashMap<Integer, Entry> map;

    public IntHashMap() {
        this.map = new HashMap<>();
    }

    public void put(int key, String value) {
        map.put(key, new Entry(key, value));
    }

    public String get(int key) {
        Entry entry = map.get(key);
        return (entry != null) ? entry.getValue() : null;
    }

    public void remove(int key) {
        map.remove(key);
    }

    @Override
    public String toString() {
        return "IntHashMap{" + "entries=" + map.values() + "}";
    }

    public static void main(String[] args) {
        IntHashMap intHashMap = new IntHashMap();
        intHashMap.put(1, "First");
        intHashMap.put(2, "Second");

        System.out.println("Value for key 1: " + intHashMap.get(1));
        System.out.println("Value for key 2: " + intHashMap.get(2));
        System.out.println(intHashMap);
    }
}
