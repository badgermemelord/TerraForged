//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.rivermap.river;

import com.terraforged.engine.util.pos.PosUtil;
import com.terraforged.noise.util.Noise;
import com.terraforged.noise.util.NoiseUtil;
import java.util.Random;

public class RiverWarp {
    public static final RiverWarp NONE = new RiverWarp(0, 0.0F, 0.0F, 0.0F, 0.0F);
    private static final float WIGGLE_MIN = 2.0F;
    private static final float WIGGLE_MAX = 45.0F;
    private static final float WIGGLE_DIST = 25.0F;
    private static final float WIGGLE_FADE = 0.075F;
    private static final float WIGGLE_FREQUENCY = 8.0F;
    private static final float LEN_FACTOR_INV = 4.0E-4F;
    private final int seed;
    private final float lower;
    private final float upper;
    private final float lowerRange;
    private final float upperRange;
    private final float frequency;
    private final float scale;

    public RiverWarp(int seed, float lower, float upper, float frequency, float scale) {
        this.seed = seed;
        this.frequency = frequency;
        this.scale = scale;
        this.lower = lower;
        this.upper = upper;
        this.lowerRange = 1.0F / lower;
        this.upperRange = 1.0F / (1.0F - upper);
    }

    public RiverWarp createChild(float lower, float upper, float factor, Random random) {
        return new RiverWarp(random.nextInt(), lower, upper, this.frequency * factor, this.scale * factor);
    }

    public boolean test(float t) {
        return this != NONE && t >= 0.0F && t <= 1.0F;
    }

    public long getOffset(float x, float z, float t, River river) {
        float alpha1 = this.getWarpAlpha(t);
        float px = x * this.frequency;
        float pz = z * this.frequency;
        float distance = alpha1 * this.scale;
        float noise = Noise.singleSimplex(px, pz, this.seed);
        float dx = river.normX * noise * distance;
        float dz = river.normZ * noise * distance;
        float alpha2 = this.getWiggleAlpha(t);
        float factor = river.length * 4.0E-4F;
        float wiggleFreq = 8.0F * factor;
        float wiggleDist = NoiseUtil.clamp(alpha2 * 25.0F * factor, 2.0F, 45.0F);
        float rads = noise + t * ((float) (Math.PI * 2)) * wiggleFreq;
        dx += NoiseUtil.cos(rads) * river.normX * wiggleDist;
        dz += NoiseUtil.sin(rads) * river.normZ * wiggleDist;
        return PosUtil.packf(dx, dz);
    }

    private float getWarpAlpha(float t) {
        if (t < 0.0F || t > 1.0F) {
            return 0.0F;
        } else if (t < this.lower) {
            return t * this.lowerRange;
        } else {
            return t > this.upper ? (1.0F - t) * this.upperRange : 1.0F;
        }
    }

    private float getWiggleAlpha(float t) {
        return NoiseUtil.map(t, 0.0F, 0.075F, 0.075F);
    }

    public static RiverWarp create(float fade, Random random) {
        return create(fade, 1.0F - fade, random);
    }

    public static RiverWarp create(float lower, float upper, Random random) {
        float scale = 125.0F + (float)random.nextInt(50);
        float frequency = 5.0E-4F + random.nextFloat() * 5.0E-4F;
        return new RiverWarp(random.nextInt(), lower, upper, frequency, scale);
    }
}
