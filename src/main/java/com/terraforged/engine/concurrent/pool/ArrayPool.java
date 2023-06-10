//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.concurrent.pool;

import com.terraforged.engine.concurrent.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public class ArrayPool<T> {
    private final int capacity;
    private final IntFunction<T[]> constructor;
    private final List<ArrayPool.Item<T>> pool;
    private final Object lock = new Object();

    public ArrayPool(int size, IntFunction<T[]> constructor) {
        this.capacity = size;
        this.constructor = constructor;
        this.pool = new ArrayList(size);
    }

    public Resource<T[]> get(int arraySize) {
        synchronized(this.lock) {
            if (this.pool.size() > 0) {
                ArrayPool.Item<T> resource = (ArrayPool.Item)this.pool.remove(this.pool.size() - 1);
                if (resource.get().length >= arraySize) {
                    return resource.retain();
                }
            }
        }

        return new ArrayPool.Item(this.constructor.apply(arraySize), this);
    }

    private boolean restore(ArrayPool.Item<T> item) {
        synchronized(this.lock) {
            if (this.pool.size() < this.capacity) {
                this.pool.add(item);
                return true;
            } else {
                return false;
            }
        }
    }

    public static <T> ArrayPool<T> of(int size, IntFunction<T[]> constructor) {
        return new ArrayPool<>(size, constructor);
    }

    public static <T> ArrayPool<T> of(int size, Supplier<T> supplier, IntFunction<T[]> constructor) {
        return new ArrayPool<>(size, new ArrayPool.ArrayConstructor(supplier, constructor));
    }

    private static class ArrayConstructor<T> implements IntFunction<T[]> {
        private final Supplier<T> element;
        private final IntFunction<T[]> array;

        private ArrayConstructor(Supplier<T> element, IntFunction<T[]> array) {
            this.element = element;
            this.array = array;
        }

        public T[] apply(int size) {
            T[] t = (T[])((Object[])this.array.apply(size));

            for(int i = 0; i < t.length; ++i) {
                t[i] = (T)this.element.get();
            }

            return t;
        }
    }

    public static class Item<T> implements Resource<T[]> {
        private final T[] value;
        private final ArrayPool<T> pool;
        private boolean released = false;

        private Item(T[] value, ArrayPool<T> pool) {
            this.value = value;
            this.pool = pool;
        }

        public T[] get() {
            return this.value;
        }

        public boolean isOpen() {
            return !this.released;
        }

        public void close() {
            if (!this.released) {
                this.released = true;
                this.released = this.pool.restore(this);
            }
        }

        private ArrayPool.Item<T> retain() {
            this.released = false;
            return this;
        }
    }
}
