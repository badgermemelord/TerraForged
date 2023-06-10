//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.concurrent.cache.map;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.function.Consumer;
import java.util.function.LongFunction;
import java.util.function.Predicate;

public class SynchronizedLongMap<T> implements LongMap<T> {
    private final Object lock;
    private final Long2ObjectOpenHashMap<T> map;

    public SynchronizedLongMap(int size) {
        this.map = new Long2ObjectOpenHashMap(size);
        this.lock = this;
    }

    public int size() {
        synchronized(this.lock) {
            return this.map.size();
        }
    }

    public void clear() {
        synchronized(this.lock) {
            this.map.clear();
        }
    }

    public void remove(long key) {
        synchronized(this.lock) {
            this.map.remove(key);
        }
    }

    public void remove(long key, Consumer<T> consumer) {
        synchronized(this.lock) {
            T t = (T)this.map.remove(key);
            if (t != null) {
                consumer.accept(t);
            }
        }
    }

    public int removeIf(Predicate<T> predicate) {
        synchronized(this.lock) {
            int startSize = this.map.size();
            ObjectIterator<Entry<T>> iterator = this.map.long2ObjectEntrySet().fastIterator();

            while(iterator.hasNext()) {
                if (predicate.test((iterator.next()).getValue())) {
                    iterator.remove();
                }
            }

            return startSize - this.map.size();
        }
    }

    public void put(long key, T t) {
        synchronized(this.lock) {
            this.map.put(key, t);
        }
    }

    public T get(long key) {
        synchronized(this.lock) {
            return (T)this.map.get(key);
        }
    }

    public T computeIfAbsent(long key, LongFunction<T> func) {
        synchronized(this.lock) {
            return (T)this.map.computeIfAbsent(key, func);
        }
    }
}
