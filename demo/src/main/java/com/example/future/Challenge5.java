package com.example.future;

import java.util.concurrent.CompletableFuture;

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

        CompletableFuture<String> summaryFuture = 
          CompletableFuture.allOf(consumerFuture,addressFuture,paymentFuture).thenApply(v-> {
          return String.join("|",consumerFuture.join(),addressFuture.join(),paymentFuture.join());
          }  
        );

          

        // Output should print: ConsumerData | AddressData | PaymentData
        System.out.println(summaryFuture.join());
    }
}
