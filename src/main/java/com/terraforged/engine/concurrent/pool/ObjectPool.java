//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.concurrent.pool;

import com.terraforged.engine.concurrent.Resource;
import com.terraforged.engine.concurrent.cache.SafeCloseable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ObjectPool<T> {
    private final int capacity;
    private final List<ObjectPool.Item<T>> pool;
    private final Object lock = new Object();
    private final Supplier<? extends T> supplier;

    public ObjectPool(int size, Supplier<? extends T> supplier) {
        this.capacity = size;
        this.pool = new ArrayList(size);
        this.supplier = supplier;
    }

    public Resource<T> get() {
        synchronized(this.lock) {
            if (this.pool.size() > 0) {
                return ((ObjectPool.Item)this.pool.remove(this.pool.size() - 1)).retain();
            }
        }

        return new ObjectPool.Item<>(this.supplier.get(), this);
    }

    private boolean restore(ObjectPool.Item<T> item) {
        synchronized(this.lock) {
            if (this.pool.size() < this.capacity) {
                this.pool.add(item);
                return true;
            } else {
                return false;
            }
        }
    }

    public static class Item<T> implements Resource<T> {
        private final T value;
        private final ObjectPool<T> pool;
        private boolean released = false;

        private Item(T value, ObjectPool<T> pool) {
            this.value = value;
            this.pool = pool;
        }

        public T get() {
            return this.value;
        }

        public boolean isOpen() {
            return !this.released;
        }

        public void close() {
            if (this.value instanceof SafeCloseable) {
                ((SafeCloseable)this.value).close();
            }

            if (!this.released) {
                this.released = true;
                this.released = this.pool.restore(this);
            }
        }

        private ObjectPool.Item<T> retain() {
            this.released = false;
            return this;
        }
    }
}
