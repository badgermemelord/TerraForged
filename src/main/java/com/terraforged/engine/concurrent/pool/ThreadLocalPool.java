// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.concurrent.pool;

import com.terraforged.engine.concurrent.Resource;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ThreadLocalPool<T>
{
    private final int size;
    private final Supplier<T> factory;
    private final Consumer<T> cleaner;
    private final ThreadLocal<Pool<T>> local;
    
    public ThreadLocalPool(final int size, final Supplier<T> factory) {
        this(size, (Supplier<Object>)factory, t -> {});
    }
    
    public ThreadLocalPool(final int size, final Supplier<T> factory, final Consumer<T> cleaner) {
        this.size = size;
        this.factory = factory;
        this.cleaner = cleaner;
        this.local = ThreadLocal.withInitial((Supplier<? extends Pool<T>>)this::createPool);
    }
    
    public Resource<T> get() {
        return (Resource<T>)((Pool<Object>)this.local.get()).retain();
    }
    
    private Pool<T> createPool() {
        return new Pool<T>(this.size, (Supplier)this.factory, (Consumer)this.cleaner);
    }
    
    private static class Pool<T>
    {
        private final int size;
        private final Supplier<T> factory;
        private final Consumer<T> cleaner;
        private final List<Resource<T>> pool;
        private int index;
        
        private Pool(final int size, final Supplier<T> factory, final Consumer<T> cleaner) {
            this.size = size;
            this.index = size - 1;
            this.factory = factory;
            this.cleaner = cleaner;
            this.pool = (List<Resource<T>>)new ObjectArrayList(size);
            for (int i = 0; i < size; ++i) {
                this.pool.add(new PoolResource<T>((Object)factory.get(), this));
            }
        }
        
        private Resource<T> retain() {
            if (this.index > 0) {
                final Resource<T> value = this.pool.remove(this.index);
                --this.index;
                return value;
            }
            return new PoolResource<T>((Object)this.factory.get(), this);
        }
        
        private void restore(final Resource<T> resource) {
            if (this.index + 1 < this.size) {
                this.cleaner.accept(resource.get());
                this.pool.add(resource);
                ++this.index;
            }
        }
    }
    
    private static class PoolResource<T> implements Resource<T>
    {
        private final T value;
        private final Pool<T> pool;
        
        private PoolResource(final T value, final Pool<T> pool) {
            this.value = value;
            this.pool = pool;
        }
        
        @Override
        public T get() {
            return this.value;
        }
        
        @Override
        public boolean isOpen() {
            return true;
        }
        
        @Override
        public void close() {
            ((Pool<Object>)this.pool).restore(this);
        }
    }
}
