package com.example.future;

import java.util.concurrent.CompletableFuture;

public class Challenge1 {

    public record FormattedUser(String id) {}

    public static CompletableFuture<String> fetchRawUserId() {
        return CompletableFuture.completedFuture("usr_987654321");
    }

    public static void main(String[] args) {
        CompletableFuture<FormattedUser> result = fetchRawUserId()
        .thenApply(v->  new FormattedUser(v.toUpperCase()));

        // Output should print: FormattedUser[id=USR_987654321]
        System.out.println(result.join()); 
    }
}