package com.example;

import java.util.Optional;

/**
 * Customer record for e-commerce order processing.
 */
public record Customer(String name, Optional<String> email) {
}