package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Interview {
    public static void main(String[] args) {
        List<String> list1 = List.of("aa","bb","cc");
        List<String> list2 = List.of("cc","dd","aa");
        List<String> ans  = list1.stream().filter(v-> v.equals("aa")).collect(Collectors.toList());
        List ans2 = list2.stream().filter(v-> v.equals("aa")).collect(Collectors.toList());
        ans.addAll(ans2);
        ans.stream().forEach(System.out :: println);  
        Stream.concat(list1.stream(), list2.stream()).filter(v-> v.equals("aa")) .forEach(System.out :: println);     
    }
    

    public String readConfigFile(Path filePath) {
    // Resources in parentheses are automatically closed at the end of the block
    try (BufferedReader reader = Files.newBufferedReader(filePath)) {
        return reader.readLine();
    } catch (NoSuchFileException e) {
        // log.warn("File not found at path: {}", filePath);
        return "DEFAULT_CONFIG";
    } catch (IOException e) {
        // log.error("Failed to read config file", e);
        throw new RuntimeException("Config initialization failed", e); // Re-wrap & escalate
    }
}
}
