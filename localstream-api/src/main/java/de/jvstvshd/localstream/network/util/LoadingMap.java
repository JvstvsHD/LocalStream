package de.jvstvshd.localstream.network.util;

import com.google.common.collect.ForwardingMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class LoadingMap<K, V> extends ForwardingMap<K, V> implements Map<K, V> {
    private final Map<K, V> map;

    private final Function<K, V> function;

    public static <K, V> LoadingMap<K, V> of(Map<K, V> map, Function<K, V> function) {
        return new LoadingMap<>(map, function);
    }

    public static <K, V> LoadingMap<K, V> of(Function<K, V> function) {
        return of(new ConcurrentHashMap<>(), function);
    }

    private LoadingMap(Map<K, V> map, Function<K, V> function) {
        this.map = map;
        this.function = function;
    }

    protected Map<K, V> delegate() {
        return this.map;
    }

    public V getIfPresent(K key) {
        return this.map.get(key);
    }

    public V get(Object key) {
        V value = this.map.get(key);
        if (value != null)
            return value;
        return this.map.computeIfAbsent((K)key, this.function);
    }
}