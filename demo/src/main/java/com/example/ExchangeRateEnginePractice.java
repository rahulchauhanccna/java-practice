package com.example;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * <b>ExchangeRateEnginePractice</b> demonstrates a thread-safe exchange rate cache
 * using {@link ReentrantReadWriteLock} with a read-downgrade pattern.
 * <p>
 * This implementation simulates a real-world caching layer for foreign exchange rates:
 * <ul>
 *   <li><b>Read-Write Lock</b> – Allows concurrent reads while blocking writes.</li>
 *   <li><b>TTL-Based Expiry</b> – Entries expire after 1 second, forcing a fresh fetch.</li>
 *   <li><b>Read Downgrade</b> – After acquiring the write lock and updating the cache,
 *       the code attempts to downgrade to a read lock before releasing the write lock,
 *       enabling other readers to proceed without contention.</li>
 * </ul>
 * <p>
 * The main method runs three phases:
 * <ol>
 *   <li>Concurrent initial cache misses (all threads trigger one HTTP call).</li>
 *   <li>Concurrent cache hits (no HTTP calls while TTL is valid).</li>
 *   <li>Cache expiry & refresh (TTL expires, exactly one HTTP call).</li>
 * </ol>
 *
 * @see ReentrantReadWriteLock
 * @see ExchangeRateCache
 * @see RateEntry
 */
public class ExchangeRateEnginePractice {

    /**
     * A record that holds a single exchange rate value and its capture timestamp.
     *
     * @param rate      the exchange rate
     * @param timestamp the system millis when the rate was fetched
     */
    public record RateEntry(double rate, long timestamp) {
        /**
         * Checks whether this entry has outlived the given TTL.
         *
         * @param ttlMs time-to-live in milliseconds
         * @return {@code true} if the entry is stale
         */
        public boolean isExpired(long ttlMs) {
            return System.currentTimeMillis() - timestamp > ttlMs;
        }
    }

    /**
     * A thread-safe exchange rate cache backed by {@link ReentrantReadWriteLock}.
     * <p>
     * Uses a <b>read-downgrade</b> pattern:
     * <ol>
     *   <li>Acquire the read lock and check the cache.</li>
     *   <li>If a valid entry exists, return it immediately.</li>
     *   <li>Release the read lock and acquire the write lock.</li>
     *   <li><b>Double-check</b> – another writer may have updated the cache already.</li>
     *   <li>If still stale, fetch from the remote API and store the entry.</li>
     *   <li>Downgrade by acquiring the read lock before releasing the write lock,
     *       so other concurrent readers are not blocked.</li>
     *   <li>Re-read the value under the read lock and return.</li>
     * </ol>
     */
    public static class ExchangeRateCache {
        private static final long TTL_MS = 1000; // 1 second TTL for demo
        private final Map<String, RateEntry> cache = new HashMap<>();

        private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
        private final ReentrantReadWriteLock.ReadLock readLock = rwl.readLock();
        private final ReentrantReadWriteLock.WriteLock writeLock = rwl.writeLock();

        /**
         * Returns the cached exchange rate for the given currency pair, fetching
         * from the (simulated) remote API if the cached entry is absent or expired.
         *
         * @param currencyPair e.g. {@code "USD_EUR"}
         * @return the exchange rate
         */
        public double getOrFetchRate(String currencyPair) {
            // TODO: Implement Read Check -> Unlock Read -> Lock Write 
            //       -> Double-Check -> Update -> Lock Read (Downgrade) 
            //       -> Unlock Write -> Read & Return -> Unlock Read

            readLock.lock();
            try {
                RateEntry rateEntry = cache.get(currencyPair);
                if(rateEntry!=null && !rateEntry.isExpired(TTL_MS)){
                    return rateEntry.rate();
                }  
            } finally {
                readLock.unlock();
            }
            writeLock.lock();

            try {
                RateEntry rateEntry = cache.get(currencyPair);
                if(rateEntry==null || rateEntry.isExpired(TTL_MS)){
                    double fetchRateFromRemoteApi = fetchRateFromRemoteApi(currencyPair);
                    cache.put(currencyPair,new RateEntry(fetchRateFromRemoteApi, System.currentTimeMillis()));
                }
                readLock.unlock();
            }
            finally {
                writeLock.unlock();
            }

            try {
                return cache.get(currencyPair).rate();
            } finally {
                readLock.unlock();
            }
        }

        // Mock External REST API call (Latency simulation)
        private double fetchRateFromRemoteApi(String currencyPair) {
            System.out.println("  [HTTP Call] Fetching fresh exchange rate for " + currencyPair + "...");
            sleep(100); // 100ms API latency
            return switch (currencyPair) {
                case "USD_EUR" -> 0.92;
                case "USD_GBP" -> 0.79;
                case "USD_JPY" -> 155.40;
                default -> 1.0;
            };
        }

        private void sleep(long ms) {
            try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }

    /**
     * Entry point that drives the three-phase demonstration.
     *
     * @param args command-line arguments (ignored)
     * @throws InterruptedException if the executor is interrupted while awaiting termination
     */
    public static void main(String[] args) throws InterruptedException {
        ExchangeRateCache rateCache = new ExchangeRateCache();
        ExecutorService pool = Executors.newFixedThreadPool(5);

        System.out.println("--- Phase 1: Concurrent Initial Cache Misses ---");
        // 5 threads request USD_EUR simultaneously on empty cache
        for (int i = 0; i < 5; i++) {
            final int threadId = i;
            pool.submit(() -> {
                double rate = rateCache.getOrFetchRate("USD_EUR");
                System.out.println("Thread-" + threadId + " got rate: " + rate);
            });
        }

        Thread.sleep(500); // Wait 500ms (cache is still valid)

        System.out.println("\n--- Phase 2: Concurrent Cache Hits (Unexpired) ---");
        // Should hit cache without making HTTP calls
        for (int i = 0; i < 3; i++) {
            final int threadId = i;
            pool.submit(() -> {
                double rate = rateCache.getOrFetchRate("USD_EUR");
                System.out.println("Thread-" + threadId + " (cached) got rate: " + rate);
            });
        }

        Thread.sleep(1000); // Wait over 1s to expire TTL

        System.out.println("\n--- Phase 3: Cache Expired Refresh ---");
        // Rate should expire and trigger exactly 1 fresh HTTP call
        pool.submit(() -> {
            double rate = rateCache.getOrFetchRate("USD_EUR");
            System.out.println("Thread-Refresh got rate: " + rate);
        });

        pool.shutdown();
        pool.awaitTermination(3, TimeUnit.SECONDS);
    }
}