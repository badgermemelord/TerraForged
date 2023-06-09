// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.concurrent.cache.map;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

import java.util.function.Consumer;
import java.util.function.LongFunction;
import java.util.function.Predicate;

public class SynchronizedLongMap<T> implements LongMap<T>
{
    private final Object lock;
    private final Long2ObjectOpenHashMap<T> map;
    
    public SynchronizedLongMap(final int size) {
        this.map = (Long2ObjectOpenHashMap<T>)new Long2ObjectOpenHashMap(size);
        this.lock = this;
    }
    
    @Override
    public int size() {
        synchronized (this.lock) {
            return this.map.size();
        }
    }
    
    @Override
    public void clear() {
        synchronized (this.lock) {
            this.map.clear();
        }
    }
    
    @Override
    public void remove(final long key) {
        synchronized (this.lock) {
            this.map.remove(key);
        }
    }
    
    @Override
    public void remove(final long key, final Consumer<T> consumer) {
        synchronized (this.lock) {
            final T t = (T)this.map.remove(key);
            if (t != null) {
                consumer.accept(t);
            }
        }
    }
    
    @Override
    public int removeIf(final Predicate<T> predicate) {
        synchronized (this.lock) {
            final int startSize = this.map.size();
            final ObjectIterator<Long2ObjectMap.Entry<T>> iterator = (ObjectIterator<Long2ObjectMap.Entry<T>>)this.map.long2ObjectEntrySet().fastIterator();
            while (iterator.hasNext()) {
                if (predicate.test((T)((Long2ObjectMap.Entry)iterator.next()).getValue())) {
                    iterator.remove();
                }
            }
            return startSize - this.map.size();
        }
    }
    
    @Override
    public void put(final long key, final T t) {
        synchronized (this.lock) {
            this.map.put(key, (Object)t);
        }
    }
    
    @Override
    public T get(final long key) {
        synchronized (this.lock) {
            return (T)this.map.get(key);
        }
    }
    
    @Override
    public T computeIfAbsent(final long key, final LongFunction<T> func) {
        synchronized (this.lock) {
            return (T)this.map.computeIfAbsent(key, (LongFunction)func);
        }
    }
}
