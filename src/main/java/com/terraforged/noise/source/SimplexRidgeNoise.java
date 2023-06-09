// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.source;

import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.util.Noise;
import com.terraforged.noise.util.NoiseUtil;

import java.util.Arrays;

public class SimplexRidgeNoise extends NoiseSource
{
    private static final int RIDGED_MAX_OCTAVE = 30;
    private final float[] spectralWeights;
    private final float min;
    private final float max;
    private final float range;
    
    public SimplexRidgeNoise(final Builder builder) {
        super(builder);
        this.spectralWeights = new float[30];
        final float h = 1.0f;
        float frequency = 1.0f;
        for (int i = 0; i < 30; ++i) {
            this.spectralWeights[i] = NoiseUtil.pow(frequency, -h);
            frequency *= this.lacunarity;
        }
        this.min = 0.0f;
        this.max = SimplexNoise2.max(builder.getOctaves(), builder.getGain());
        this.range = Math.abs(this.max - this.min);
    }
    
    @Override
    public String getSpecName() {
        return "SimplexRidge";
    }
    
    @Override
    public float getValue(float x, float y, final int seed) {
        x *= this.frequency;
        y *= this.frequency;
        float value = 0.0f;
        float weight = 1.0f;
        final float offset = 1.0f;
        float amp = 2.0f;
        for (int octave = 0; octave < this.octaves; ++octave) {
            float signal = Noise.singleSimplex(x, y, seed + octave);
            signal = Math.abs(signal);
            signal = offset - signal;
            signal *= signal;
            signal *= weight;
            weight = signal * amp;
            weight = NoiseUtil.clamp(weight, 0.0f, 1.0f);
            value += signal * this.spectralWeights[octave];
            x *= this.lacunarity;
            y *= this.lacunarity;
            amp *= this.gain;
        }
        return NoiseUtil.map(value, this.min, this.max, this.range);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SimplexRidgeNoise)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final SimplexRidgeNoise that = (SimplexRidgeNoise)o;
        return Float.compare(that.min, this.min) == 0 && Float.compare(that.max, this.max) == 0 && Float.compare(that.range, this.range) == 0 && this.interpolation == that.interpolation;
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(this.spectralWeights);
        result = 31 * result + ((this.min != 0.0f) ? Float.floatToIntBits(this.min) : 0);
        result = 31 * result + ((this.max != 0.0f) ? Float.floatToIntBits(this.max) : 0);
        result = 31 * result + ((this.range != 0.0f) ? Float.floatToIntBits(this.range) : 0);
        return result;
    }
    
    public static DataSpec<SimplexRidgeNoise> ridgeSpec() {
        return NoiseSource.specBuilder("SimplexRidge", SimplexRidgeNoise.class, SimplexRidgeNoise::new).build();
    }
}
