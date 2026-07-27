package com.example;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe rate limiter using ConcurrentHashMap and CAS (compare-and-swap).
 * Limits each user to a maximum number of concurrent requests (MAX_REQUESTS = 5).
 * Demonstrates CountDownLatch for releasing threads simultaneously in tests.
 */
public class RateLimiter {

    /** Maximum allowed requests per user. */
    private final int MAX_REQUESTS = 5;

    /** Thread-safe map: userId -> AtomicInteger (request count). */
    private final Map<String, AtomicInteger> requestCounts;

    RateLimiter() {
        requestCounts = new ConcurrentHashMap<>();
    }

    /**
     * Checks if a request from the given user is allowed.
     * Uses CAS (compareAndSet) to atomically increment the counter,
     * ensuring correctness under concurrent access.
     *
     * @param userId the user requesting access
     * @return true if allowed, false if rate-limited
     */
    public boolean allowRequest(String userId) {
        AtomicInteger counter = requestCounts.computeIfAbsent(userId, k -> new AtomicInteger(0));
        while (true) {
            int count = counter.get();
            if (count >= MAX_REQUESTS) {
                return false; // Rate-limited
            }
            // CAS: only succeeds if the current value hasn't changed since we read it
            if (counter.compareAndSet(count, count + 1)) {
                return true;
            }
            // CAS failed → another thread changed the value concurrently → retry
        }
    }

    public static void main(String[] args) throws InterruptedException {
        RateLimiter limiter = new RateLimiter();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        String user = "user_123";

        // Spawn 10 concurrent threads trying to hit the API at the exact same time
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger allowedCount = new AtomicInteger(0);

        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                try {
                    latch.await(); // All threads wait here until released simultaneously
                    if (limiter.allowRequest(user)) {
                        allowedCount.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        latch.countDown(); // Release all threads at once
        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.SECONDS);

        System.out.println("Allowed requests out of 10: " + allowedCount.get());
        // Expected: 5 (since MAX_REQUESTS = 5)
    }
}