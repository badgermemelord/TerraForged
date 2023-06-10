//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.concurrent.cache;

import com.terraforged.engine.concurrent.task.LazyCallable;
import com.terraforged.engine.concurrent.thread.ThreadPool;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.function.Function;

public class CacheEntry<T> extends LazyCallable<T> implements ExpiringEntry {
    private volatile long timestamp;
    private final Future<T> task;

    public CacheEntry(Future<T> task) {
        this.task = task;
        this.timestamp = System.currentTimeMillis();
    }

    public T get() {
        this.timestamp = System.currentTimeMillis();
        return (T)super.get();
    }

    public boolean isDone() {
        return this.task.isDone();
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void close() {
        if (this.value instanceof SafeCloseable) {
            ((SafeCloseable)this.value).close();
        } else {
            if (this.value instanceof AutoCloseable) {
                try {
                    ((AutoCloseable)this.value).close();
                } catch (Exception var2) {
                    var2.printStackTrace();
                }
            }
        }
    }

    protected T create() {
        if (this.task instanceof ForkJoinTask) {
            return (T)((ForkJoinTask)this.task).join();
        } else {
            try {
                return (T)this.task.get();
            } catch (Throwable var2) {
                throw new RuntimeException(var2);
            }
        }
    }

    public <V> CacheEntry<V> then(ThreadPool executor, Function<T, V> function) {
        return computeAsync(() -> function.apply(this.get()), executor);
    }

    public static <T> CacheEntry<T> supply(Future<T> task) {
        return new CacheEntry<>(task);
    }

    public static <T> CacheEntry<T> computeAsync(Callable<T> callable, ThreadPool executor) {
        return new CacheEntry<>(executor.submit(callable));
    }
}
