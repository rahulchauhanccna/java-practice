package com.example.future;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Challenge3 {

    public record UserAccessProfile(String role, List<String> permissions) {}

    public static CompletableFuture<String> fetchUserRole(String userId) {
        return CompletableFuture.supplyAsync(() -> "ADMIN");
    }

    public static CompletableFuture<List<String>> fetchUserPermissions(String userId) {
        return CompletableFuture.supplyAsync(() -> List.of("READ", "WRITE", "DELETE"));
    }

    public static void main(String[] args) {
        String userId = "usr_123";

        CompletableFuture<String> roleFuture = fetchUserRole(userId);
        CompletableFuture<List<String>> permsFuture = fetchUserPermissions(userId);

        CompletableFuture<UserAccessProfile> profileFuture = roleFuture
        .thenCombine(permsFuture,(u,perm)-> new UserAccessProfile(u, perm));

        // Output should print: UserAccessProfile[role=ADMIN, permissions=[READ, WRITE, DELETE]]
        System.out.println(profileFuture.join());
    }
}