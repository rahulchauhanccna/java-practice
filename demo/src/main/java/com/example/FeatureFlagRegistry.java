package com.example;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Thread-safe feature flag registry using CopyOnWriteArrayList.
 * Demonstrates safe concurrent reads and writes:
 * - Reader thread iterates flags every 500ms
 * - Writer thread toggles flags (adds/removes) after delays
 * CopyOnWriteArrayList ensures readers never see ConcurrentModificationException.
 */
public class FeatureFlagRegistry {

    /** Thread-safe list of active feature flags. */
    CopyOnWriteArrayList<String> activeFlags;

    FeatureFlagRegistry() {
        this.activeFlags = new CopyOnWriteArrayList<>();
    }

    /** Add a flag if not already present (no duplicates). */
    public void enableFlag(String flag) {
        activeFlags.addIfAbsent(flag);
    }

    /** Remove a flag if present. */
    public void disableFlag(String flag) {
        activeFlags.remove(flag);
    }

    /** Print all currently active flags (safe during concurrent modification). */
    public void printActiveFlags() {
        activeFlags.stream().forEach(System.out::println);
    }

    public static void main(String[] args) throws InterruptedException {
        FeatureFlagRegistry registry = new FeatureFlagRegistry();
        registry.enableFlag("NEW_CHECKOUT_UI");
        registry.enableFlag("DARK_MODE");

        // Reader Thread: reads flags every 500ms (5 iterations)
        Thread reader = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("--- Reading Flags ---");
                registry.printActiveFlags();
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        // Writer Thread: toggles flags after delays (simulating admin actions)
        Thread writer = new Thread(() -> {
            try {
                Thread.sleep(700);
                System.out.println(">> ADMIN TOGGLING: Enabling AI_RECOMMENDATIONS <<");
                registry.enableFlag("AI_RECOMMENDATIONS");

                Thread.sleep(800);
                System.out.println(">> ADMIN TOGGLING: Disabling DARK_MODE <<");
                registry.disableFlag("DARK_MODE");
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        });

        reader.start();
        writer.start();
        reader.join();
        writer.join();
    }
}