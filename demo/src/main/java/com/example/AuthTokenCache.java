package com.example;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LRU (Least Recently Used) cache for auth tokens built on top of LinkedHashMap.
 * When the cache exceeds maxCapacity, the eldest (oldest accessed) entry is evicted.
 */
public class AuthTokenCache {

    /** Underlying map: access-ordered LinkedHashMap so LRU eviction works correctly. */
    Map<String, String> imageCache;

    /**
     * @param maxCapacity maximum number of entries before eviction kicks in
     */
    public AuthTokenCache(int maxCapacity) {
       // accessOrder=true → LinkedHashMap orders entries by last access (get/put)
       imageCache = new LinkedHashMap<>(maxCapacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                // Automatically evict the oldest entry when size exceeds maxCapacity
                return size() > maxCapacity;
            }
        };
    }

    /** Look up a token by userId; returns null if not present. */
    public String getToken(String userId) {
        return imageCache.getOrDefault(userId, null);
    }

    /** Store a token for the given userId. */
    public void putToken(String userId, String token) {
        imageCache.put(userId, token);
    }

    /** Print all entries currently in the cache (in access order). */
    public void printCacheState() {
        imageCache.entrySet().stream().forEach((entry) -> {
            System.out.println(entry.getKey() + "-->" + entry.getValue());
        });
    }
}