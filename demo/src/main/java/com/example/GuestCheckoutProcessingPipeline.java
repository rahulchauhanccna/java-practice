package com.example;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Pipeline for processing guest checkout orders.
 * Filters out cancelled orders and extracts distinct guest customer names.
 * Guest customers are those with no email (empty Optional).
 */
public class GuestCheckoutProcessingPipeline {

    /**
     * Filters out cancelled orders and extracts distinct guest customer names.
     * Guest customers are those with no email (empty Optional).
     * 
     * @param orders List of orders to process
     * @return Unmodifiable list of unique guest customer names
     */
    public List<String> run(List<Order> orders) {
        List<String> uniqueCustomers = orders.stream()
                // Step 1: Remove cancelled orders
                .filter(order -> order.status() != OrderStatus.CANCELLED)
                // Step 2: Keep only guest customers (no email)
                .filter(order -> !order.customer().email().isPresent())
                // Step 3: Extract Customer objects
                .map(Order::customer)
                // Step 4: Extract customer names
                .map(Customer::name)
                // Step 5: Deduplicate
                .distinct()
                // Step 6: Collect into unmodifiable list
                .collect(Collectors.toUnmodifiableList());
        return uniqueCustomers;
    }
}