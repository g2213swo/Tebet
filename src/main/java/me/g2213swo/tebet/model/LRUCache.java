package me.g2213swo.tebet.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int CAPACITY;

    public LRUCache(int capacity) {
        super(capacity, 0.75f, true);
        this.CAPACITY = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return this.size() > CAPACITY;
    }

    public static class LRUChatSet extends LRUCache<ChatMessage, Object> {

        public static final Object VALUE = new Object();

        public LRUChatSet(int capacity) {
            super(capacity);
        }

        public void add(ChatMessage key) {
            super.put(key, VALUE);
        }
    }

}
