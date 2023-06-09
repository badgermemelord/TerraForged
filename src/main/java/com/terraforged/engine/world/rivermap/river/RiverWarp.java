// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.rivermap.river;

import com.terraforged.engine.util.pos.PosUtil;
import com.terraforged.noise.util.Noise;
import com.terraforged.noise.util.NoiseUtil;

import java.util.Random;

public class RiverWarp
{
    public static final RiverWarp NONE;
    private static final float WIGGLE_MIN = 2.0f;
    private static final float WIGGLE_MAX = 45.0f;
    private static final float WIGGLE_DIST = 25.0f;
    private static final float WIGGLE_FADE = 0.075f;
    private static final float WIGGLE_FREQUENCY = 8.0f;
    private static final float LEN_FACTOR_INV = 4.0E-4f;
    private final int seed;
    private final float lower;
    private final float upper;
    private final float lowerRange;
    private final float upperRange;
    private final float frequency;
    private final float scale;
    
    public RiverWarp(final int seed, final float lower, final float upper, final float frequency, final float scale) {
        this.seed = seed;
        this.frequency = frequency;
        this.scale = scale;
        this.lower = lower;
        this.upper = upper;
        this.lowerRange = 1.0f / lower;
        this.upperRange = 1.0f / (1.0f - upper);
    }
    
    public RiverWarp createChild(final float lower, final float upper, final float factor, final Random random) {
        return new RiverWarp(random.nextInt(), lower, upper, this.frequency * factor, this.scale * factor);
    }
    
    public boolean test(final float t) {
        return this != RiverWarp.NONE && t >= 0.0f && t <= 1.0f;
    }
    
    public long getOffset(final float x, final float z, final float t, final River river) {
        final float alpha1 = this.getWarpAlpha(t);
        final float px = x * this.frequency;
        final float pz = z * this.frequency;
        final float distance = alpha1 * this.scale;
        final float noise = Noise.singleSimplex(px, pz, this.seed);
        float dx = river.normX * noise * distance;
        float dz = river.normZ * noise * distance;
        final float alpha2 = this.getWiggleAlpha(t);
        final float factor = river.length * 4.0E-4f;
        final float wiggleFreq = 8.0f * factor;
        final float wiggleDist = NoiseUtil.clamp(alpha2 * 25.0f * factor, 2.0f, 45.0f);
        final float rads = noise + t * 6.2831855f * wiggleFreq;
        dx += NoiseUtil.cos(rads) * river.normX * wiggleDist;
        dz += NoiseUtil.sin(rads) * river.normZ * wiggleDist;
        return PosUtil.packf(dx, dz);
    }
    
    private float getWarpAlpha(final float t) {
        if (t < 0.0f || t > 1.0f) {
            return 0.0f;
        }
        if (t < this.lower) {
            return t * this.lowerRange;
        }
        if (t > this.upper) {
            return (1.0f - t) * this.upperRange;
        }
        return 1.0f;
    }
    
    private float getWiggleAlpha(final float t) {
        return NoiseUtil.map(t, 0.0f, 0.075f, 0.075f);
    }
    
    public static RiverWarp create(final float fade, final Random random) {
        return create(fade, 1.0f - fade, random);
    }
    
    public static RiverWarp create(final float lower, final float upper, final Random random) {
        final float scale = 125.0f + random.nextInt(50);
        final float frequency = 5.0E-4f + random.nextFloat() * 5.0E-4f;
        return new RiverWarp(random.nextInt(), lower, upper, frequency, scale);
    }
    
    static {
        NONE = new RiverWarp(0, 0.0f, 0.0f, 0.0f, 0.0f);
    }
}
