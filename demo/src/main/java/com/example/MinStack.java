package com.example;

import java.util.HashMap;
import java.util.Stack;

/**
 * A stack that supports push, pop, top, and retrieving the minimum element in constant time (amortized).
 * Uses a HashMap to track the current minimum after each value is pushed.
 */
class MinStack {

    /** Main stack storing the elements. */
    Stack<Integer> st;
    /** Maps each value to the minimum value seen after that value was pushed. */
    HashMap<Integer, Integer> minMap;

    public MinStack() {
        st = new Stack<>();
        minMap = new HashMap<>();
    }

    /** Push value onto the stack, updating the tracked minimum. */
    public void push(int value) {
        st.push(value);
        updateMinValue(value);
    }

    /** Pop the top element. */
    public void pop() {
        int value = st.peek();
        minMap.remove(value);
        st.pop();
    }

    /** Track the minimum value for the given pushed value. */
    public void updateMinValue(int value) {
        int minValue = Math.min(value, minMap.getOrDefault(value, Integer.MAX_VALUE));
        minMap.put(value, minValue);
    }

    /** Return the top element without removing it. */
    public int top() {
        return st.peek();
    }

    /** Return the current minimum value in the stack. */
    public int getMin() {
        return minMap.values().stream()
            .mapToInt(Integer::intValue)
            .max()
            .orElse(0);
    }
}