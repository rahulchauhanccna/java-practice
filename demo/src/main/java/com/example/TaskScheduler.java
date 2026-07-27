package com.example;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Producer-consumer task scheduler using a bounded BlockingQueue.
 * The producer blocks when the queue is full; the consumer blocks when the queue is empty.
 * Demonstrates thread-safe coordination without explicit locks.
 */
public class TaskScheduler {
    /** Bounded queue (capacity limit) to protect memory. */
    private final BlockingQueue<String> taskQueue;

    public TaskScheduler(int capacity) {
        this.taskQueue = new ArrayBlockingQueue<>(capacity);
    }

    /**
     * Producer method: Adds a task to the queue.
     * BLOCKS if the queue is full until space becomes available (via put()).
     */
    public void submitTask(String task) throws InterruptedException {
       taskQueue.put(task); // Blocks if full
    }

    /**
     * Consumer method: Retrieves and removes the next task.
     * BLOCKS if the queue is empty until a task becomes available (via take()).
     */
    public String takeTask() throws InterruptedException {
        return taskQueue.take(); // Blocks if empty
    }

    public static void main(String[] args) throws InterruptedException {
        TaskScheduler scheduler = new TaskScheduler(2); // Small capacity for demonstration

        // Producer Thread: submits 5 tasks (will block when queue is full)
        Thread producer = new Thread(() -> {
            try {
                String[] tasks = {"Task 1", "Task 2", "Task 3", "Task 4", "Task 5"};
                for (String task : tasks) {
                    System.out.println("Submitting: " + task);
                    scheduler.submitTask(task);
                    System.out.println("Submitted: " + task);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Consumer Thread: processes tasks slowly (1 per second)
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    Thread.sleep(1000); // Simulate heavy processing delay
                    String task = scheduler.takeTask();
                    System.out.println("   Processed: " + task);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();
    }
}