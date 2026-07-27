package com.example.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Challenge 8: Timeout handling with orTimeout and completeOnTimeout.
 * 
 * 📘 orTimeout(long, TimeUnit): fails the future with TimeoutException if
 *   it doesn't complete within the given duration.
 * 📘 completeOnTimeout(T, long, TimeUnit): resolves the future with a
 *   fallback value if it doesn't complete within the given duration.
 */
public class Challenge8 {

    public static CompletableFuture<String> slowServiceCall() {
        return CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(500); } catch (InterruptedException e) {}
            return "slow_response";
        });
    }

    public static void main(String[] args) {
        // Case 1: Fail with TimeoutException if taking > 200ms
        CompletableFuture<String> timeoutWithException = slowServiceCall()
            .orTimeout(200, TimeUnit.MILLISECONDS);

        // Case 2: Resolve with "FALLBACK_DATA" if taking > 200ms
        CompletableFuture<String> timeoutWithFallback = slowServiceCall()
            .completeOnTimeout("FALLBACK_DATA", 200, TimeUnit.MILLISECONDS);

        try {
            timeoutWithException.join();
        } catch (Exception e) {
            // Output should print: TimeoutException triggered!
            System.out.println("TimeoutException triggered!");
        }

        // Output should print: FALLBACK_DATA
        System.out.println(timeoutWithFallback.join());
    }
}