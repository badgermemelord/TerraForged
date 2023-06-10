//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.concurrent.cache.map;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Consumer;
import java.util.function.LongFunction;
import java.util.function.Predicate;

public class LoadBalanceLongMap<T> implements LongMap<T> {
    private final int mask;
    private final int sectionCapacity;
    private final Long2ObjectLinkedOpenHashMap<T>[] maps;
    private final StampedLock[] locks;

    public LoadBalanceLongMap(int factor, int size) {
        factor = getNearestFactor(factor);
        size = getSectionSize(size, factor);
        this.mask = factor - 1;
        this.sectionCapacity = size - 2;
        this.maps = new Long2ObjectLinkedOpenHashMap[factor];
        this.locks = new StampedLock[factor];

        for(int i = 0; i < factor; ++i) {
            this.maps[i] = new Long2ObjectLinkedOpenHashMap(size);
            this.locks[i] = new StampedLock();
        }
    }

    public int size() {
        int size = 0;

        for(int i = 0; i < this.locks.length; ++i) {
            StampedLock lock = this.locks[i];
            long stamp = lock.readLock();

            try {
                size += this.maps[i].size();
            } finally {
                lock.unlockRead(stamp);
            }
        }

        return size;
    }

    public void clear() {
        for(int i = 0; i < this.locks.length; ++i) {
            StampedLock lock = this.locks[i];
            long stamp = lock.writeLock();

            try {
                this.maps[i].clear();
            } finally {
                lock.unlockWrite(stamp);
            }
        }
    }

    public void remove(long key) {
        int index = this.getIndex(key);
        StampedLock lock = this.locks[index];
        long stamp = lock.writeLock();

        try {
            this.maps[index].remove(key);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public void remove(long key, Consumer<T> consumer) {
        int index = this.getIndex(key);
        StampedLock lock = this.locks[index];
        long stamp = lock.writeLock();

        try {
            this.maps[index].remove(key, consumer);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public int removeIf(Predicate<T> predicate) {
        int count = 0;

        for(int i = 0; i < this.locks.length; ++i) {
            StampedLock lock = this.locks[i];
            Long2ObjectLinkedOpenHashMap<T> map = this.maps[i];
            long stamp = lock.writeLock();

            try {
                int startSize = map.size();
                ObjectIterator<Entry<T>> iterator = map.long2ObjectEntrySet().fastIterator();

                while(iterator.hasNext()) {
                    Entry<T> entry = (Entry)iterator.next();
                    if (predicate.test(entry.getValue())) {
                        iterator.remove();
                    }
                }

                count += startSize - map.size();
            } finally {
                lock.unlockWrite(stamp);
            }
        }

        return count;
    }

    public void put(long key, T value) {
        int index = this.getIndex(key);
        StampedLock lock = this.locks[index];
        Long2ObjectLinkedOpenHashMap<T> map = this.maps[index];
        long stamp = lock.writeLock();

        try {
            if (map.size() > this.sectionCapacity) {
                map.removeFirst();
            }

            map.put(key, value);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public T get(long key) {
        int index = this.getIndex(key);
        StampedLock lock = this.locks[index];
        long stamp = lock.readLock();

        Object var7;
        try {
            var7 = this.maps[index].get(key);
        } finally {
            lock.unlockRead(stamp);
        }

        return (T)var7;
    }

    public T computeIfAbsent(long key, LongFunction<T> factory) {
        int index = this.getIndex(key);
        StampedLock lock = this.locks[index];
        Long2ObjectLinkedOpenHashMap<T> map = this.maps[index];
        long readStamp = lock.readLock();

        try {
            T t = (T)map.get(key);
            if (t != null) {
                return t;
            }
        } finally {
            lock.unlockRead(readStamp);
        }

        long var19 = lock.writeLock();

        Object var11;
        try {
            if (map.size() > this.sectionCapacity) {
                map.removeFirst();
            }

            var11 = map.computeIfAbsent(key, factory);
        } finally {
            lock.unlockWrite(var19);
        }

        return (T)var11;
    }

    private int getIndex(long key) {
        return HashCommon.long2int(key) & this.mask;
    }

    private static int getSectionSize(int size, int factor) {
        int section = size / factor;
        if (section * factor < size) {
            ++section;
        }

        return section;
    }

    private static int getNearestFactor(int i) {
        int j;
        for(j = 0; i != 0; ++j) {
            i >>= 1;
        }

        return j;
    }
}
