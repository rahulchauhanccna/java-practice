package com.example;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

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

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getDepartment() {
        return department;
    }

    public double getSalary() {
        return salary;
    }

    public List<String> getSkills() {
        return skills;
    }

    @Override
    public String toString() {
        return "Employee{name='" + name + "'}";
    }
}

public class StreamPractice {
    public static void main(String[] args) {
        List<Employee> employees = Arrays.asList(
                new Employee("Alice", 29, "Engineering", 85000, Arrays.asList("Java", "Spring", "Docker")),
                new Employee("Bob", 35, "Engineering", 95000, Arrays.asList("Java", "Go", "Kubernetes")),
                new Employee("Charlie", 24, "HR", 55000, Arrays.asList("Communication", "Recruiting")),
                new Employee("David", 42, "Marketing", 75000, Arrays.asList("SEO", "Copywriting", "Java")),
                new Employee("Emma", 19, "HR", 60000, Arrays.asList("Onboarding", "Excel")),
                new Employee("Frank", 31, "Engineering", 68000, Arrays.asList("Python", "AWS")));

        // Find all employees whose salary is greater than 70,000 and collect their
        // names into a List<String>.
        employees.stream().filter(v -> v.getSalary() > 70000)
                .map(Employee::getName).forEach(System.out::println);

        // Find the average age of all employees who have "Java" in their skills list.
        OptionalDouble average = employees.stream().filter(v -> v.getSkills().contains("Java"))
                .mapToInt(Employee::getAge).average();
        if (average.isPresent()) {
            System.out.println("Average" + average.getAsDouble());
        }

        // Get a distinct, sorted list of all department names in alphabetical order.
        employees.stream().map(Employee::getDepartment).distinct().sorted().forEach(System.out::println);

        // Find the Employee with the highest salary. (Bonus: What does this return if
        // the list is empty?)
        System.out.println(employees.stream().max(Comparator.comparingDouble(Employee::getSalary)).get());

        // Collect a flat list of every single unique skill across the entire company.
        employees.stream().flatMap(e -> e.getSkills().stream()).distinct().forEach(System.out::println);

        // Check if there is at least one employee in the company under the age of 21.
        System.out.println(employees.stream().anyMatch(v -> v.getAge() < 21));

        // Question 7: Group employees by department and get the sum of their salaries.

        employees.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment, Collectors.summingDouble(Employee::getSalary)));

        // Question 8: Partition employees into two groups: age $\ge 30$ vs under 30.

        Map<Boolean, List<Employee>> collect = employees.stream()
                .collect(Collectors.partitioningBy(e -> e.getAge() >= 30));
        collect.entrySet().stream().forEach(entry -> {
            System.out.println(entry.getKey() + "->" + entry.getValue());
        });

        // Question 9: Create a single comma-separated string of all names, sorted
        // alphabetically.
        employees.stream().map(Employee::getName).sorted().collect(Collectors.joining(", "));

        // Question 10: Find the single most common skill.

        employees.stream()
                .flatMap(e -> e.getSkills().stream())
                .collect(Collectors.groupingBy(skill -> skill, Collectors.counting()))
                .entrySet()
                .stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getValue)
                .orElse(0L);
    }
}