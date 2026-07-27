package com.example;

/**
 * Order record for e-commerce order processing.
 * Contains the order ID, monetary amount, current status, and the associated customer.
 */
public record Order(String orderId, Double amount, OrderStatus status, Customer customer) {
}