package com.example;

/**
 * OrderStatus enum for e-commerce order processing.
 * Represents the lifecycle states of an order:
 * PENDING → SHIPPED → DELIVERED, or CANCELLED at any point.
 */
public enum OrderStatus {
    PENDING("PENDING"),
    SHIPPED("SHIPPED"),
    DELIVERED("DELIVERED"),
    CANCELLED("CANCELLED");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    /** Case-insensitive lookup from a string. Throws IllegalArgumentException if not found. */
    public static OrderStatus fromString(String text) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.value.equalsIgnoreCase(text)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown order status: " + text);
    }
}