package com.example;

import java.util.HashMap;
import java.util.Stack;

class MinStack {

    Stack<Integer> st;
    HashMap<Integer,Integer> minMap;

    public MinStack() {
        st = new Stack();
        minMap = new HashMap();
    }
    
    public void push(int value) {
        st.push(value);
        updateMinValue(value);
    }
    
    public void pop() {
        int value = st.peek();
        minMap.remove(value);
        st.pop();
    }

    public void updateMinValue(int value){
        int minValue = Math.min(value,minMap.getOrDefault(value,0));
        minMap.put(value,minValue);
    }
    
    public int top() {
        return st.peek();
    }
    
    public int getMin() {
        return minMap.values().stream().mapToInt(Integer::intValue).max().isPresent() ? minMap.values().stream().mapToInt(Integer::intValue).max().getAsInt() : 0;
    }
}

