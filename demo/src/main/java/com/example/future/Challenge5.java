package com.example.future;

import java.util.concurrent.CompletableFuture;

/**
 * Challenge 5: Waiting for ALL independent futures with allOf.
 * Fetches consumer, address, and payment data in parallel, then joins
 * them into a single pipe-delimited string.
 * 
 * 📘 allOf: returns a CompletableFuture<Void> that completes when ALL
 *   provided futures complete. Use .join() on each individual future
 *   inside the callback to aggregate results safely.
 */
public class Challenge5 {

    public static CompletableFuture<String> fetchConsumer(String userId) {
        return CompletableFuture.supplyAsync(() -> "ConsumerData");
    }

    public static CompletableFuture<String> fetchAddress(String userId) {
        return CompletableFuture.supplyAsync(() -> "AddressData");
    }

    public static CompletableFuture<String> fetchPayments(String userId) {
        return CompletableFuture.supplyAsync(() -> "PaymentData");
    }

    public static void main(String[] args) {
        String userId = "usr_123";

        CompletableFuture<String> consumerFuture = fetchConsumer(userId);
        CompletableFuture<String> addressFuture = fetchAddress(userId);
        CompletableFuture<String> paymentFuture = fetchPayments(userId);

        // allOf waits for all three, then thenApply aggregates them
        CompletableFuture<String> summaryFuture = 
            CompletableFuture.allOf(consumerFuture, addressFuture, paymentFuture)
                .thenApply(v -> String.join(" | ",
                    consumerFuture.join(),    // safe: already completed
                    addressFuture.join(),
                    paymentFuture.join()
                ));

        // Output should print: ConsumerData | AddressData | PaymentData
        System.out.println(summaryFuture.join());
    }
}