package com.example.collections;

import java.util.*;
import java.util.concurrent.*;

/**
 * Comprehensive telemetry pipeline demonstrating Java concurrent collections:
 * 
 * 1. ConcurrentHashMap        – thread-safe IP rate limiter
 * 2. ArrayBlockingQueue       – bounded ingestion buffer (producer-consumer)
 * 3. LinkedHashSet (sync)     – deduplication with insertion-order preservation
 * 4. TreeMap (NavigableMap)   – sorted risk-score leaderboard
 * 5. LinkedHashMap (LRU)      – session cache with auto-eviction (capacity 100)
 * 6. CopyOnWriteArrayList     – lock-free dynamic blacklist
 */
public class TelemetryPipeline {

    // ---- 1. Rate Limiter Storage (thread-safe) ----
    private final ConcurrentMap<String, Integer> ipRateLimiter = new ConcurrentHashMap<>();

    // ---- 2. Thread-Safe Bounded Ingestion Buffer (producer-consumer) ----
    private final BlockingQueue<TransactionEvent> eventBuffer = new ArrayBlockingQueue<>(1000);

    // ---- 3. Deduplication (custom equals/hashCode on UserKey) ----
    private final Set<UserKey> processedUsers = Collections.synchronizedSet(new LinkedHashSet<>());

    // ---- 4. Sorted Risk Leaderboard (NavigableMap for range queries) ----
    private final NavigableMap<Double, String> riskLeaderboard = new TreeMap<>();

    // ---- 5. LRU Session Cache (access-ordered LinkedHashMap with capacity 100) ----
    private final Map<String, UserSession> sessionCache = new LinkedHashMap<String, UserSession>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, UserSession> eldest) {
            return size() > 100; // LRU eviction policy
        }
    };

    // ---- 6. Lock-Free Dynamic Blacklist Rules ----
    private final CopyOnWriteArrayList<String> blacklistedRegions = new CopyOnWriteArrayList<>();

    // ====================================================================
    // Data Models
    // ====================================================================
    public record TransactionEvent(String ip, String userId, double riskScore, String region) {};
    public record UserSession(String userId, String ip, String region, double lastRiskScore) {};

    /** Key for deduplication — equality based ONLY on userId. */
    public static class UserKey {
        private final String userId;

        public UserKey(String userId) {
            this.userId = userId;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (this.getClass() != obj.getClass() || obj == null) return false;
            UserKey userKey = (UserKey) obj;
            return userKey.userId.equals(userId);
        }
        @Override
        public int hashCode() {
            return Objects.hash(userId);
        }
    }

    // ====================================================================
    // Pipeline Logic Methods
    // ====================================================================

    /**
     * Producer: rate-limits by IP, then enqueues the event.
     * Returns false if rate-limited or queue is full.
     */
    public boolean processIncomingRequest(TransactionEvent event) {
        // Atomic increment: merge(ip, 1, Integer::sum) atomically increments or inserts
        int count = ipRateLimiter.merge(event.ip(), 1, Integer::sum);
        if (count > 100)
            return false; // Rate-limited
        try {
            eventBuffer.put(event); // Blocks if queue is full
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        return true;
    }

    /**
     * Consumer: continuously polls the buffer and processes events.
     * Steps: region blacklist check → dedup by userId → leaderboard update → session cache update.
     */
    public void startWorkerConsumer() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                TransactionEvent transactionEvent = eventBuffer.take(); // Blocks if empty

                // Step A: Check region blacklist
                if (blacklistedRegions.contains(transactionEvent.region)) {
                    System.out.println("Blocked event from region: " + transactionEvent.region());
                    continue;
                }

                // Step B: Deduplicate by userId
                UserKey userKey = new UserKey(transactionEvent.userId());
                if (!processedUsers.add(userKey)) {
                    System.out.println("Duplicate transaction skipped for user: " + transactionEvent.userId());
                    continue;
                }

                // Step C: Update risk leaderboard (sorted by riskScore ascending)
                riskLeaderboard.put(transactionEvent.riskScore(), transactionEvent.userId);

                // Step D: Update LRU session cache
                sessionCache.put(transactionEvent.userId,
                    new UserSession(
                        transactionEvent.userId, transactionEvent.ip,
                        transactionEvent.region, transactionEvent.riskScore
                    ));

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        TelemetryPipeline pipeline = new TelemetryPipeline();

        // Dynamically blacklist a region
        pipeline.blacklistedRegions.add("EMBARGOED_REGION");

        // Start consumer thread
        Thread consumer = new Thread(pipeline::startWorkerConsumer);
        consumer.start();

        // Producer: submit 105 events via 4 parallel threads
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 105; i++) {
            final int id = i;
            executorService.submit(() -> {
                String ip = (id < 101) ? "192.168.1.1" : "10.0.0.2";
                String userId = "user_" + id;
                String region = (id == 50) ? "EMBARGOED_REGION" : "US-EAST";
                double risk = ThreadLocalRandom.current().nextDouble(0.0, 100.0);
                boolean accepted = pipeline.processIncomingRequest(
                    new TransactionEvent(ip, userId, risk, region));
                if (!accepted) {
                    System.out.println("REQUEST REJECTED (Rate-limited/Full): Request #" + id + " from " + ip);
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
        consumer.interrupt();
        consumer.join();

        System.out.println("\n--- FINAL STATE ---");
        System.out.println("Highest Risk Users: " + pipeline.riskLeaderboard.descendingMap());
        System.out.println("Session Cache Size (Max 100): " + pipeline.sessionCache.size());
    }
}