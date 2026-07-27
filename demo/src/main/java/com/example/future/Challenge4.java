package com.example.future;

import java.util.concurrent.CompletableFuture;

/**
 * Challenge 4: Race two data sources with applyToEither.
 * Queries primary and secondary DBs simultaneously; takes whichever completes FIRST
 * and transforms the result to uppercase.
 * 
 * 📘 applyToEither: takes the result of the FIRST future to complete
 *   (primary or secondary) and applies a function to it.
 */
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

        // Take whichever completes first and transform to uppercase
        CompletableFuture<String> winnerFuture = primaryFuture
            .applyToEither(secondaryFuture, String::toUpperCase);

        // Output should print: DATA_FROM_SECONDARY (secondary is faster)
        System.out.println(winnerFuture.join());
    }
}