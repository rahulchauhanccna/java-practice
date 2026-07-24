package com.example.future;
import java.util.concurrent.CompletableFuture;

public class Challenge6 {

    public static CompletableFuture<String> fetchPayments(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            throw new RuntimeException("Payment Service Down");
        });
    }

    public static void main(String[] args) {
        String userId = "usr_123";

        CompletableFuture<String> paymentFuture = fetchPayments(userId).exceptionally(v->  "Default Payment (Fallback)")
            // TODO: Use exceptionally to catch the exception and return "Default Payment (Fallback)"
            ;

        // Output should print: Default Payment (Fallback)
        System.out.println(paymentFuture.join());
    }
}