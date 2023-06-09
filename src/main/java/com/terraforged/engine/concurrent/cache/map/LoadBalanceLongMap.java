// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.concurrent.cache.map;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

import java.util.concurrent.locks.StampedLock;
import java.util.function.Consumer;
import java.util.function.LongFunction;
import java.util.function.Predicate;

public class LoadBalanceLongMap<T> implements LongMap<T>
{
    private final int mask;
    private final int sectionCapacity;
    private final Long2ObjectLinkedOpenHashMap<T>[] maps;
    private final StampedLock[] locks;
    
    public LoadBalanceLongMap(int factor, int size) {
        factor = getNearestFactor(factor);
        size = getSectionSize(size, factor);
        this.mask = factor - 1;
        this.sectionCapacity = size - 2;
        this.maps = (Long2ObjectLinkedOpenHashMap<T>[])new Long2ObjectLinkedOpenHashMap[factor];
        this.locks = new StampedLock[factor];
        for (int i = 0; i < factor; ++i) {
            this.maps[i] = (Long2ObjectLinkedOpenHashMap<T>)new Long2ObjectLinkedOpenHashMap(size);
            this.locks[i] = new StampedLock();
        }
    }
    
    @Override
    public int size() {
        int size = 0;
        for (int i = 0; i < this.locks.length; ++i) {
            final StampedLock lock = this.locks[i];
            final long stamp = lock.readLock();
            try {
                size += this.maps[i].size();
            }
            finally {
                lock.unlockRead(stamp);
            }
        }
        return size;
    }
    
    @Override
    public void clear() {
        for (int i = 0; i < this.locks.length; ++i) {
            final StampedLock lock = this.locks[i];
            final long stamp = lock.writeLock();
            try {
                this.maps[i].clear();
            }
            finally {
                lock.unlockWrite(stamp);
            }
        }
    }
    
    @Override
    public void remove(final long key) {
        final int index = this.getIndex(key);
        final StampedLock lock = this.locks[index];
        final long stamp = lock.writeLock();
        try {
            this.maps[index].remove(key);
        }
        finally {
            lock.unlockWrite(stamp);
        }
    }
    
    @Override
    public void remove(final long key, final Consumer<T> consumer) {
        final int index = this.getIndex(key);
        final StampedLock lock = this.locks[index];
        final long stamp = lock.writeLock();
        try {
            this.maps[index].remove(key, (Object)consumer);
        }
        finally {
            lock.unlockWrite(stamp);
        }
    }
    
    @Override
    public int removeIf(final Predicate<T> predicate) {
        int count = 0;
        for (int i = 0; i < this.locks.length; ++i) {
            final StampedLock lock = this.locks[i];
            final Long2ObjectLinkedOpenHashMap<T> map = this.maps[i];
            final long stamp = lock.writeLock();
            try {
                final int startSize = map.size();
                final ObjectIterator<Long2ObjectMap.Entry<T>> iterator = (ObjectIterator<Long2ObjectMap.Entry<T>>)map.long2ObjectEntrySet().fastIterator();
                while (iterator.hasNext()) {
                    final Long2ObjectMap.Entry<T> entry = (Long2ObjectMap.Entry<T>)iterator.next();
                    if (predicate.test((T)entry.getValue())) {
                        iterator.remove();
                    }
                }
                count += startSize - map.size();
            }
            finally {
                lock.unlockWrite(stamp);
            }
        }
        return count;
    }
    
    @Override
    public void put(final long key, final T value) {
        final int index = this.getIndex(key);
        final StampedLock lock = this.locks[index];
        final Long2ObjectLinkedOpenHashMap<T> map = this.maps[index];
        final long stamp = lock.writeLock();
        try {
            if (map.size() > this.sectionCapacity) {
                map.removeFirst();
            }
            map.put(key, (Object)value);
        }
        finally {
            lock.unlockWrite(stamp);
        }
    }
    
    @Override
    public T get(final long key) {
        final int index = this.getIndex(key);
        final StampedLock lock = this.locks[index];
        final long stamp = lock.readLock();
        try {
            return (T)this.maps[index].get(key);
        }
        finally {
            lock.unlockRead(stamp);
        }
    }
    
    @Override
    public T computeIfAbsent(final long key, final LongFunction<T> factory) {
        final int index = this.getIndex(key);
        final StampedLock lock = this.locks[index];
        final Long2ObjectLinkedOpenHashMap<T> map = this.maps[index];
        final long readStamp = lock.readLock();
        try {
            final T t = (T)map.get(key);
            if (t != null) {
                return t;
            }
        }
        finally {
            lock.unlockRead(readStamp);
        }
        final long writeStamp = lock.writeLock();
        try {
            if (map.size() > this.sectionCapacity) {
                map.removeFirst();
            }
            return (T)map.computeIfAbsent(key, (LongFunction)factory);
        }
        finally {
            lock.unlockWrite(writeStamp);
        }
    }
    
    private int getIndex(final long key) {
        return HashCommon.long2int(key) & this.mask;
    }
    
    private static int getSectionSize(final int size, final int factor) {
        int section = size / factor;
        if (section * factor < size) {
            ++section;
        }
        return section;
    }
    
    private static int getNearestFactor(int i) {
        int j;
        for (j = 0; i != 0; i >>= 1, ++j) {}
        return j;
    }
}
