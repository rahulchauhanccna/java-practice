package com.example;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestMain {

        public static void main(String[] args) {
                // System.out.println("Hello, World!");

                // List<String> listA = List.of("Apple", "Banana", "Cherry");
                // List<String> listB = List.of("Banana", "Cherry", "Dragonfruit");
                // Set<String> setA = new HashSet<>(listA);

                // // find the common elements (Intersection) between two lists
                // listB.stream().filter(setA::contains)
                //                 .forEach(System.out::println);

                // // find elements present in List A but NOT in List B (Difference)
                // System.out.println("Elements present in List A but NOT in List B:");
                // listA.stream().filter(v -> !listB.contains(v))
                //                 .forEach(System.out::println);

                // // merge two lists of objects and remove duplicates based on a specific property
                // System.out.println("Merged list with duplicates removed:");
                // Stream.concat(listA.stream(), listB.stream())
                //                 .distinct()
                //                 .forEach(System.out::println);

                // // INIT
                // List<User> users = List.of(new User(1, "Alice"), new User(2, "Bob"), new User(1, "Rahul"));
                // List<Order> orders = List.of(new Order(101, 1, "Laptop"), new Order(102, 2, "Phone"));

                // // Join users and orders based on userId
                // System.out.println("Joined Users and Orders:");
                // orders.stream()
                //                 .collect(Collectors.toMap(Order::getUserId, order -> order)).forEach((k, v) -> {
                //                         System.out.println(k + "-->" + v.getProduct());
                //                 });

                // System.out.println("Priting DTO ");
                // // users.stream().map(user -> new UserOrderDTO(user, orderMap.get(user.getId())))
                // //                 .forEach(System.out::println);

                // System.out.println("distintByKey");
                // // distintByKey
                // users.stream().filter(distintByKey(User::getId)).forEach(System.out::println);

        
             
                // * Your MinStack object will be instantiated and called as such:
                 MinStack obj = new MinStack();
                  obj.push(-2);
                  obj.push(0);
                  obj.push(-3);
                  obj.getMin();
                  obj.pop();
                  obj.top();
                  obj.getMin();
                 int param_3 = obj.top();
                  int param_4 = obj.getMin();
                 
        }

        public static <T> Predicate<T> distintByKey(Function<? super T, ?> keyExtractor) {
                Set<Object> seen = ConcurrentHashMap.newKeySet();
                return t -> seen.add(keyExtractor.apply(t));
        }
}
