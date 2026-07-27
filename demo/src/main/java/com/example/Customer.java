package com.example;

import java.util.Optional;

/**
 * Customer record for e-commerce order processing.
 * A customer has a name and an optional email address.
 * An empty email indicates a guest customer (no account).
 */
public record Customer(String name, Optional<String> email) {
}