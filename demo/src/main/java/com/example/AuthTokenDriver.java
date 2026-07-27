package com.example;

/**
 * Driver class to demonstrate LRU eviction in AuthTokenCache.
 * Creates a cache of capacity 3, adds 4 entries, and shows that
 * the least recently used entry (user2) gets evicted.
 */
public class AuthTokenDriver {
    public static void main(String[] args) {
        // ---- Create an LRU cache with max capacity of 3 ----
        AuthTokenCache cache = new AuthTokenCache(3);
        cache.putToken("user1", "tokA");
        cache.putToken("user2", "tokB");
        cache.putToken("user3", "tokC");

        // Access user1 so it shifts to the back (most recently used)
        cache.getToken("user1"); 

        // Add 4th item -> triggers eviction of the least recently used (user2)
        cache.putToken("user4", "tokD");

        // Print final state: should show user1, user3, user4 (user2 evicted)
        cache.printCacheState();
    }
}