package com.example.future;

import java.util.concurrent.CompletableFuture;

/**
 * Challenge 2: Chaining dependent async calls with thenCompose.
 * Fetches a user ID, then fetches the user's email using that ID.
 * 
 * 📘 thenCompose: flatMap for CompletableFuture – chains one async operation
 *   that returns a future, flattening the result (avoids CompletableFuture<CompletableFuture>).
 */
public class Challenge2 {

    public static CompletableFuture<String> fetchUserId() {
        return CompletableFuture.supplyAsync(() -> "usr_123");
    }

    public static CompletableFuture<String> fetchUserEmail(String userId) {
        return CompletableFuture.supplyAsync(() -> userId + "@example.com");
    }

    public static void main(String[] args) {
        CompletableFuture<String> emailFuture = fetchUserId()
            .thenComposeAsync(v -> fetchUserEmail(v));

        // Output should print: usr_123@example.com
        System.out.println(emailFuture.join());
    }
}