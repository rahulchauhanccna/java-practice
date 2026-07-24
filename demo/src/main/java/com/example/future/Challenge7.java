package com.example.future;

import java.util.concurrent.CompletableFuture;

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
        CompletableFuture<String> successCase = fetchServiceData(false).handle((value,ex)-> {
            if(value!=null){
                return value.toUpperCase();
            }
            else {
                return "FALLBACK_DATA";
            }
        });
          

        CompletableFuture<String> failureCase = fetchServiceData(true).handle((value,ex)-> {
            if(value!=null){
                return value.toUpperCase();
            }
            else {
                return "FALLBACK_DATA";
            }
        });

        // Output should print: VALID_DATA
        System.out.println(successCase.join());

        // Output should print: FALLBACK_DATA
        System.out.println(failureCase.join());
    }
}