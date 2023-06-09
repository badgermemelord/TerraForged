// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.concurrent.thread;

import java.util.concurrent.ThreadFactory;

public class SimpleThreadFactory implements ThreadFactory
{
    private final String name;
    
    public SimpleThreadFactory(final String name) {
        this.name = name;
    }
    
    @Override
    public Thread newThread(final Runnable r) {
        final Thread thread = new Thread(r);
        thread.setName(this.name);
        return thread;
    }
}
