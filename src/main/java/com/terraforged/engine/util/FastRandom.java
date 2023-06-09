// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.util;

public class FastRandom
{
    private static final long GOLDEN_GAMMA = -7046029254386353131L;
    private static final float FLOAT_MULTIPLIER = 5.9604645E-8f;
    private long seed;
    private long gamma;
    
    public FastRandom() {
        this(System.currentTimeMillis(), -7046029254386353131L);
    }
    
    public FastRandom(final long seed) {
        this(seed, -7046029254386353131L);
    }
    
    public FastRandom(final long seed, final long gamma) {
        this.seed = seed;
        this.gamma = gamma;
    }
    
    public FastRandom seed(final long seed) {
        this.seed = seed;
        return this;
    }
    
    public FastRandom seed(final long seed, final long gamma) {
        this.seed = seed;
        this.gamma = mixGamma(gamma);
        return this;
    }
    
    public FastRandom gamma(final long gamma) {
        this.gamma = gamma;
        return this;
    }
    
    public int nextInt() {
        return mix32(this.nextSeed());
    }
    
    public int nextInt(final int bound) {
        int r = mix32(this.nextSeed());
        final int m = bound - 1;
        if ((bound & m) == 0x0) {
            r &= m;
        }
        else {
            for (int u = r >>> 1; u + m - (r = u % bound) < 0; u = mix32(this.nextSeed()) >>> 1) {}
        }
        return r;
    }
    
    public float nextFloat() {
        return (mix32(this.nextSeed()) >>> 8) * 5.9604645E-8f;
    }
    
    public boolean nextBoolean() {
        return mix32(this.nextSeed()) < 0;
    }
    
    private long nextSeed() {
        return this.seed += this.gamma;
    }
    
    private static int mix32(long z) {
        z = (z ^ z >>> 33) * 7109453100751455733L;
        return (int)((z ^ z >>> 28) * -3808689974395783757L >>> 32);
    }
    
    private static long mixGamma(long z) {
        z = (z ^ z >>> 33) * -49064778989728563L;
        z = (z ^ z >>> 33) * -4265267296055464877L;
        z = ((z ^ z >>> 33) | 0x1L);
        final int n = Long.bitCount(z ^ z >>> 1);
        return (n < 24) ? (z ^ 0xAAAAAAAAAAAAAAAAL) : z;
    }
}
