//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.concurrent.cache.map;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Consumer;
import java.util.function.LongFunction;
import java.util.function.Predicate;

public class StampedBoundLongMap<T> implements LongMap<T> {
    private final int capacity;
    private final StampedLock lock;
    private final Long2ObjectLinkedOpenHashMap<T> map;

    public StampedBoundLongMap(int size) {
        this.capacity = size;
        this.lock = new StampedLock();
        this.map = new Long2ObjectLinkedOpenHashMap(size);
    }

    public int size() {
        long stamp = this.lock.readLock();

        int var3;
        try {
            var3 = this.map.size();
        } finally {
            this.lock.unlockRead(stamp);
        }

        return var3;
    }

    public void clear() {
        long stamp = this.lock.writeLock();

        try {
            this.map.clear();
        } finally {
            this.lock.unlockWrite(stamp);
        }
    }

    public void remove(long key) {
        long stamp = this.lock.writeLock();

        try {
            this.map.remove(key);
        } finally {
            this.lock.unlockWrite(stamp);
        }
    }

    public void remove(long key, Consumer<T> consumer) {
        long stamp = this.lock.writeLock();

        T t;
        try {
            t = (T)this.map.remove(key);
        } finally {
            this.lock.unlockWrite(stamp);
        }

        if (t != null) {
            consumer.accept(t);
        }
    }

    public int removeIf(Predicate<T> predicate) {
        long stamp = this.lock.writeLock();

        int var10;
        try {
            int startSize = this.map.size();
            ObjectIterator<Entry<T>> iterator = this.map.long2ObjectEntrySet().fastIterator();

            while(iterator.hasNext()) {
                Entry<T> entry = (Entry)iterator.next();
                if (predicate.test(entry.getValue())) {
                    iterator.remove();
                }
            }

            var10 = startSize - this.map.size();
        } finally {
            this.lock.unlockWrite(stamp);
        }

        return var10;
    }

    public void put(long key, T t) {
        long stamp = this.lock.writeLock();

        try {
            this.map.put(key, t);
        } finally {
            this.lock.unlockWrite(stamp);
        }
    }

    public T get(long key) {
        long stamp = this.lock.readLock();

        Object var5;
        try {
            var5 = this.map.get(key);
        } finally {
            this.lock.unlockRead(stamp);
        }

        return (T)var5;
    }

    public T computeIfAbsent(long key, LongFunction<T> func) {
        long readStamp = this.lock.readLock();

        try {
            T t = (T)this.map.get(key);
            if (t != null) {
                return t;
            }
        } finally {
            this.lock.unlockRead(readStamp);
        }

        long var16 = this.lock.writeLock();

        Object var8;
        try {
            if (this.map.size() >= this.capacity) {
                this.map.removeFirst();
            }

            var8 = this.map.computeIfAbsent(key, func);
        } finally {
            this.lock.unlockWrite(var16);
        }

        return (T)var8;
    }
}
