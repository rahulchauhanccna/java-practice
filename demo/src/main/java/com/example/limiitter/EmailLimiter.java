package com.example.limiitter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A thread-safe sliding window rate limiter for emails.
 * <p>
 * Allows a maximum of {@code maxEmails} emails to a given recipient per
 * {@code windowSize} milliseconds, using a sliding window approach.
 * <p>
 * Stale entries are periodically purged to prevent memory leaks.
 */
public class EmailLimiter {

    private final int maxEmails;
    private final long windowSizeMs;
    /** Thread-safe map: recipient email -> deque of timestamps (sorted oldest-first). */
    private final Map<String, Deque<Long>> userTimestamps;

    /** Background thread that periodically purges stale entries. */
    private final ScheduledExecutorService cleanupExecutor;

    /**
     * @param maxEmails    maximum number of emails allowed in the window
     * @param windowSizeMs size of the sliding window in milliseconds
     * @throws IllegalArgumentException if maxEmails <= 0 or windowSizeMs <= 0
     */
    public EmailLimiter(int maxEmails, long windowSizeMs) {
        if (maxEmails <= 0) {
            throw new IllegalArgumentException("maxEmails must be positive, got: " + maxEmails);
        }
        if (windowSizeMs <= 0) {
            throw new IllegalArgumentException("windowSizeMs must be positive, got: " + windowSizeMs);
        }
        this.maxEmails = maxEmails;
        this.windowSizeMs = windowSizeMs;
        this.userTimestamps = new ConcurrentHashMap<>();

        // Schedule periodic cleanup of stale entries every 60 seconds
        this.cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "email-limiter-cleanup");
            t.setDaemon(true);
            return t;
        });
        this.cleanupExecutor.scheduleAtFixedRate(this::purgeStaleEntries, 60, 60, TimeUnit.SECONDS);
    }

    /**
     * Returns {@code true} if the email is allowed (within the rate limit),
     * {@code false} if it is rate-limited.
     * <p>
     * Algorithm: remove timestamps outside the window, then check if count < max.
     *
     * @param recipientEmail the recipient's email address
     * @return {@code true} if the email can be sent, {@code false} otherwise
     * @throws NullPointerException if recipientEmail is null
     */
    public boolean allowEmail(String recipientEmail) {
        Objects.requireNonNull(recipientEmail, "recipientEmail must not be null");

        long now = System.currentTimeMillis();
        long windowStart = now - windowSizeMs;

        // Get or create the deque for this recipient
        Deque<Long> timestamps = userTimestamps.computeIfAbsent(recipientEmail, k -> new ArrayDeque<>());

        synchronized (timestamps) {
            // Remove timestamps that have fallen outside the sliding window
            while (!timestamps.isEmpty() && timestamps.peekFirst() <= windowStart) {
                timestamps.pollFirst();
            }

            // Check if under the limit
            if (timestamps.size() < maxEmails) {
                timestamps.addLast(now);
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Removes entries for recipients whose last recorded timestamp is older than
     * the window size. This prevents unbounded memory growth for inactive emails.
     */
    private void purgeStaleEntries() {
        long cutoff = System.currentTimeMillis() - windowSizeMs;
        userTimestamps.entrySet().removeIf(entry -> {
            Deque<Long> timestamps = entry.getValue();
            synchronized (timestamps) {
                // Also clean up the deque while we're at it
                while (!timestamps.isEmpty() && timestamps.peekFirst() <= cutoff) {
                    timestamps.pollFirst();
                }
                return timestamps.isEmpty();
            }
        });
    }

    /**
     * Shuts down the background cleanup thread. Call this when the rate limiter
     * is no longer needed to release resources.
     */
    public void shutdown() {
        cleanupExecutor.shutdown();
        try {
            if (!cleanupExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                cleanupExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            cleanupExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // Allow 3 emails per recipient every 2 seconds
        EmailLimiter limiter = new EmailLimiter(3, 2000);

        String recipient1 = "alice@gmail.com";
        String recipient2 = "bob@gmail.com";

        System.out.println("=== Sending 5 emails to alice and 5 to bob (no delay) ===");
        for (int i = 1; i <= 5; i++) {
            System.out.println("Alice #" + i + " allowed: " + limiter.allowEmail(recipient1));
            System.out.println("Bob   #" + i + " allowed: " + limiter.allowEmail(recipient2));
        }
        // Expect: first 3 pass for each, last 2 blocked for each

        System.out.println("\n=== Waiting 2 seconds for window to slide... ===");
        Thread.sleep(2000);

        System.out.println("\n=== After window slide (3 more attempts each) ===");
        for (int i = 1; i <= 3; i++) {
            System.out.println("Alice #" + (5 + i) + " allowed: " + limiter.allowEmail(recipient1));
            System.out.println("Bob   #" + (5 + i) + " allowed: " + limiter.allowEmail(recipient2));
        }
        // Expect: first batch expired, so all 3 pass for each

        System.out.println("\n=== Null protection test ===");
        try {
            limiter.allowEmail(null);
        } catch (NullPointerException e) {
            System.out.println("Correctly threw NPE for null email: " + e.getMessage());
        }

        limiter.shutdown();
        System.out.println("\nDone. Rate limiter shut down.");
    }
}