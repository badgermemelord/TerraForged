// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.geology;

import com.terraforged.noise.Source;

public class Stratum<T>
{
    private final T value;
    private final Module depth;
    
    public Stratum(final T value, final double depth) {
        this(value, Source.constant(depth));
    }
    
    public Stratum(final T value, final Module depth) {
        this.depth = depth;
        this.value = value;
    }
    
    public T getValue() {
        return this.value;
    }
    
    public float getDepth(final float x, final float z) {
        return this.depth.getValue(x, z);
    }
    
    public static <T> Stratum<T> of(final T t, final double depth) {
        return new Stratum<T>(t, depth);
    }
    
    public static <T> Stratum<T> of(final T t, final Module depth) {
        return new Stratum<T>(t, depth);
    }
    
    public interface Visitor<T, Context>
    {
        boolean visit(final int p0, final T p1, final Context p2);
    }
}
