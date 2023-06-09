// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.concurrent.pool;

import com.terraforged.engine.concurrent.Resource;
import com.terraforged.engine.concurrent.cache.SafeCloseable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ObjectPool<T>
{
    private final int capacity;
    private final List<Item<T>> pool;
    private final Object lock;
    private final Supplier<? extends T> supplier;
    
    public ObjectPool(final int size, final Supplier<? extends T> supplier) {
        this.lock = new Object();
        this.capacity = size;
        this.pool = new ArrayList<Item<T>>(size);
        this.supplier = supplier;
    }
    
    public Resource<T> get() {
        synchronized (this.lock) {
            if (this.pool.size() > 0) {
                return (Resource<T>)((Item<Object>)this.pool.remove(this.pool.size() - 1)).retain();
            }
        }
        return new Item<T>((Object)this.supplier.get(), this);
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
    
    public static class Item<T> implements Resource<T>
    {
        private final T value;
        private final ObjectPool<T> pool;
        private boolean released;
        
        private Item(final T value, final ObjectPool<T> pool) {
            this.released = false;
            this.value = value;
            this.pool = pool;
        }
        
        @Override
        public T get() {
            return this.value;
        }
        
        @Override
        public boolean isOpen() {
            return !this.released;
        }
        
        @Override
        public void close() {
            if (this.value instanceof SafeCloseable) {
                ((SafeCloseable)this.value).close();
            }
            if (!this.released) {
                this.released = true;
                this.released = ((ObjectPool<Object>)this.pool).restore(this);
            }
        }
        
        private Item<T> retain() {
            this.released = false;
            return this;
        }
    }
}
