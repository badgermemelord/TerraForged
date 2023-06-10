//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.concurrent.pool;

import com.terraforged.engine.concurrent.Resource;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ThreadLocalPool<T> {
    private final int size;
    private final Supplier<T> factory;
    private final Consumer<T> cleaner;
    private final ThreadLocal<ThreadLocalPool.Pool<T>> local;

    public ThreadLocalPool(int size, Supplier<T> factory) {
        this(size, factory, t -> {
        });
    }

    public ThreadLocalPool(int size, Supplier<T> factory, Consumer<T> cleaner) {
        this.size = size;
        this.factory = factory;
        this.cleaner = cleaner;
        this.local = ThreadLocal.withInitial(this::createPool);
    }

    public Resource<T> get() {
        return ((ThreadLocalPool.Pool)this.local.get()).retain();
    }

    private ThreadLocalPool.Pool<T> createPool() {
        return new ThreadLocalPool.Pool<>(this.size, this.factory, this.cleaner);
    }

    private static class Pool<T> {
        private final int size;
        private final Supplier<T> factory;
        private final Consumer<T> cleaner;
        private final List<Resource<T>> pool;
        private int index;

        private Pool(int size, Supplier<T> factory, Consumer<T> cleaner) {
            this.size = size;
            this.index = size - 1;
            this.factory = factory;
            this.cleaner = cleaner;
            this.pool = new ObjectArrayList(size);

            for(int i = 0; i < size; ++i) {
                this.pool.add(new ThreadLocalPool.PoolResource(factory.get(), this));
            }
        }

        private Resource<T> retain() {
            if (this.index > 0) {
                Resource<T> value = (Resource)this.pool.remove(this.index);
                --this.index;
                return value;
            } else {
                return new ThreadLocalPool.PoolResource<>(this.factory.get(), this);
            }
        }

        private void restore(Resource<T> resource) {
            if (this.index + 1 < this.size) {
                this.cleaner.accept(resource.get());
                this.pool.add(resource);
                ++this.index;
            }
        }
    }

    private static class PoolResource<T> implements Resource<T> {
        private final T value;
        private final ThreadLocalPool.Pool<T> pool;

        private PoolResource(T value, ThreadLocalPool.Pool<T> pool) {
            this.value = value;
            this.pool = pool;
        }

        public T get() {
            return this.value;
        }

        public boolean isOpen() {
            return true;
        }

        public void close() {
            this.pool.restore(this);
        }
    }
}
