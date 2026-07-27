package com.example;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Demonstrates async notification dispatch using CompletableFuture.
 * Channels are processed in parallel with a fixed thread pool, simulating
 * network latency via Thread.sleep().
 */
public class NotificationDispatherPractice {

    // --- Record types (immutable data carriers) ---

    /** Incoming notification payload: target user + message text. */
    public record NotificationRequest(String userId, String message) {
    }

    /** Outcome of sending to one channel: which channel, whether it succeeded,
     *  and a generated tracking ID. */
    public record DispatchResult(String channel, boolean success, String trackingId) {
    }

    // --- Thread pool for async I/O work (10 threads) ---
    private static final ExecutorService io = Executors.newFixedThreadPool(10);

    // ====================================================================
    // Entry point
    // ====================================================================
    public static void main(String[] args) {
        // 1. Build a sample notification request
        NotificationRequest request = new NotificationRequest("user_999", "Your account is suspended");
        List<String> channels = List.of("SMS", "EMAIL", "APP");

        long start = System.currentTimeMillis();

        // 2. Fire off all dispatches asynchronously – returns almost immediately
        //    (the actual work runs on the `io` thread pool)
        CompletableFuture<List<DispatchResult>> batchFuture = dispatchAllNotifications(request, channels);

        // 3. Register a callback that runs once *all* dispatches have completed.
        //    .thenAccept() consumes the result (List<DispatchResult>) without transforming it.
        //    .join() blocks the main thread so we can see the output before the program exits.
        batchFuture.thenAccept(results -> {
            long duration = System.currentTimeMillis() - start;
            System.out.println("=== Batch Completed in " + duration + "ms ===");
            System.out.println("Successful deliveries (" + results.size() + "/" + channels.size() + "):");
            results.forEach(r -> System.out.println(" • [" + r.channel() + "] Tracking ID: " + r.trackingId()));
        }).join(); // Block main thread for verification

        // 4. Shut down the thread pool (gracefully)
        io.shutdown();
    }

    // ====================================================================
    // Core orchestrator: launches one async task per channel, then waits
    // for all of them and collects the non-null results.
    // ====================================================================
    public static CompletableFuture<List<DispatchResult>> dispatchAllNotifications(
            NotificationRequest request,
            List<String> channels) {

        // ---- Step A: Kick off a CompletableFuture for every channel ----
        // channels.stream().map(...) creates one future per channel.
        // .supplyAsync(Supplier, Executor) runs the Supplier lambda on the `io` pool.
        // If the lambda throws, the future completes exceptionally.
        List<CompletableFuture<DispatchResult>> futures = channels.stream()
            .map(channel -> CompletableFuture.supplyAsync(() -> sendNotification(request, channel)))
            .toList();          // collect into an unmodifiable list

        // ---- Step B: allOf() returns a future that completes when ALL
        //             individual futures have completed (success or failure) ----
        CompletableFuture<Void> all = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0])
        );

        // ---- Step C: Transform the "all done" signal into the collected results ----
        // .thenApply() runs after all futures complete.
        // For each channel future we call .join() – safe because all are done.
        // .filter(Objects::nonNull) skips channels that failed (their future
        // returned null from exceptionally(...) – now handled inside sendNotification).
        return all.thenApply(v ->
            futures.stream()
                .map(CompletableFuture::join)    // extract each result (or re-throw if failed)
                .filter(Objects::nonNull)        // discard failed dispatches
                .collect(Collectors.toList())    // gather into List<DispatchResult>
        );
    }

    // ====================================================================
    // Simulates sending a notification over a single channel.
    // Sleeps to mimic network latency, then (for demo purposes) fails SMS
    // while succeeding on other channels.
    // ====================================================================
    public static DispatchResult sendNotification(NotificationRequest request, String channel) {
        // ---- Simulate random network latency ----
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(50, 60));
        } catch (InterruptedException e) {
            // Restore the interrupted flag so callers can detect it, then abort.
            Thread.currentThread().interrupt();
            // Wrapping checked InterruptedException in RuntimeException because
            // CompletableFuture.supplyAsync expects a Supplier (no checked exceptions).
            throw new RuntimeException("Thread interrupted during dispatch", e);
        }

        // ---- Deliberate failure for SMS to demonstrate error handling ----
        if ("SMS".equals(channel)) {
            throw new RuntimeException("SMS Gateway 504 Gateway timeout");
        }

        // ---- Success path: generate a tracking ID and return ----
        String trackingId = "trk_" + UUID.randomUUID().toString().substring(0, 8);
        return new DispatchResult(channel, true, trackingId);
    }

}