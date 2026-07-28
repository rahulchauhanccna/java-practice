package com.example;

import java.util.concurrent.*;
import java.util.concurrent.locks.StampedLock;

/**
 * <b>StampedLockOrderEngine</b> demonstrates the use of {@link StampedLock}
 * for optimistic reads in a concurrent order-state management system.
 * <p>
 * This implementation showcases:
 * <ul>
 *   <li><b>Optimistic Read</b> – Uses {@link StampedLock#tryOptimisticRead()} to
 *       read state without blocking, then validates with {@link StampedLock#validate(long)}.</li>
 *   <li><b>Read Fallback</b> – If the optimistic read is invalidated by a concurrent write,
 *       falls back to a traditional read lock via {@link StampedLock#readLock()}.</li>
 *   <li><b>Write Lock</b> – Uses {@link StampedLock#writeLock()} for exclusive updates.</li>
 * </ul>
 * <p>
 * The main method runs two phases:
 * <ol>
 *   <li>High-volume optimistic reads (8 readers, no write contention).</li>
 *   <li>Concurrent read & write interference (writer updates while readers read).</li>
 * </ol>
 *
 * @see StampedLock
 * @see OrderState
 * @see OrderSummary
 */
public class StampedLockOrderEngine {

    /**
     * An immutable snapshot of an order's state at a point in time.
     *
     * @param status    the order status (e.g. "PENDING", "PROCESSING", "SHIPPED")
     * @param amount    the order amount
     * @param itemCount the number of items in the order
     */
    public record OrderSummary(String status, double amount, int itemCount) {}

    /**
     * Thread-safe order state backed by a {@link StampedLock}.
     * <p>
     * Reads use an optimistic locking strategy for maximum throughput:
     * <ol>
     *   <li>Take an optimistic read stamp.</li>
     *   <li>Copy the fields into a local {@link OrderSummary}.</li>
     *   <li>Validate the stamp – if no write occurred, return the summary immediately.</li>
     *   <li>If validation fails, acquire a read lock, re-read, and return.</li>
     * </ol>
     * Writes acquire an exclusive write lock, update all fields, then release.
     */
    public static class OrderState {
        private String status = "PENDING";
        private double amount = 0.0;
        private int itemCount = 0;

        // TODO: Replace or keep StampedLock
        private final StampedLock sl = new StampedLock();

        /**
         * Returns a consistent snapshot of the current order state.
         * <p>
         * Uses an optimistic read first; falls back to a pessimistic read lock
         * if a concurrent write invalidated the optimistic stamp.
         *
         * @return an {@link OrderSummary} with the current state
         */
        public OrderSummary getOrderSummary() {
            // TODO: Step 1 - Get optimistic stamp with sl.tryOptimisticRead()
            // TODO: Step 2 - Copy fields into local variables
            // TODO: Step 3 - Check sl.validate(stamp)
            // TODO: Step 4 - If valid, return new OrderSummary(...)
            // TODO: Step 5 - If invalid, fallback to sl.readLock(), re-read under try-finally, and unlockRead(stamp)

            long stamp = sl.tryOptimisticRead();
            OrderSummary localOrderSummary = new OrderSummary(status, amount, itemCount);
            if(sl.validate(stamp)){
                return localOrderSummary;
            }
            else {
                stamp = sl.readLock();
                try {
                    localOrderSummary = new OrderSummary(status, amount, itemCount);
                } finally{
                    sl.unlockRead(stamp);
                }
            }
            return localOrderSummary; // Placeholder
        }

        /**
         * Updates the order state with new values.
         * <p>
         * Acquires an exclusive write lock to ensure atomicity of the update.
         *
         * @param newStatus  the new order status
         * @param newAmount  the new order amount
         * @param newItemCount the new item count
         */
        public void updateOrder(String newStatus, double newAmount, int newItemCount) {
            // TODO: Acquire write lock with sl.writeLock()
            // TODO: Update fields inside try block
            // TODO: Release write lock in finally block using sl.unlockWrite(stamp)
            long stamp = sl.writeLock();
            try {
                this.status = newStatus;
                this.amount = newAmount;
                this.itemCount = newItemCount;
            } finally {
                sl.unlockWrite(stamp);
            }
            
        }
    }

    /**
     * Entry point that demonstrates optimistic reads and concurrent read/write interference.
     *
     * @param args command-line arguments (ignored)
     * @throws InterruptedException if the executor is interrupted while awaiting termination
     */
    public static void main(String[] args) throws InterruptedException {
        OrderState order = new OrderState();
        ExecutorService pool = Executors.newFixedThreadPool(10);

        // 1. Initialize order
        order.updateOrder("PROCESSING", 150.50, 3);

        System.out.println("--- Phase 1: High-Volume Optimistic Reads ---");
        // Launch 8 reader threads
        for (int i = 0; i < 8; i++) {
            final int id = i;
            pool.submit(() -> {
                OrderSummary summary = order.getOrderSummary();
                System.out.println("Reader-" + id + " read: " + summary);
            });
        }

        Thread.sleep(100);

        System.out.println("\n--- Phase 2: Concurrent Read & Write Interference ---");
        // Simulate concurrent writing and reading
        pool.submit(() -> {
            order.updateOrder("SHIPPED", 150.50, 3);
            System.out.println("  [Writer] Updated order status to SHIPPED");
        });

        for (int i = 8; i < 12; i++) {
            final int id = i;
            pool.submit(() -> {
                OrderSummary summary = order.getOrderSummary();
                System.out.println("Reader-" + id + " read: " + summary);
            });
        }

        pool.shutdown();
        pool.awaitTermination(3, TimeUnit.SECONDS);
    }
}