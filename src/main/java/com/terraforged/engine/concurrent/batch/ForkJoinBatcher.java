// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.concurrent.batch;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

public class ForkJoinBatcher implements Batcher
{
    private static final ForkJoinTask<?>[] empty;
    private final ForkJoinPool pool;
    private int size;
    private int count;
    private ForkJoinTask<?>[] tasks;
    
    public ForkJoinBatcher(final ForkJoinPool pool) {
        this.size = 0;
        this.count = 0;
        this.tasks = ForkJoinBatcher.empty;
        this.pool = pool;
    }
    
    @Override
    public void size(final int newSize) {
        if (this.tasks.length < newSize) {
            this.count = 0;
            this.size = newSize;
            this.tasks = (ForkJoinTask<?>[])new ForkJoinTask[newSize];
        }
    }
    
    @Override
    public void submit(final Runnable task) {
        if (this.count < this.size) {
            this.tasks[this.count++] = this.pool.submit(task);
        }
    }
    
    @Override
    public void close() {
        for (int i = 0; i < this.size; ++i) {
            this.tasks[i].quietlyJoin();
            this.tasks[i] = null;
        }
    }
    
    static {
        empty = new ForkJoinTask[0];
    }
}
