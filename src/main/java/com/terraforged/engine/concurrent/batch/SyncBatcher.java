// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.concurrent.batch;

import com.terraforged.engine.concurrent.Resource;

public class SyncBatcher implements Batcher, Resource<Batcher>
{
    @Override
    public void size(final int size) {
    }
    
    @Override
    public void submit(final Runnable task) {
        task.run();
    }
    
    @Override
    public Batcher get() {
        return this;
    }
    
    @Override
    public boolean isOpen() {
        return false;
    }
    
    @Override
    public void close() {
    }
}
