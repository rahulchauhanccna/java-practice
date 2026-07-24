package com.example;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Pipeline for processing guest checkout orders.
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
                .filter(order -> order.status() != OrderStatus.CANCELLED)
                .filter(order -> !order.customer().email().isPresent())
                .map(Order::customer)
                .map(Customer::name)
                .distinct()
                .collect(Collectors.toUnmodifiableList());
        return uniqueCustomers;
    }
}