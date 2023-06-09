// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.concurrent.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class WorkerFactory implements ThreadFactory
{
    protected final String prefix;
    protected final ThreadGroup group;
    protected final AtomicInteger threadNumber;
    
    public WorkerFactory(final String name) {
        this.threadNumber = new AtomicInteger(1);
        this.group = Thread.currentThread().getThreadGroup();
        this.prefix = name + "-Worker-";
    }
    
    @Override
    public Thread newThread(final Runnable task) {
        final Thread thread = new Thread(this.group, task);
        thread.setDaemon(true);
        thread.setName(this.prefix + this.threadNumber.getAndIncrement());
        return thread;
    }
}
