package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

public class GuestCheckoutProcessingPipelineTest {

    private List<Order> sampleOrders;
    private GuestCheckoutProcessingPipeline pipeline;

    @BeforeEach
    void setup() {
        pipeline = new GuestCheckoutProcessingPipeline();
        
        // 1. Regular customer (has email)
        Customer alice = new Customer("Alice", Optional.ofNullable("alice@example.com"));
        // 2. Guest customer (null email)
        Customer bob = new Customer("Bob", Optional.empty());
        // 3. Another guest customer (null email)
        Customer charlie = new Customer("Charlie", Optional.empty());
        // 4. Guest customer who cancelled
        Customer david = new Customer("David", Optional.empty());

        sampleOrders = Arrays.asList(
            new Order("ORD-001", 150.00, OrderStatus.DELIVERED, alice),
            new Order("ORD-002", 50.00, OrderStatus.PENDING, bob),
            new Order("ORD-003", 12000.00, OrderStatus.PENDING, charlie), // High value pending
            new Order("ORD-004", 300.00, OrderStatus.CANCELLED, david),  // Cancelled order - should be filtered out
            new Order("ORD-005", 250.00, OrderStatus.SHIPPED, alice)
        );
    }

    @Test
    void testGuestCheckoutProcessingPipeline() {
        List<String> uniqueGuestCustomers = pipeline.run(sampleOrders);
        
        // Bob and Charlie are guest customers (no email) with non-cancelled orders
        // David is a guest customer but his order is CANCELLED, so filtered out
        assertEquals(2, uniqueGuestCustomers.size());
        assertTrue(uniqueGuestCustomers.contains("Bob"));
        assertTrue(uniqueGuestCustomers.contains("Charlie"));
        assertFalse(uniqueGuestCustomers.contains("David"));
    }
}