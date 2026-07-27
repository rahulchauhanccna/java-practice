package com.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pipeline for calculating total revenue grouped by order status.
 * Iterates through orders and sums the amounts for each status (PENDING, SHIPPED, DELIVERED, CANCELLED).
 */
public class RevenueByStatusPipeline {

    /**
     * Calculates total revenue for each order status.
     *
     * @param orders List of orders to process
     * @return Map of OrderStatus -> total revenue amount
     */
    public Map<OrderStatus, Double> run(List<Order> orders) {
        // Initialize a map to hold the revenue by status
        Map<OrderStatus, Double> revenueByStatus = new HashMap<>();

        // Iterate through the list of orders
        for (Order order : orders) {
            OrderStatus status = order.status();
            Double amount = order.amount();

            // Update the revenue for the corresponding status
            // getOrDefault handles the first occurrence (default 0.0)
            revenueByStatus.put(status, revenueByStatus.getOrDefault(status, 0.0) + amount);
        }

        return revenueByStatus;
    }
}