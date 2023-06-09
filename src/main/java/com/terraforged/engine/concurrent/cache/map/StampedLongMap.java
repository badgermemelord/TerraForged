// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.concurrent.cache.map;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

import java.util.concurrent.locks.StampedLock;
import java.util.function.Consumer;
import java.util.function.LongFunction;
import java.util.function.Predicate;

public class StampedLongMap<T> implements LongMap<T>
{
    private final StampedLock lock;
    private final Long2ObjectOpenHashMap<T> map;
    
    public StampedLongMap(final int size) {
        this.map = (Long2ObjectOpenHashMap<T>)new Long2ObjectOpenHashMap(size);
        this.lock = new StampedLock();
    }
    
    @Override
    public int size() {
        final long stamp = this.lock.readLock();
        try {
            return this.map.size();
        }
        finally {
            this.lock.unlockRead(stamp);
        }
    }
    
    @Override
    public void clear() {
        final long stamp = this.lock.writeLock();
        try {
            this.map.clear();
        }
        finally {
            this.lock.unlockWrite(stamp);
        }
    }
    
    @Override
    public void remove(final long key) {
        final long stamp = this.lock.writeLock();
        try {
            this.map.remove(key);
        }
        finally {
            this.lock.unlockWrite(stamp);
        }
    }
    
    @Override
    public void remove(final long key, final Consumer<T> consumer) {
        final long stamp = this.lock.writeLock();
        T t;
        try {
            t = (T)this.map.remove(key);
        }
        finally {
            this.lock.unlockWrite(stamp);
        }
        if (t != null) {
            consumer.accept(t);
        }
    }
    
    @Override
    public int removeIf(final Predicate<T> predicate) {
        final long stamp = this.lock.writeLock();
        try {
            final int startSize = this.map.size();
            final ObjectIterator<Long2ObjectMap.Entry<T>> iterator = (ObjectIterator<Long2ObjectMap.Entry<T>>)this.map.long2ObjectEntrySet().fastIterator();
            while (iterator.hasNext()) {
                if (predicate.test((T)((Long2ObjectMap.Entry)iterator.next()).getValue())) {
                    iterator.remove();
                }
            }
            return startSize - this.map.size();
        }
        finally {
            this.lock.unlockWrite(stamp);
        }
    }
    
    @Override
    public void put(final long key, final T t) {
        final long stamp = this.lock.writeLock();
        try {
            this.map.put(key, (Object)t);
        }
        finally {
            this.lock.unlockWrite(stamp);
        }
    }
    
    @Override
    public T get(final long key) {
        final long stamp = this.lock.readLock();
        try {
            return (T)this.map.get(key);
        }
        finally {
            this.lock.unlockRead(stamp);
        }
    }
    
    @Override
    public T computeIfAbsent(final long key, final LongFunction<T> func) {
        final long readStamp = this.lock.readLock();
        try {
            final T t = (T)this.map.get(key);
            if (t != null) {
                return t;
            }
        }
        finally {
            this.lock.unlockRead(readStamp);
        }
        final long writeStamp = this.lock.writeLock();
        try {
            return (T)this.map.computeIfAbsent(key, (LongFunction)func);
        }
        finally {
            this.lock.unlockWrite(writeStamp);
        }
    }
}
