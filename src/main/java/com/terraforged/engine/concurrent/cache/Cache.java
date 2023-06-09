// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.concurrent.cache;

import com.terraforged.engine.concurrent.cache.map.LongMap;
import com.terraforged.engine.concurrent.cache.map.StampedBoundLongMap;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.LongFunction;
import java.util.function.Predicate;

public class Cache<V extends ExpiringEntry> implements Runnable, Predicate<V>
{
    private final String name;
    private final LongMap<V> map;
    private final long lifetimeMS;
    private volatile long timeout;
    
    public Cache(final String name, final long expireTime, final long interval, final TimeUnit unit) {
        this(name, 256, expireTime, interval, unit);
    }
    
    public Cache(final String name, final int capacity, final long expireTime, final long interval, final TimeUnit unit) {
        this(name, capacity, expireTime, interval, unit, (IntFunction)StampedBoundLongMap::new);
    }
    
    public Cache(final String name, final int capacity, final long expireTime, final long interval, final TimeUnit unit, final IntFunction<LongMap<V>> mapFunc) {
        this.timeout = 0L;
        this.name = name;
        this.map = mapFunc.apply(capacity);
        this.lifetimeMS = unit.toMillis(expireTime);
        CacheManager.get().schedule(this, unit.toMillis(interval));
    }
    
    public String getName() {
        return this.name;
    }
    
    public void remove(final long key) {
        this.map.remove(key, ExpiringEntry::close);
    }
    
    public V get(final long key) {
        return this.map.get(key);
    }
    
    public V computeIfAbsent(final long key, final LongFunction<V> func) {
        return this.map.computeIfAbsent(key, func);
    }
    
    public <T> T map(final long key, final LongFunction<V> func, final Function<V, T> mapper) {
        return this.map.map(key, func, mapper);
    }
    
    @Override
    public void run() {
        this.timeout = System.currentTimeMillis() - this.lifetimeMS;
        this.map.removeIf(this);
    }
    
    @Override
    public boolean test(final V v) {
        return v.getTimestamp() < this.timeout;
    }
}
