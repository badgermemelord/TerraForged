// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.concurrent.pool;

import com.terraforged.engine.concurrent.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public class ArrayPool<T>
{
    private final int capacity;
    private final IntFunction<T[]> constructor;
    private final List<Item<T>> pool;
    private final Object lock;
    
    public ArrayPool(final int size, final IntFunction<T[]> constructor) {
        this.lock = new Object();
        this.capacity = size;
        this.constructor = constructor;
        this.pool = new ArrayList<Item<T>>(size);
    }
    
    public Resource<T[]> get(final int arraySize) {
        synchronized (this.lock) {
            if (this.pool.size() > 0) {
                final Item<T> resource = this.pool.remove(this.pool.size() - 1);
                if (resource.get().length >= arraySize) {
                    return (Resource<T[]>)((Item<Object>)resource).retain();
                }
            }
        }
        return new Item<T>((Object[])this.constructor.apply(arraySize), this);
    }
    
    private boolean restore(final Item<T> item) {
        synchronized (this.lock) {
            if (this.pool.size() < this.capacity) {
                this.pool.add(item);
                return true;
            }
        }
        return false;
    }
    
    public static <T> ArrayPool<T> of(final int size, final IntFunction<T[]> constructor) {
        return new ArrayPool<T>(size, constructor);
    }
    
    public static <T> ArrayPool<T> of(final int size, final Supplier<T> supplier, final IntFunction<T[]> constructor) {
        return new ArrayPool<T>(size, new ArrayConstructor<T>((Supplier)supplier, (IntFunction)constructor));
    }
    
    public static class Item<T> implements Resource<T[]>
    {
        private final T[] value;
        private final ArrayPool<T> pool;
        private boolean released;
        
        private Item(final T[] value, final ArrayPool<T> pool) {
            this.released = false;
            this.value = value;
            this.pool = pool;
        }
        
        @Override
        public T[] get() {
            return this.value;
        }
        
        @Override
        public boolean isOpen() {
            return !this.released;
        }
        
        @Override
        public void close() {
            if (!this.released) {
                this.released = true;
                this.released = ((ArrayPool<Object>)this.pool).restore(this);
            }
        }
        
        private Item<T> retain() {
            this.released = false;
            return this;
        }
    }
    
    private static class ArrayConstructor<T> implements IntFunction<T[]>
    {
        private final Supplier<T> element;
        private final IntFunction<T[]> array;
        
        private ArrayConstructor(final Supplier<T> element, final IntFunction<T[]> array) {
            this.element = element;
            this.array = array;
        }
        
        @Override
        public T[] apply(final int size) {
            final T[] t = this.array.apply(size);
            for (int i = 0; i < t.length; ++i) {
                t[i] = this.element.get();
            }
            return t;
        }
    }
}
