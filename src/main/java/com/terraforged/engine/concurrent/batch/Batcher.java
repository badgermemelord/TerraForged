// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.concurrent.batch;

import com.terraforged.engine.concurrent.cache.SafeCloseable;

public interface Batcher extends SafeCloseable
{
    void size(final int p0);
    
    void submit(final Runnable p0);
    
    default void submit(final BatchTask task) {
        this.submit((Runnable)task);
    }
}
