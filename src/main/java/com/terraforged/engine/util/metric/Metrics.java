// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.util.metric;

import java.util.concurrent.atomic.AtomicLong;

public class Metrics
{
    public static final Metric BATCHER;
    public static final Metric HEIGHTMAP;
    public static final Metric RIVER_GEN;
    private static final AtomicLong timer;
    
    public static void print() {
        final long now = System.currentTimeMillis();
        if (now - Metrics.timer.get() > 5000L) {
            Metrics.timer.set(now);
            System.out.println("Heightmap: " + Metrics.HEIGHTMAP.average());
            System.out.println("River Gen: " + Metrics.RIVER_GEN.average());
            System.out.println("Batching:  " + Metrics.BATCHER.average());
        }
    }
    
    static {
        BATCHER = new Metric();
        HEIGHTMAP = new Metric();
        RIVER_GEN = new Metric();
        timer = new AtomicLong(System.currentTimeMillis());
    }
}
