// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.util.metric;

import com.terraforged.engine.concurrent.cache.SafeCloseable;
import com.terraforged.engine.concurrent.pool.ObjectPool;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class Metric
{
    private final AtomicLong hits;
    private final AtomicLong nanos;
    private final ObjectPool<Timer> pool;
    
    public Metric() {
        this.hits = new AtomicLong();
        this.nanos = new AtomicLong();
        this.pool = new ObjectPool<Timer>(4, () -> new Timer());
    }
    
    public long hits() {
        return this.hits.get();
    }
    
    public long nanos() {
        return this.nanos.get();
    }
    
    public String average() {
        final long hits = this.hits();
        final double milli = (double)TimeUnit.NANOSECONDS.toMillis(this.nanos());
        final double average = milli / hits;
        return String.format("Average: %.3f", average);
    }
    
    public Timer timer() {
        return this.pool.get().get().punchIn();
    }
    
    public class Timer implements SafeCloseable
    {
        private long start;
        
        public Timer() {
            this.start = -1L;
        }
        
        public Timer punchIn() {
            this.start = System.nanoTime();
            return this;
        }
        
        public Timer punchOut() {
            if (this.start > -1L) {
                final long duration = System.nanoTime() - this.start;
                Metric.this.nanos.addAndGet(duration);
                Metric.this.hits.incrementAndGet();
                this.start = -1L;
            }
            return this;
        }
        
        @Override
        public void close() {
            this.punchOut();
        }
    }
}
