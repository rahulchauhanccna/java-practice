package com.example;

import java.util.LinkedHashMap;
import java.util.Map;

public class AuthTokenCache {

   
    Map<String, String> imageCache;

    public AuthTokenCache(int maxCapacity) {

       imageCache = new LinkedHashMap<>(maxCapacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                return size() > maxCapacity; // Drops oldest accessed image when size exceeds maxCapacity;
            }
        };
    }

    public String getToken(String userId) {
   
        return imageCache.getOrDefault(userId, null);
    }

    public void putToken(String userId, String token) {
        imageCache.put(userId, token) ;

    }

    public void printCacheState() {
        imageCache.entrySet().stream().forEach((entry)-> {
            System.out.println(entry.getKey()+"-->"+entry.getValue());
        });
    }
}