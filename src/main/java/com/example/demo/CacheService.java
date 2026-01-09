package com.example.demo;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CacheService {
    private final Map<String, CachedResponse> cache = new ConcurrentHashMap<>();

    public CachedResponse get(String key) {
        return cache.get(key);
    }

    public void put(String key, CachedResponse value) {
        cache.put(key, value);
    }

    public void clearAll() {
        cache.clear();
    }

    public int getSize(){
        return cache.size();
    }
}
