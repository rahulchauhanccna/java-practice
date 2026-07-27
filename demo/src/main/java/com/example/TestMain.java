package com.example;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Scratchpad / test class for various Stream API and MinStack experiments.
 * Contains commented-out examples for list intersection, difference, merge,
 * user-order join, and distinct-by-key filtering.
 */
public class TestMain {

    public static void main(String[] args) {
        // ---- MinStack demo ----
        MinStack obj = new MinStack();
        obj.push(-2);
        obj.push(0);
        obj.push(-3);
        obj.getMin();   // Returns -3
        obj.pop();
        obj.top();      // Returns 0
        obj.getMin();   // Returns -2
        int param_3 = obj.top();
        int param_4 = obj.getMin();
    }

    /**
     * Generic distinct-by-key filter for streams.
     * Uses ConcurrentHashMap.newKeySet() for thread-safe deduplication.
     * 
     * Example: users.stream().filter(distinctByKey(User::getId))
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}