package com.example;

/**
 * UserOrderDTO
 */
public class UserOrderDTO { 
        User user;
        Order order;
        public UserOrderDTO(User user2, Order order2) {
            this.user = user2;
            this.order = order2;
        }
        public User getUser() {
            return user;
        }
        public void setUser(User user) {
            this.user = user;
        }
        public Order getOrder() {
            return order;
        }
        public void setOrder(Order order) {
            this.order = order;
        }
    @Override
    public String toString() {
        return "UserOrderDTO [user=" + user + ", order=" + order + "]";
    }

}
