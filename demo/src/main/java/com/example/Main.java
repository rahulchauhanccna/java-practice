package com.example;

/**
 * Test driver for {@link EmailLimiter}.
 * <p>
 * Demonstrates a sliding-window rate limiter: allows a maximum of N emails
 * per recipient within a T-millisecond window.
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        // Allow 3 emails per recipient every 2 seconds
        EmailLimiter limiter = new EmailLimiter(3, 2000);

        String recipient1 = "alice@gmail.com";
        String recipient2 = "bob@gmail.com";

        System.out.println("=== Sending 5 emails to alice and 5 to bob (no delay) ===");
        for (int i = 1; i <= 5; i++) {
            System.out.println("Alice #" + i + " allowed: " + limiter.allowEmail(recipient1));
            System.out.println("Bob   #" + i + " allowed: " + limiter.allowEmail(recipient2));
        }
        // Expect: first 3 pass for each, last 2 blocked for each

        System.out.println("\n=== Waiting 2 seconds for window to slide... ===");
        Thread.sleep(2000);

        System.out.println("\n=== After window slide (3 more attempts each) ===");
        for (int i = 1; i <= 3; i++) {
            System.out.println("Alice #" + (5 + i) + " allowed: " + limiter.allowEmail(recipient1));
            System.out.println("Bob   #" + (5 + i) + " allowed: " + limiter.allowEmail(recipient2));
        }
        // Expect: first batch expired, so all 3 pass for each

        System.out.println("\n=== Null protection test ===");
        try {
            limiter.allowEmail(null);
        } catch (NullPointerException e) {
            System.out.println("Correctly threw NPE for null email: " + e.getMessage());
        }

        limiter.shutdown();
        System.out.println("\nDone. Rate limiter shut down.");
    }
}
