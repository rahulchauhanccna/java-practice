package com.example;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RevenueByStatusPipelineTest {

    private List<Order> sampleOrders;
    private RevenueByStatusPipeline pipeline;

    @BeforeEach
    void setup() {
        pipeline = new RevenueByStatusPipeline();
        
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
    void testRun() {
        // Run the pipeline
        var revenueByStatus = pipeline.run(sampleOrders);
        assert revenueByStatus.get(OrderStatus.DELIVERED) == 150.00;
        assert revenueByStatus.get(OrderStatus.PENDING) == 12050.00; // 50 + 12000
        assert revenueByStatus.get(OrderStatus.CANCELLED) == 300.00;
        assert revenueByStatus.get(OrderStatus.SHIPPED) == 250.00; 
       
    }
}
