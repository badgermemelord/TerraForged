// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.concurrent.thread;

import com.terraforged.engine.concurrent.Resource;
import com.terraforged.engine.concurrent.batch.Batcher;
import com.terraforged.engine.concurrent.batch.TaskBatcher;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BatchingThreadPool implements ThreadPool
{
    private final int size;
    private final boolean managed;
    private final ExecutorService taskExecutor;
    private final ExecutorService batchExecutor;
    
    private BatchingThreadPool(final int taskSize, final int batchSize, final boolean managed) {
        this.managed = managed;
        this.size = taskSize + batchSize;
        this.taskExecutor = Executors.newFixedThreadPool(taskSize, new WorkerFactory("TF-Task"));
        this.batchExecutor = Executors.newFixedThreadPool(batchSize, new WorkerFactory("TF-Batch"));
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    @Override
    public Future<?> submit(final Runnable runnable) {
        return this.taskExecutor.submit(runnable);
    }
    
    @Override
    public <T> Future<T> submit(final Callable<T> callable) {
        return this.taskExecutor.submit(callable);
    }
    
    @Override
    public boolean isManaged() {
        return this.managed;
    }
    
    @Override
    public void shutdown() {
        this.taskExecutor.shutdown();
        this.batchExecutor.shutdown();
        ThreadPools.markShutdown(this);
    }
    
    @Override
    public void shutdownNow() {
        this.taskExecutor.shutdownNow();
        this.batchExecutor.shutdownNow();
    }
    
    @Override
    public Resource<Batcher> batcher() {
        return new TaskBatcher(this.batchExecutor);
    }
    
    public static ThreadPool of(final int size, final boolean keepalive) {
        final int tasks = Math.max(1, size / 2);
        final int batches = Math.max(2, size);
        return new BatchingThreadPool(tasks, batches, keepalive);
    }
}
