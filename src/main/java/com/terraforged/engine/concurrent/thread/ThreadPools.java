// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.concurrent.thread;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ThreadPools
{
    public static final ThreadPool NONE;
    private static final Object lock;
    private static final ScheduledExecutorService scheduler;
    private static WeakReference<ThreadPool> instance;
    
    public static ThreadPool createDefault() {
        return create(defaultPoolSize());
    }
    
    public static ThreadPool create(final int poolSize) {
        return create(poolSize, false);
    }
    
    public static ThreadPool create(final int poolSize, final boolean keepAlive) {
        synchronized (ThreadPools.lock) {
            final ThreadPool current = ThreadPools.instance.get();
            if (current != null && current.isManaged()) {
                if (poolSize == current.size()) {
                    return current;
                }
                current.shutdown();
            }
            final ThreadPool next = BatchingThreadPool.of(poolSize, !keepAlive);
            if (next.isManaged()) {
                ThreadPools.instance = new WeakReference<ThreadPool>(next);
            }
            return next;
        }
    }
    
    public static int defaultPoolSize() {
        return Math.max(2, Runtime.getRuntime().availableProcessors());
    }
    
    public static void scheduleDelayed(final Runnable runnable, final long delayMS) {
        ThreadPools.scheduler.schedule(runnable, delayMS, TimeUnit.MILLISECONDS);
    }
    
    public static ScheduledFuture<?> scheduleRepeat(final Runnable runnable, final long intervalMS) {
        return ThreadPools.scheduler.scheduleAtFixedRate(runnable, intervalMS, intervalMS, TimeUnit.MILLISECONDS);
    }
    
    public static void markShutdown(final ThreadPool threadPool) {
        synchronized (ThreadPools.lock) {
            if (threadPool == ThreadPools.instance.get()) {
                ThreadPools.instance.clear();
            }
        }
    }
    
    public static void shutdownAll() {
        ThreadPools.scheduler.shutdownNow();
        synchronized (ThreadPools.lock) {
            final ThreadPool pool = ThreadPools.instance.get();
            if (pool != null) {
                pool.shutdown();
                ThreadPools.instance.clear();
            }
        }
    }
    
    static {
        NONE = new EmptyThreadPool();
        lock = new Object();
        scheduler = Executors.newSingleThreadScheduledExecutor(new SimpleThreadFactory("TF-Scheduler"));
        ThreadPools.instance = new WeakReference<ThreadPool>(null);
    }
}
