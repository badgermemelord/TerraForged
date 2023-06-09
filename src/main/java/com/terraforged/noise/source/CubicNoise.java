// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.source;

import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.util.Noise;
import com.terraforged.noise.util.NoiseUtil;

public class CubicNoise extends NoiseSource
{
    private final float min;
    private final float max;
    private final float range;
    
    public CubicNoise(final Builder builder) {
        super(builder);
        this.min = this.calculateBound(-0.75f, builder.getOctaves(), builder.getGain());
        this.max = this.calculateBound(0.75f, builder.getOctaves(), builder.getGain());
        this.range = this.max - this.min;
    }
    
    @Override
    public String getSpecName() {
        return "Cubic";
    }
    
    @Override
    public float minValue() {
        return this.min;
    }
    
    @Override
    public float maxValue() {
        return this.max;
    }
    
    @Override
    public float getValue(float x, float y, int seed) {
        x *= this.frequency;
        y *= this.frequency;
        float sum = Noise.singleCubic(x, y, seed);
        float amp = 1.0f;
        int i = 0;
        while (++i < this.octaves) {
            x *= this.lacunarity;
            y *= this.lacunarity;
            amp *= this.gain;
            sum += Noise.singleCubic(x, y, ++seed) * amp;
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
        final CubicNoise that = (CubicNoise)o;
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
    
    private float calculateBound(final float signal, final int octaves, final float gain) {
        float amp = 1.0f;
        float value = signal;
        for (int i = 1; i < octaves; ++i) {
            amp *= gain;
            value += signal * amp;
        }
        return value;
    }
    
    public static DataSpec<CubicNoise> spec() {
        return NoiseSource.specBuilder("Cubic", CubicNoise.class, CubicNoise::new).build();
    }
}
