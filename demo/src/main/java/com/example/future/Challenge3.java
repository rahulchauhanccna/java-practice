package com.example.future;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Challenge 3: Combining two independent futures with thenCombine.
 * Fetches a user's role AND permissions independently, then combines them
 * into a single UserAccessProfile.
 * 
 * 📘 thenCombine: merges the results of TWO independent CompletableFutures
 *   using a BiFunction when both complete.
 */
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
            .thenCombine(permsFuture, (role, perms) -> new UserAccessProfile(role, perms));

        // Output should print: UserAccessProfile[role=ADMIN, permissions=[READ, WRITE, DELETE]]
        System.out.println(profileFuture.join());
    }
}