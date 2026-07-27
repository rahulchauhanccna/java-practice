package com.example;

import java.util.Scanner;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Simple app that reads space-separated integers from stdin,
 * then finds and prints the first element of the array.
 * Demonstrates basic Stream & IntStream usage.
 */
public class App 
{
    public static void main( String[] args )
    {
        final Scanner sc = new Scanner(System.in);
        System.out.println("Enter space-separated integers:");

        // ---- Read line, split by spaces, parse each token as int, collect into int[] ----
        final int arr[] = Stream.of(sc.nextLine().trim().split(" "))
                .mapToInt(Integer::parseInt)   // String -> int via Integer.parseInt
                .toArray();                     // terminal operation: produces int[]

        // ---- Find the first element (index 0) and print it (if present) ----
        IntStream.range(0, arr.length).findFirst().ifPresent(System.out::println);
    }
}