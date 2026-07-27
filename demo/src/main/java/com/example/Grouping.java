package com.example;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Demonstrates various Stream API operations:
 * groupingBy, mapping, summingDouble, primitive streams, summaryStatistics, and Optional filtering.
 */
public class Grouping {

    public static void main(String[] args) {

        // ---- Local record for demo data ----
        record Employee(String name, String department, double salary) {}

        List<Employee> employees = List.of(
                new Employee("Alice", "IT", 6000),
                new Employee("Bob", "HR", 4000),
                new Employee("Charlie", "IT", 7000));

        // ---- Group employees by department name ----
        Map<String, List<Employee>> namesByDept = employees.stream()
                .collect(Collectors.groupingBy(Employee::department));

        // ---- Group by department, then collect only employee names ----
        Map<String, List<String>> collect = employees.stream()
            .collect(Collectors.groupingBy(
                Employee::department,
                Collectors.mapping(Employee::name, Collectors.toList())
            ));
        System.out.println(namesByDept);

        // ---- Calculate total salary spent per department ----
        Map<String, Double> totalSalaryByDept = employees.stream()
            .collect(Collectors.groupingBy(
                Employee::department,
                Collectors.summingDouble(Employee::salary)
            ));

        // ---- Object stream -> primitive stream (mapToInt) ----
        List<Integer> nums = List.of(1, 2, 3, 4, 5, 6, 7);
        int maxValue = nums.stream()
            .mapToInt(Integer::intValue)
            .max()
            .orElse(0);
        System.out.println(maxValue);

        // ---- Primitive stream -> object stream (boxed) ----
        List<Integer> intStream = IntStream.of(1, 2, 3, 4, 5, 6, 7)
            .boxed()
            .collect(Collectors.toList());

        // ---- mapToObj: convert primitive int to User objects ----
        IntStream.of(1, 2, 3, 4, 5, 6, 7)
            .mapToObj(i -> new User(i, "Employee " + i))
            .forEach(System.out::println);

        // ---- IntSummaryStatistics: max, min, avg, sum in one pass ----
        List<Integer> scores = List.of(90, 85, 78, 92, 65);
        IntSummaryStatistics stats = scores.stream()
            .mapToInt(Integer::intValue)
            .summaryStatistics();
        System.out.println("Max: " + stats.getMax());
        System.out.println("Min: " + stats.getMin());
        System.out.println("Avg: " + stats.getAverage());
        System.out.println("Sum: " + stats.getSum());

        // ---- Filter Optional values: keep only present ones ----
        List<String> userIds = List.of("1", "2", "3", "4");
        List<User> collect2 = userIds.stream()
            .map(id -> findUserById(id))       // Returns Optional<User>
            .filter(Optional::isPresent)        // Keep only present Optionals
            .map(Optional::get)                 // Unwrap to User
            .collect(Collectors.toList());
    }

    /** Mock lookup: returns a User for ids "2" and "4", empty otherwise. */
    private static Optional<User> findUserById(String id) {
        if ("2".equals(id) || "4".equals(id)) {
            return Optional.of(new User(1, "User-" + id));
        }
        return Optional.empty();
    }
}