package com.example.future;
import java.util.concurrent.CompletableFuture;

/**
 * Challenge 6: Error recovery with exceptionally.
 * Simulates a failing payment service call and provides a fallback value.
 * 
 * 📘 exceptionally: catches any exception from the upstream future and
 *   returns a fallback value (like a "catch" block for CompletableFuture).
 */
public class Challenge6 {

    public static CompletableFuture<String> fetchPayments(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            throw new RuntimeException("Payment Service Down");
        });
    }

    public static void main(String[] args) {
        String userId = "usr_123";

        // If fetchPayments throws, exceptionally catches it and returns a fallback
        CompletableFuture<String> paymentFuture = fetchPayments(userId)
            .exceptionally(ex -> "Default Payment (Fallback)");

        // Output should print: Default Payment (Fallback)
        System.out.println(paymentFuture.join());
    }
}