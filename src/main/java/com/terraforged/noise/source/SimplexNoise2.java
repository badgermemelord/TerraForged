// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.source;

import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.util.Noise;
import com.terraforged.noise.util.NoiseUtil;

public class SimplexNoise2 extends NoiseSource
{
    private final float min;
    private final float max;
    private final float range;
    private static final float[] signals;
    
    public SimplexNoise2(final Builder builder) {
        super(builder);
        this.min = -max(builder.getOctaves(), builder.getGain());
        this.max = max(builder.getOctaves(), builder.getGain());
        this.range = this.max - this.min;
    }
    
    @Override
    public String getSpecName() {
        return "Simplex2";
    }
    
    @Override
    public float getValue(float x, float y, final int seed) {
        x *= this.frequency;
        y *= this.frequency;
        float sum = 0.0f;
        float amp = 1.0f;
        for (int i = 0; i < this.octaves; ++i) {
            sum += Noise.singleSimplex(x, y, seed + i) * amp;
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
        final SimplexNoise2 that = (SimplexNoise2)o;
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
    
    protected static float max(final int octaves, final float gain) {
        final float signal = signal(octaves);
        float sum = 0.0f;
        float amp = 1.0f;
        for (int i = 0; i < octaves; ++i) {
            sum += amp * signal;
            amp *= gain;
        }
        return sum;
    }
    
    private static float signal(final int octaves) {
        final int index = Math.min(octaves, SimplexNoise2.signals.length - 1);
        return SimplexNoise2.signals[index];
    }
    
    public static DataSpec<SimplexNoise2> spec() {
        return NoiseSource.specBuilder("Simplex2", SimplexNoise2.class, SimplexNoise2::new).build();
    }
    
    static {
        signals = new float[] { 1.0f, 0.989f, 0.81f, 0.781f, 0.708f, 0.702f, 0.696f };
    }
}
