package com.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pipeline for calculating revenue by order status.
 */
public class RevenueByStatusPipeline {

    public Map<OrderStatus, Double> run(List<Order> orders) {
        // Initialize a map to hold the revenue by status
        Map<OrderStatus, Double> revenueByStatus = new HashMap<>();

        // Iterate through the list of orders
        for (Order order : orders) {
            OrderStatus status = order.status();
            Double amount = order.amount();

            // Update the revenue for the corresponding status
            revenueByStatus.put(status, revenueByStatus.getOrDefault(status, 0.0) + amount);
        }

       return revenueByStatus;
    }
}