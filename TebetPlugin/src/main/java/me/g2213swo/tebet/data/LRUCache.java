package me.g2213swo.tebet.data;

import me.g2213swo.tebetapi.model.ChatMessage;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int CAPACITY;

    private final Set<K> pinnedKeys;

    public LRUCache(int capacity) {
        super(capacity, 0.75f, true);
        this.CAPACITY = capacity;
        this.pinnedKeys = new HashSet<>();
    }

    public void pinKey(K key) {
        pinnedKeys.add(key);
    }

    public void unpinKey(K key) {
        pinnedKeys.remove(key);
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return this.size() > CAPACITY && !pinnedKeys.contains(eldest.getKey());
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