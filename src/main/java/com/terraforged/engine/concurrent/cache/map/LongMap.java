// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.concurrent.cache.map;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.function.Predicate;

public interface LongMap<T>
{
    int size();
    
    void clear();
    
    void remove(final long p0);
    
    void remove(final long p0, final Consumer<T> p1);
    
    int removeIf(final Predicate<T> p0);
    
    void put(final long p0, final T p1);
    
    T get(final long p0);
    
    T computeIfAbsent(final long p0, final LongFunction<T> p1);
    
    default <V> V map(final long key, final LongFunction<T> factory, final Function<T, V> mapper) {
        return mapper.apply(this.computeIfAbsent(key, factory));
    }
}
