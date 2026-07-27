package com.example;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.example.DashboardAggregator.DashboardDTO;

/**
 * Demonstrates composing multiple async data fetches into a single dashboard DTO.
 * Uses thenCompose, thenCombine, and exception handling with CompletableFuture.
 * 
 * Dependency graph:
 * [ fetchUserProfile ] ───► [ fetchRecommendations ] ──┐
 *                                                      ├──► [ Assemble Dashboard ]
 * [ fetchRecentOrders ] ───────────────────────────────┘
 */
public class DashboardAggregator {

    // Dummy domain models
    public record UserProfile(String userId, String name, String tier) {}
    public record Order(String orderId, double amount) {}
    public record Product(String productId, String title) {}
    public record DashboardDTO(UserProfile profile, List<Order> orders, List<Product> recommendations) {}

    /** Fixed thread pool for async I/O operations. */
    public static ExecutorService ioExecutor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        // Fire off the async pipeline
        CompletableFuture<DashboardDTO> dashFuture = buildUserDashboard("user_123");

        // Register callback to print results when complete, then block main thread
        dashFuture.thenAccept(dashboard -> {
            long totalTime = System.currentTimeMillis() - startTime;
            System.out.println("--- Dashboard Loaded in " + totalTime + "ms ---");
            System.out.println("User: " + dashboard.profile().name());
            System.out.println("Orders count: " + dashboard.orders().size());
            System.out.println("Recommendations count: " + dashboard.recommendations().size());
        }).exceptionally(ex -> {
            System.err.println("Dashboard build failed: " + ex.getMessage());
            return null;
        }).join(); // Block main thread only for verification
    }

    /**
     * Builds a dashboard by composing three async calls:
     * 1. fetchUserProfile (independent) ──► 2. fetchRecommendations (depends on profile)
     * 3. fetchRecentOrders (independent)
     * Profile + Recommendations are combined first, then combined with Orders.
     */
    public static CompletableFuture<DashboardDTO> buildUserDashboard(String userId) {
        // Independent: fetch recent orders
        CompletableFuture<List<Order>> ordersFuture = 
            CompletableFuture.supplyAsync(() -> fetchRecentOrders(userId), ioExecutor);

        // Independent: fetch user profile
        CompletableFuture<UserProfile> profileFuture = 
            CompletableFuture.supplyAsync(() -> fetchUserProfile(userId), ioExecutor);

        // Dependent on profile: fetch recommendations (thenComposeAsync chains futures)
        CompletableFuture<List<Product>> recsFuture = profileFuture
            .thenComposeAsync(profile -> 
                CompletableFuture.supplyAsync(() -> fetchRecommendations(profile), ioExecutor))
            .exceptionally(ex -> Collections.emptyList());

        // Combine: profile + recs together first, then combine with orders
        return profileFuture
            .thenCombine(recsFuture, (profile, recs) -> new AbstractMap.SimpleEntry<>(profile, recs))
            .thenCombine(ordersFuture, (entry, orders) -> 
                new DashboardDTO(entry.getKey(), orders, entry.getValue())
            );
    }

    // --- Mock Downstream Service Calls ---

    private static UserProfile fetchUserProfile(String userId) {
        sleep(150);
        return new UserProfile(userId, "Alice Smith", "VIP");
    }

    private static List<Order> fetchRecentOrders(String userId) {
        sleep(200);
        return List.of(new Order("ord_1", 99.99), new Order("ord_2", 49.50));
    }

    private static List<Product> fetchRecommendations(UserProfile profile) {
        sleep(100);
        if ("VIP".equals(profile.tier())) {
            return List.of(
                new Product("p_1", "Luxury Watch"), 
                new Product("p_2", "Noise Cancelling Headphones")
            );
        }
        return List.of(new Product("p_3", "Basic Earbuds"));
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}