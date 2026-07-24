package com.example;

public class AuthTokenDriver {
    public static void main(String[] args) {
        AuthTokenCache cache = new AuthTokenCache(3);
        cache.putToken("user1", "tokA");
        cache.putToken("user2", "tokB");
        cache.putToken("user3", "tokC");

        // Access user1 so it shifts to the back (most recently used)
        cache.getToken("user1"); 

        // Add 4th item -> triggers eviction of the least recently used (user2)
        cache.putToken("user4", "tokD");

        cache.printCacheState();
    }
    
}
