// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.concurrent.cache;

import com.terraforged.engine.concurrent.thread.ThreadPools;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

public class CacheManager
{
    private static final CacheManager INSTANCE;
    private final List<ScheduledFuture<?>> cacheTasks;
    
    private CacheManager() {
        this.cacheTasks = new ArrayList<ScheduledFuture<?>>();
    }
    
    public synchronized void schedule(final Cache<?> cache, final long intervalMS) {
        this.cacheTasks.add(ThreadPools.scheduleRepeat(cache, intervalMS));
    }
    
    public synchronized void clear() {
        if (this.cacheTasks.isEmpty()) {
            return;
        }
        for (final ScheduledFuture<?> task : this.cacheTasks) {
            try {
                task.cancel(false);
            }
            catch (Throwable t) {
                t.printStackTrace();
            }
        }
        this.cacheTasks.clear();
    }
    
    public static CacheManager get() {
        return CacheManager.INSTANCE;
    }
    
    static {
        INSTANCE = new CacheManager();
    }
}
