//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.geology;

import com.terraforged.engine.concurrent.Resource;
import com.terraforged.engine.concurrent.pool.ObjectPool;

public class DepthBuffer {
    private static final ObjectPool<DepthBuffer> pool = new ObjectPool(5, DepthBuffer::new);
    private float sum;
    private float[] buffer;

    public DepthBuffer() {
    }

    public void init(int size) {
        this.sum = 0.0F;
        if (this.buffer == null || this.buffer.length < size) {
            this.buffer = new float[size];
        }
    }

    public float getSum() {
        return this.sum;
    }

    public float get(int index) {
        return this.buffer[index];
    }

    public float getDepth(int index) {
        return this.buffer[index] / this.sum;
    }

    public void set(int index, float value) {
        this.sum += value;
        this.buffer[index] = value;
    }

    public static Resource<DepthBuffer> get() {
        return pool.get();
    }
}
