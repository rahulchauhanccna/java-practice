package com.example;

/**
 * Data Transfer Object that combines a User with their Order.
 * Used for joining user and order data in stream pipelines.
 */
public class UserOrderDTO { 
    User user;
    Order order;

    public UserOrderDTO(User user, Order order) {
        this.user = user;
        this.order = order;
    }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    @Override
    public String toString() {
        return "UserOrderDTO [user=" + user + ", order=" + order + "]";
    }
}