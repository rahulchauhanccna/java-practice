package com.example.future;

import java.util.concurrent.CompletableFuture;

/**
 * Challenge 1: Basic transformation with thenApply.
 * Fetches a raw user ID and transforms it to uppercase inside a FormattedUser record.
 * 
 * 📘 thenApply: transforms the result of a CompletableFuture synchronously
 *   (runs on the completing thread, not a separate pool).
 */
public class Challenge1 {

    public record FormattedUser(String id) {}

    /** Returns a pre-completed future with a raw user ID. */
    public static CompletableFuture<String> fetchRawUserId() {
        return CompletableFuture.completedFuture("usr_987654321");
    }

    public static void main(String[] args) {
        CompletableFuture<FormattedUser> result = fetchRawUserId()
            .thenApply(v -> new FormattedUser(v.toUpperCase()));

        // Output should print: FormattedUser[id=USR_987654321]
        System.out.println(result.join());
    }
}