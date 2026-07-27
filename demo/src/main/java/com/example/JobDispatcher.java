package com.example;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Demonstrates a priority-based job dispatcher using PriorityQueue.
 * Jobs are ordered first by Priority (HIGH > MEDIUM > LOW), then by timestamp (earliest first).
 */
public class JobDispatcher {

    /** Job priority levels. */
    public enum Priority {
        HIGH,
        MEDIUM,
        LOW
    }

    /** A job with a title, priority level, and creation timestamp. */
    public static class Job {
        private final String title;
        private final Priority priority;
        private final long timestamp;

        public Job(String title, Priority priority, long timestamp) {
            this.title = title;
            this.priority = priority;
            this.timestamp = timestamp;
        }

        public String getTitle() { return title; }
        public Priority getPriority() { return priority; }
        public long getTimestamp() { return timestamp; }

        @Override
        public String toString() {
            return String.format("Job[title='%s', priority=%s, time=%d]", title, priority, timestamp);
        }
    }

    public static void main(String[] args) {
        // PriorityQueue with a custom Comparator:
        // 1. Compare by Priority enum ordinal first (HIGH=0, MEDIUM=1, LOW=2)
        // 2. Then by timestamp (earliest first → natural ascending order)
        PriorityQueue<Job> jobQueue = new PriorityQueue<>(
            Comparator.comparing(Job::getPriority).thenComparingLong(Job::getTimestamp));

        // Populate test jobs (out of order to demonstrate PriorityQueue ordering)
        long now = System.currentTimeMillis();
        jobQueue.add(new Job("Marketing Email", Priority.LOW, now));
        jobQueue.add(new Job("Password Reset", Priority.HIGH, now + 10));
        jobQueue.add(new Job("Critical Security Alert", Priority.HIGH, now));       // earlier than Password Reset
        jobQueue.add(new Job("Order Processing", Priority.MEDIUM, now + 50));

        // Poll removes the highest-priority + earliest job each iteration
        while (!jobQueue.isEmpty()) {
            System.out.println(jobQueue.poll().toString());
        }
        // Expected output order: Critical Security Alert, Password Reset, Order Processing, Marketing Email
    }
}