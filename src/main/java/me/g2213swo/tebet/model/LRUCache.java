package me.g2213swo.tebet.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    /**
     * 这段代码的作用是： * 1. 设置了一个最大的容量 MAX_ENTRIES，当超过这个容量时，就会删除最不常用的元素。
     * 2. 通过构造器中的参数来设置 HashMap 的初始大小、加载因子和是否开启对访问顺序的维护，它的初始大小为 16，加载因子为 0.75，开启了对访问顺序的维护。
     * 3. 通过调用 LinkedHashMap 的构造器来构造一个 LinkedHashMap，它的初始大小为 16，加载因子为 0.75，开启了对访问顺序的维护。
     * 4. 通过调用 LinkedHashMap 的构造器来构造一个 LinkedHashMap，它的初始大小为 16，加载因子为 0.75，开启了对访问
     */
    private final int CAPACITY;

    public LRUCache(int capacity) {
        super(capacity, 0.75f, true);
        this.CAPACITY = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return this.size() > CAPACITY;
    }


    /**
     * 通俗易懂的讲，LRUChatSet内部类的作用是： * 1. 继承了 LRUCache，LRUCache 继承了 LinkedHashMap。
     * 2. 通过 LRUCache 的构造器来构造一个 LRUCache，它的初始大小为 capacity，加载因子为 0.75，开启了对访问顺序的维护。
     * 3. 通过 LRUCache 的构造器来构造一个 LRUCache，它的初始大小为 capacity，加载因子为 0.75，开启了对访问顺序的维护。
     */
    public static class LRUChatSet extends LRUCache<String, Object> {

        public static final Object VALUE = new Object();

        public LRUChatSet(int capacity) {
            super(capacity);
        }

        public void add(String key) {
            super.put(key, VALUE);
        }
    }

}
