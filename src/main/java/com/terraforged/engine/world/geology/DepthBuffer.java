// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.geology;

import com.terraforged.engine.concurrent.Resource;
import com.terraforged.engine.concurrent.pool.ObjectPool;

public class DepthBuffer
{
    private static final ObjectPool<DepthBuffer> pool;
    private float sum;
    private float[] buffer;
    
    public void init(final int size) {
        this.sum = 0.0f;
        if (this.buffer == null || this.buffer.length < size) {
            this.buffer = new float[size];
        }
    }
    
    public float getSum() {
        return this.sum;
    }
    
    public float get(final int index) {
        return this.buffer[index];
    }
    
    public float getDepth(final int index) {
        return this.buffer[index] / this.sum;
    }
    
    public void set(final int index, final float value) {
        this.sum += value;
        this.buffer[index] = value;
    }
    
    public static Resource<DepthBuffer> get() {
        return DepthBuffer.pool.get();
    }
    
    static {
        pool = new ObjectPool<DepthBuffer>(5, DepthBuffer::new);
    }
}
