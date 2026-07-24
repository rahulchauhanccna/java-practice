package com.example;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Grouping {

    public static void main(String[] args) {

        record Employee(String name, String department, double salary) {
        }

        List<Employee> employees = List.of(
                new Employee("Alice", "IT", 6000),
                new Employee("Bob", "HR", 4000),
                new Employee("Charlie", "IT", 7000));

        // Group by department name
        Map<String, List<Employee>> namesByDept = employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::department));

        Map<String, List<String>> collect = employees.stream().collect(Collectors.groupingBy(Employee:: department,Collectors.mapping(Employee::name,Collectors.toList())));
        System.out.println(namesByDept);

        //Calculate the total salary spent per department.
        Map<String, Double> totalSalaryByDept = employees.stream().collect(Collectors.groupingBy(Employee::department,Collectors.summingDouble(Employee:: salary)));

        List<Integer> nums  = List.of(1,2,3,4,5,6,7);

        //To go from Object Stream to Primitive Stream:
        int  maxValue = nums.stream().mapToInt(Integer::intValue).max().isPresent() ? nums.stream().mapToInt(Integer::intValue).max().getAsInt() : 0;
        System.out.println(maxValue);   
        
        //To go from Primitive Stream to Object Stream
        List<Integer> intStream = IntStream.of(1,2,3,4,5,6,7).boxed().collect(Collectors.toList());

        // Find the employee with the highest salary in each department.
        System.out.println("Test HEllo");
        IntStream.of(1,2,3,4,5,6,7).mapToObj(i -> new User(i,"Employee " + i)).forEach(System.out::println);
        

        // Main
        List<Integer> scores = List.of(90, 85, 78, 92, 65);
        IntSummaryStatistics    stats = scores.stream().mapToInt(Integer::intValue).summaryStatistics();
        System.out.println("Max: " + stats.getMax());
        System.out.println("Min: " + stats.getMin());
        System.out.println("Avg: " + stats.getAverage());
        System.out.println("Sum: " + stats.getSum());

        List<String> userIds = List.of("1", "2", "3", "4");
        System.out.println("Active User");
        List<User> collect2 = userIds.stream()
            .map(id -> findUserById(id)) // Returns Optional<User>
            .filter(Optional :: isPresent)
            .map(Optional:: get)
            .collect(Collectors.toList());
    }

  private static Optional<User> findUserById(String id) {
        if ("2".equals(id) || "4".equals(id)) {
            return Optional.of(new User(1,"User-" + id));
        }
        return Optional.empty();
    }

}
