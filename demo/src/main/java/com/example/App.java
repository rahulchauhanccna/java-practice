package com.example;

import java.util.Scanner;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        final Scanner sc = new Scanner(System.in);
        System.out.println("Enter space-separated integers:");
        final int arr[] = Stream.of(sc.nextLine().trim().split(" "))
                .mapToInt(Integer::parseInt)
                .toArray();
        //IntStream.range(0, arr.length).filter(v-> v % 2 == 0).forEach(i -> System.out.println(arr[i]));

        IntStream.range(0, arr.length).findFirst().ifPresent(System.out::println);
        
    }
}