// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.source;

import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.util.Noise;
import com.terraforged.noise.util.NoiseUtil;

public class PerlinNoise2 extends NoiseSource
{
    private static final float[] signals;
    protected final float min;
    protected final float max;
    protected final float range;
    
    public PerlinNoise2(final Builder builder) {
        super(builder);
        this.min = this.min(builder.getOctaves(), builder.getGain());
        this.max = this.max(builder.getOctaves(), builder.getGain());
        this.range = Math.abs(this.max - this.min);
    }
    
    @Override
    public String getSpecName() {
        return "Perlin2";
    }
    
    @Override
    public float getValue(float x, float y) {
        x *= this.frequency;
        y *= this.frequency;
        float sum = 0.0f;
        float amp = this.gain;
        for (int i = 0; i < this.octaves; ++i) {
            sum += Noise.singlePerlin2(x, y, this.seed + i, this.interpolation) * amp;
            x *= this.lacunarity;
            y *= this.lacunarity;
            amp *= this.gain;
        }
        return NoiseUtil.map(sum, this.min, this.max, this.range);
    }
    
    @Override
    public float getValue(float x, float y, final int seed) {
        x *= this.frequency;
        y *= this.frequency;
        float sum = 0.0f;
        float amp = this.gain;
        for (int i = 0; i < this.octaves; ++i) {
            sum += Noise.singlePerlin(x, y, seed + i, this.interpolation) * amp;
            x *= this.lacunarity;
            y *= this.lacunarity;
            amp *= this.gain;
        }
        return NoiseUtil.map(sum, this.min, this.max, this.range);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final PerlinNoise2 that = (PerlinNoise2)o;
        return Float.compare(that.min, this.min) == 0 && Float.compare(that.max, this.max) == 0 && Float.compare(that.range, this.range) == 0;
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + ((this.min != 0.0f) ? Float.floatToIntBits(this.min) : 0);
        result = 31 * result + ((this.max != 0.0f) ? Float.floatToIntBits(this.max) : 0);
        result = 31 * result + ((this.range != 0.0f) ? Float.floatToIntBits(this.range) : 0);
        return result;
    }
    
    protected float min(final int octaves, final float gain) {
        return -this.max(octaves, gain);
    }
    
    protected float max(final int octaves, final float gain) {
        final float signal = signal(octaves);
        float sum = 0.0f;
        float amp = gain;
        for (int i = 0; i < octaves; ++i) {
            sum += signal * amp;
            amp *= gain;
        }
        return sum;
    }
    
    private static float signal(final int octaves) {
        final int index = Math.min(octaves, PerlinNoise2.signals.length - 1);
        return PerlinNoise2.signals[index];
    }
    
    public static DataSpec<PerlinNoise2> spec() {
        return NoiseSource.specBuilder("Perlin2", PerlinNoise2.class, PerlinNoise2::new).build();
    }
    
    static {
        signals = new float[] { 1.0f, 0.9f, 0.83f, 0.75f, 0.64f, 0.62f, 0.61f };
    }
}
