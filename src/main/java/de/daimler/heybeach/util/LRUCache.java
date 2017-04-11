package de.daimler.heybeach.util;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Function;

public class LRUCache<K, V> {

    private final Map<K, V> mapping = new ConcurrentHashMap<>();
    private final Deque<K> usageOrder = new ConcurrentLinkedDeque<>();
    private final int limit;
    private final Function<K, V> loadFunction;

    public LRUCache(int limit) {
        this(limit, null);
    }

    public LRUCache(int limit, Function<K, V> loadFunction) {
        this.limit = limit;
        this.loadFunction = loadFunction;
    }

    public void put(K key, V value) {
        V oldValue = mapping.put(key, value);
        if (oldValue != null) {
            moveToHead(key);
        } else {
            usageOrder.addFirst(key);
        }
        if (mapping.size() > limit) {
            mapping.remove(usageOrder.removeLast());
        }
    }

    public V get(K key) {
        moveToHead(key);
        return loadFunction != null ?
                mapping.computeIfAbsent(key, loadFunction) :
                mapping.get(key);
    }

    public V remove(K key) {
        if (mapping.containsKey(key)) {
            usageOrder.remove(key);
            return mapping.remove(key);
        } else {
            return null;
        }
    }

    public int size() {
        return mapping.size();
    }

    private void moveToHead(K key) {
        if (usageOrder.peekFirst() != key) {
            usageOrder.remove(key);
            usageOrder.addFirst(key);
        }
    }
}
