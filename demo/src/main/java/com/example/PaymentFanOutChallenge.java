package com.example;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <b>PaymentFanOutChallenge</b> demonstrates a fan-out concurrency pattern for
 * order payment verification using {@link CountDownLatch} and {@link Semaphore}.
 * <p>
 * For a given order, three verification tasks are launched in parallel:
 * <ol>
 *   <li><b>Internal Risk Check</b> – No rate limit, runs immediately.</li>
 *   <li><b>External Fraud Check</b> – Requires a semaphore permit (max 2 concurrent calls).</li>
 *   <li><b>Credit Verification Check</b> – Also requires a semaphore permit.</li>
 * </ol>
 * <p>
 * The orchestrator waits for all three tasks to complete (with a 2-second timeout).
 * If any task fails or the timeout expires, the order is rejected.
 * <p>
 * The main method simulates 3 concurrent orders (9 total tasks) competing for
 * only 2 external API permits, demonstrating semaphore-based rate limiting.
 *
 * @see CountDownLatch
 * @see Semaphore
 * @see PaymentOrchestrator
 */
public class PaymentFanOutChallenge {

    /**
     * Orchestrates the fan-out verification of an order across three parallel tasks.
     * <p>
     * Uses a {@link CountDownLatch} to wait for all tasks and a {@link Semaphore}
     * with 2 permits to rate-limit calls to external APIs.
     */
    public static class PaymentOrchestrator {
        private final ExecutorService executor = Executors.newFixedThreadPool(10);

        // TODO 1: Declare a Semaphore with 2 permits for external APIs
        private final Semaphore externalApiSemaphore = new Semaphore(2);

        /**
         * Verifies an order by running three checks in parallel.
         *
         * @param orderId the unique order identifier
         * @return {@code true} if all checks pass within the timeout
         * @throws InterruptedException if the current thread is interrupted while waiting
         */
        public boolean verifyOrder(String orderId) throws InterruptedException {
            // TODO 2: Initialize CountDownLatch for 3 tasks
            CountDownLatch latch = new CountDownLatch(3);
            // Shared thread-safe status collector
            AtomicBoolean allPassed = new AtomicBoolean(true);

            System.out.println("--> Starting fan-out verification for Order: " + orderId);

            // Task 1: Internal Risk Check (No rate limit needed)
            executor.submit(() -> {
                try {
                    boolean pass = runInternalRiskCheck(orderId);
                    if (!pass) allPassed.set(false);
                } finally {
                    latch.countDown();
                }
            });

            // Task 2: External Fraud Check (Requires Semaphore Permit!)
            executor.submit(() -> {

                boolean permitAcquired = false;
                try {
                    // TODO 4: Acquire Semaphore permit, call callExternalFraudApi(orderId), update allPassed
                    externalApiSemaphore.acquire();
                    permitAcquired = true;
                    boolean pass = callExternalFraudApi(orderId);
                    if(!pass){
                         allPassed.set(false);
                    }  
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    allPassed.set(false);
                } finally {
                    // TODO 5: Release Semaphore permit & Count down latch
                    latch.countDown();
                    if(permitAcquired)
                        externalApiSemaphore.release();
                }
            });

            // Task 3: Credit Verification Check (Requires Semaphore Permit!)
            executor.submit(() -> {
                boolean permitAcquired = false;
                try {
                    // TODO 6: Acquire Semaphore permit, call callCreditCheckApi(orderId), update allPassed
                    externalApiSemaphore.acquire();
                    permitAcquired = true;
                    callCreditCheckApi(orderId);
                    allPassed.set(true);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    allPassed.set(false);
                } finally {
                    // TODO 7: Release Semaphore permit & Count down latch
                     latch.countDown();
                     if(permitAcquired)
                        externalApiSemaphore.release();
                }
            });

            // TODO 8: Wait on latch with 2 seconds timeout
            boolean completedInTime = latch.await(2, TimeUnit.SECONDS);

            if (!completedInTime) {
                System.err.println("  [TIMEOUT] Verification timed out for Order: " + orderId);
                return false;
            }

            return allPassed.get();
        }

        // Mock Internal Service
        private boolean runInternalRiskCheck(String orderId) {
            System.out.println("  [Internal Risk] Checking order " + orderId);
            sleep(100);
            return true;
        }

        // Mock External Fraud Service
        private boolean callExternalFraudApi(String orderId) {
            System.out.println("  [External Fraud] Calling API for order " + orderId + " under permit...");
            sleep(300);
            return true;
        }

        // Mock External Credit Service
        private boolean callCreditCheckApi(String orderId) {
            System.out.println("  [Credit Check] Calling API for order " + orderId + " under permit...");
            sleep(300);
            return true;
        }

        private void sleep(long ms) {
            try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        /**
         * Shuts down the internal executor service.
         */
        public void shutdown() {
            executor.shutdown();
        }
    }

    /**
     * Entry point that simulates 3 concurrent order verifications.
     * <p>
     * Each order fans out into 3 tasks, so 9 total tasks compete for the
     * 2 external API permits.
     *
     * @param args command-line arguments (ignored)
     * @throws InterruptedException if the executor is interrupted while awaiting termination
     */
    public static void main(String[] args) throws InterruptedException {
        PaymentOrchestrator orchestrator = new PaymentOrchestrator();

        // Simulate 3 concurrent order verifications (9 total parallel tasks competing for 2 permits)
        ExecutorService clientSim = Executors.newFixedThreadPool(3);

        for (int i = 1; i <= 3; i++) {
            final String orderId = "ORD-00" + i;
            clientSim.submit(() -> {
                try {
                    boolean approved = orchestrator.verifyOrder(orderId);
                    System.out.println("<-- Result for " + orderId + ": " + (approved ? "APPROVED" : "REJECTED"));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        clientSim.shutdown();
        clientSim.awaitTermination(10, TimeUnit.SECONDS);
        orchestrator.shutdown();
    }
}