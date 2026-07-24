package com.example.future;

import java.util.concurrent.CompletableFuture;

public class Challenge2 {

    public static CompletableFuture<String> fetchUserId() {
        return CompletableFuture.supplyAsync(() -> "usr_123");
    }

    public static CompletableFuture<String> fetchUserEmail(String userId) {
        return CompletableFuture.supplyAsync(() -> userId + "@example.com");
    }

    public static void main(String[] args) {
        CompletableFuture<String> emailFuture = fetchUserId()
        .thenComposeAsync(v-> fetchUserEmail(v));

        // Output should print: usr_123@example.com
        System.out.println(emailFuture.join());
    }
}