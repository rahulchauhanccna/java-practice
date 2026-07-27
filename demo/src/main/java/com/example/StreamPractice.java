package com.example;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

/** Employee model used in StreamPractice exercises. */
class Employee {
    private String name;
    private int age;
    private String department;
    private double salary;
    private List<String> skills;

    public Employee(String name, int age, String department, double salary, List<String> skills) {
        this.name = name;
        this.age = age;
        this.department = department;
        this.salary = salary;
        this.skills = skills;
    }

    public String getName() { return name; }
    public int getAge() { return age; }
    public String getDepartment() { return department; }
    public double getSalary() { return salary; }
    public List<String> getSkills() { return skills; }

    @Override
    public String toString() {
        return "Employee{name='" + name + "'}";
    }
}

/**
 * Comprehensive Stream API practice covering:
 * filter, map, flatMap, distinct, sorted, max, anyMatch,
 * groupingBy, partitioningBy, joining, and counting.
 */
public class StreamPractice {
    public static void main(String[] args) {
        List<Employee> employees = Arrays.asList(
                new Employee("Alice", 29, "Engineering", 85000, Arrays.asList("Java", "Spring", "Docker")),
                new Employee("Bob", 35, "Engineering", 95000, Arrays.asList("Java", "Go", "Kubernetes")),
                new Employee("Charlie", 24, "HR", 55000, Arrays.asList("Communication", "Recruiting")),
                new Employee("David", 42, "Marketing", 75000, Arrays.asList("SEO", "Copywriting", "Java")),
                new Employee("Emma", 19, "HR", 60000, Arrays.asList("Onboarding", "Excel")),
                new Employee("Frank", 31, "Engineering", 68000, Arrays.asList("Python", "AWS")));

        // Q1: Find employees with salary > 70,000 and collect their names
        employees.stream()
            .filter(v -> v.getSalary() > 70000)
            .map(Employee::getName)
            .forEach(System.out::println);

        // Q2: Average age of employees who have "Java" in their skills
        OptionalDouble average = employees.stream()
            .filter(v -> v.getSkills().contains("Java"))
            .mapToInt(Employee::getAge)
            .average();
        average.ifPresent(avg -> System.out.println("Average age (Java devs): " + avg));

        // Q3: Distinct, sorted list of all department names (alphabetical)
        employees.stream()
            .map(Employee::getDepartment)
            .distinct()
            .sorted()
            .forEach(System.out::println);

        // Q4: Employee with the highest salary (returns Optional)
        employees.stream()
            .max(Comparator.comparingDouble(Employee::getSalary))
            .ifPresent(System.out::println);

        // Q5: Flat list of every unique skill across the company
        employees.stream()
            .flatMap(e -> e.getSkills().stream())
            .distinct()
            .forEach(System.out::println);

        // Q6: Check if at least one employee is under 21
        System.out.println("Any employee under 21? " + employees.stream().anyMatch(v -> v.getAge() < 21));

        // Q7: Group employees by department and sum their salaries
        employees.stream()
            .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.summingDouble(Employee::getSalary)
            ));

        // Q8: Partition employees into age >= 30 vs under 30
        Map<Boolean, List<Employee>> partitioned = employees.stream()
            .collect(Collectors.partitioningBy(e -> e.getAge() >= 30));
        partitioned.forEach((key, value) -> System.out.println(key + " -> " + value));

        // Q9: Comma-separated string of all names, sorted alphabetically
        String namesCsv = employees.stream()
            .map(Employee::getName)
            .sorted()
            .collect(Collectors.joining(", "));
        System.out.println(namesCsv);

        // Q10: Find the most common skill
        employees.stream()
            .flatMap(e -> e.getSkills().stream())
            .collect(Collectors.groupingBy(skill -> skill, Collectors.counting()))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .ifPresent(entry -> System.out.println("Most common skill: " + entry.getKey() + " (" + entry.getValue() + ")"));
    }
}