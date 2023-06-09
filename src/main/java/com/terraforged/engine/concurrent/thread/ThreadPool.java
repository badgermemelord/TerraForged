// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.concurrent.thread;

import com.terraforged.engine.concurrent.Resource;
import com.terraforged.engine.concurrent.batch.Batcher;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface ThreadPool
{
    int size();
    
    void shutdown();
    
    void shutdownNow();
    
    default boolean isManaged() {
        return false;
    }
    
    Future<?> submit(final Runnable p0);
    
     <T> Future<T> submit(final Callable<T> p0);
    
    Resource<Batcher> batcher();
}
