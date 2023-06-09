// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.concurrent.thread;

import com.terraforged.engine.concurrent.Resource;
import com.terraforged.engine.concurrent.batch.Batcher;
import com.terraforged.engine.concurrent.batch.SyncBatcher;
import com.terraforged.engine.concurrent.task.LazyCallable;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.function.Supplier;

public class EmptyThreadPool implements ThreadPool
{
    private final ThreadLocal<SyncBatcher> batcher;
    
    public EmptyThreadPool() {
        this.batcher = ThreadLocal.withInitial((Supplier<? extends SyncBatcher>)SyncBatcher::new);
    }
    
    @Override
    public int size() {
        return 0;
    }
    
    @Override
    public Future<?> submit(final Runnable runnable) {
        return LazyCallable.adapt(runnable);
    }
    
    @Override
    public <T> Future<T> submit(final Callable<T> callable) {
        return LazyCallable.adaptComplete(callable);
    }
    
    @Override
    public void shutdown() {
        ThreadPools.markShutdown(this);
    }
    
    @Override
    public void shutdownNow() {
    }
    
    @Override
    public Resource<Batcher> batcher() {
        return this.batcher.get();
    }
}
