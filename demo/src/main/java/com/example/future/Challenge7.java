package com.example.future;

import java.util.concurrent.CompletableFuture;

/**
 * Challenge 7: Handle both success and failure with handle().
 * handle() is like exceptionally + thenApply combined — it always runs,
 * regardless of whether the upstream future completed normally or exceptionally.
 * 
 * 📘 handle(BiFunction<T, Throwable, R>): receives the result (or null if failed)
 *   and the exception (or null if succeeded). Must handle both cases.
 */
public class Challenge7 {

    public static CompletableFuture<String> fetchServiceData(boolean shouldFail) {
        return CompletableFuture.supplyAsync(() -> {
            if (shouldFail) {
                throw new RuntimeException("Service Error");
            }
            return "valid_data";
        });
    }

    public static void main(String[] args) {
        // Success case: value is present, exception is null
        CompletableFuture<String> successCase = fetchServiceData(false)
            .handle((value, ex) -> {
                if (value != null) {
                    return value.toUpperCase();
                } else {
                    return "FALLBACK_DATA";
                }
            });

        // Failure case: value is null, exception is present
        CompletableFuture<String> failureCase = fetchServiceData(true)
            .handle((value, ex) -> {
                if (value != null) {
                    return value.toUpperCase();
                } else {
                    return "FALLBACK_DATA";
                }
            });

        // Output should print: VALID_DATA
        System.out.println(successCase.join());

        // Output should print: FALLBACK_DATA
        System.out.println(failureCase.join());
    }
}