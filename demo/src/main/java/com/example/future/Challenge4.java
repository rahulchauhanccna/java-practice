package com.example.future;

import java.util.concurrent.CompletableFuture;

public class Challenge4 {

    public static CompletableFuture<String> fetchFromPrimaryDb(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(300); } catch (InterruptedException e) {} // Slower
            return "data_from_primary";
        });
    }

    public static CompletableFuture<String> fetchFromSecondaryDb(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(50); } catch (InterruptedException e) {}  // Faster!
            return "data_from_secondary";
        });
    }

    public static void main(String[] args) {
        String userId = "usr_123";

        CompletableFuture<String> primaryFuture = fetchFromPrimaryDb(userId);
        CompletableFuture<String> secondaryFuture = fetchFromSecondaryDb(userId);

        CompletableFuture<String> winnerFuture = primaryFuture
        .applyToEither(secondaryFuture, (v)-> v.toUpperCase())
            // TODO: Use applyToEither to take whichever completes first 
            // (primary or secondary) and transform the resulting string to UPPERCASE
            ;

        // Output should print: DATA_FROM_SECONDARY
        System.out.println(winnerFuture.join());
    }
}