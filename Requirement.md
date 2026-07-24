# The E-Commerce Order Processor Challenge

## Overview

You are building a backend processing component for an e-commerce platform. The task involves processing a list of customer orders, filtering them based on status, calculating total values, and handling potential missing data safely.

---

## Step 1: The Java 8 Foundation

Using **Java 8 syntax only** (no records, no modern switch expressions).

### 1.1 Create the Customer POJO

Create a `Customer` class with the following fields:

| Field | Type | Description |
|-------|------|-------------|
| `name` | `String` | Customer's full name |
| `email` | `String` | Customer's email (can be `null` for guest checkouts) |

**Important:** The email getter return type should be wrapped in `Optional<String>` to handle null values safely.

```java
public class Customer {
    private String name;
    private Optional<String> email;
    
    public Optional<String> getEmail() {
        return email;
    }
    
    // ... other getters and setters
}
```

### 1.2 Create the Order POJO

Create an `Order` class with the following fields:

| Field | Type | Description |
|-------|------|-------------|
| `orderId` | `String` | Unique order identifier |
| `amount` | `Double` | Order total amount |
| `status` | `String` | Order status (e.g., "PENDING", "SHIPPED", "DELIVERED") |
| `customer` | `Customer` | Customer object reference |

### 1.3 Stream Pipeline #1: Guest Checkout Processing

Write a processing method using a **single Stream pipeline** that takes a `List<Order>` and:

1. **Filters out** orders with a status of `"CANCELLED"`
2. **Extracts** a distinct list of all guest checkout customer names (where the email `Optional` is empty)
3. **Collects** them into an **unmodifiable list**

```java
List<String> guestCustomers = orders.stream()
    .filter(order -> !"CANCELLED".equals(order.getStatus()))
    .map(Order::getCustomer)
    .filter(customer -> !customer.getEmail().isPresent())
    .map(Customer::getName)
    .distinct()
    .collect(Collectors.toUnmodifiableList());
```

### 1.4 Stream Pipeline #2: Revenue by Status

Write a **second Stream pipeline** that uses `Collectors.groupingBy` to group the total revenue (sum of amount) by each order status.

```java
Map<String, Double> revenueByStatus = orders.stream()
    .collect(Collectors.groupingBy(
        Order::getStatus,
        Collectors.summingDouble(Order::getAmount)
    ));
```

---

## Step 2: The Modern Upgrade (Java 11 to 21+)

Now, refactor your entire solution using modern Java paradigms to eliminate boilerplate code.

### 2.1 Refactor to Records

Convert the verbose `Order` and `Customer` POJOs into **shallow data carriers using Java Records**.

```java
public record Customer(String name, Optional<String> email) {}

public record Order(String orderId, Double amount, String status, Customer customer) {}
```

### 2.2 Simplify Stream Collection

Replace the Java 8 `.collect(Collectors.toList())` or unmodifiable wrappers with the clean **Java 16 `.toList()` terminal stream method**.

```java
// Before (Java 8)
List<String> list = stream.collect(Collectors.toList());

// After (Java 16+)
List<String> list = stream.toList();
```

### 2.3 Implement Pattern Matching for Switch

Write a method `getShippingPriority(Order order)` that uses an **enhanced switch expression** evaluating the order status string.

| Status | Priority |
|--------|----------|
| `"DELIVERED"` | `Priority.LOW` |
| `"SHIPPED"` | `Priority.MEDIUM` |
| `"PENDING"` (amount > $10,000) | `Priority.URGENT` |
| `"PENDING"` (amount ≤ $10,000) | `Priority.HIGH` |

```java
public enum Priority {
    LOW, MEDIUM, HIGH, URGENT
}

public Priority getShippingPriority(Order order) {
    return switch (order.status()) {
        case "DELIVERED" -> Priority.LOW;
        case "SHIPPED" -> Priority.MEDIUM;
        case "PENDING" -> {
            if (order.amount() > 10000) {
                yield Priority.URGENT;
            }
            yield Priority.HIGH;
        }
        default -> throw new IllegalArgumentException("Unknown status: " + order.status());
    };
}
```

---

## Summary

| Feature | Java 8 Approach | Modern Java Approach |
|---------|-----------------|---------------------|
| Data Models | POJOs with boilerplate | Records (Java 14+) |
| Stream Collection | `Collectors.toList()` | `.toList()` (Java 16+) |
| Switch Logic | Traditional if-else | Pattern matching switch (Java 17+) |
| Null Safety | Manual null checks | `Optional` wrapper |

---

## Expected Deliverables

1. `Customer.java` - Customer POJO/Record
2. `Order.java` - Order POJO/Record  
3. `Pipeline.java` - Stream processing logic
4. `TestMain.java` - Test harness with sample data
5. `OrderStatus.java` - Status enum (optional, for type safety)