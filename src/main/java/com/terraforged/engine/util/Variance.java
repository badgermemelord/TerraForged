// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.util;

import java.util.Random;

public class Variance
{
    private final float min;
    private final float range;
    
    private Variance(final float min, final float range) {
        this.min = min;
        this.range = range;
    }
    
    public float apply(final float value) {
        return this.min + value * this.range;
    }
    
    public float apply(final float value, final float scaler) {
        return this.apply(value) * scaler;
    }
    
    public float next(final FastRandom random) {
        return this.apply(random.nextFloat());
    }
    
    public float next(final Random random) {
        return this.apply(random.nextFloat());
    }
    
    public float next(final FastRandom random, final float scalar) {
        return this.apply(random.nextFloat(), scalar);
    }
    
    public float next(final Random random, final float scalar) {
        return this.apply(random.nextFloat(), scalar);
    }
    
    public static Variance min(final double min) {
        return new Variance((float)min, 1.0f - (float)min);
    }
    
    public static Variance range(final double range) {
        return new Variance(1.0f - (float)range, (float)range);
    }
    
    public static Variance of(final double min, final double range) {
        return new Variance((float)min, (float)range);
    }
}
