package com.example;

/**
 * Order record for e-commerce order processing.
 */
public record Order(String orderId, Double amount, OrderStatus status, Customer customer) {
}