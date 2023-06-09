// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.concurrent.batch;

import com.terraforged.engine.concurrent.Resource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class TaskBatcher implements Batcher, BatchTask.Notifier, Resource<Batcher>
{
    private final Executor executor;
    private volatile CountDownLatch latch;
    
    public TaskBatcher(final Executor executor) {
        this.executor = executor;
    }
    
    @Override
    public Batcher get() {
        return this;
    }
    
    @Override
    public boolean isOpen() {
        final CountDownLatch latch = this.latch;
        return latch != null && latch.getCount() > 0L;
    }
    
    @Override
    public void markDone() {
        final CountDownLatch latch = this.latch;
        latch.countDown();
    }
    
    @Override
    public void size(final int size) {
        this.latch = new CountDownLatch(size);
    }
    
    @Override
    public void submit(final Runnable task) {
    }
    
    @Override
    public void submit(final BatchTask task) {
        final CountDownLatch latch = this.latch;
        if (latch == null) {
            throw new IllegalStateException("Submitted batch task before setting the size limit!");
        }
        task.setNotifier(this);
        this.executor.execute(task);
    }
    
    @Override
    public void close() {
        final CountDownLatch latch = this.latch;
        if (latch == null) {
            throw new IllegalStateException("Closed batcher before any work was done!");
        }
        try {
            if (!latch.await(60L, TimeUnit.SECONDS)) {
                throw new BatchTimeoutException("Heightmap generation took over 60 seconds. Check logs for errors");
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
