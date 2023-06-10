//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.terraforged.noise.source;

import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.util.Noise;
import com.terraforged.noise.util.NoiseUtil;

public class PerlinNoise extends NoiseSource {
    private static final float[] signals = new float[]{1.0F, 0.9F, 0.83F, 0.75F, 0.64F, 0.62F, 0.61F};
    protected final float min;
    protected final float max;
    protected final float range;

    public PerlinNoise(Builder builder) {
        super(builder);
        this.min = this.min(builder.getOctaves(), builder.getGain());
        this.max = this.max(builder.getOctaves(), builder.getGain());
        this.range = Math.abs(this.max - this.min);
    }

    public String getSpecName() {
        return "Perlin";
    }

    public float getValue(float x, float y) {
        x *= this.frequency;
        y *= this.frequency;
        float sum = 0.0F;
        float amp = this.gain;

        for(int i = 0; i < this.octaves; ++i) {
            sum += Noise.singlePerlin(x, y, this.seed + i, this.interpolation) * amp;
            x *= this.lacunarity;
            y *= this.lacunarity;
            amp *= this.gain;
        }

        return NoiseUtil.map(sum, this.min, this.max, this.range);
    }

    public float getValue(float x, float y, int seed) {
        x *= this.frequency;
        y *= this.frequency;
        float sum = 0.0F;
        float amp = this.gain;

        for(int i = 0; i < this.octaves; ++i) {
            sum += Noise.singlePerlin(x, y, seed + i, this.interpolation) * amp;
            x *= this.lacunarity;
            y *= this.lacunarity;
            amp *= this.gain;
        }

        return NoiseUtil.map(sum, this.min, this.max, this.range);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            if (!super.equals(o)) {
                return false;
            } else {
                PerlinNoise that = (PerlinNoise)o;
                if (Float.compare(that.min, this.min) != 0) {
                    return false;
                } else if (Float.compare(that.max, this.max) != 0) {
                    return false;
                } else {
                    return Float.compare(that.range, this.range) == 0;
                }
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.min != 0.0F ? Float.floatToIntBits(this.min) : 0);
        result = 31 * result + (this.max != 0.0F ? Float.floatToIntBits(this.max) : 0);
        result = 31 * result + (this.range != 0.0F ? Float.floatToIntBits(this.range) : 0);
        return result;
    }

    protected float min(int octaves, float gain) {
        return -this.max(octaves, gain);
    }

    protected float max(int octaves, float gain) {
        float signal = signal(octaves);
        float sum = 0.0F;
        float amp = gain;

        for(int i = 0; i < octaves; ++i) {
            sum += signal * amp;
            amp *= gain;
        }

        return sum;
    }

    private static float signal(int octaves) {
        int index = Math.min(octaves, signals.length - 1);
        return signals[index];
    }

    public static DataSpec<PerlinNoise> spec() {
        return specBuilder("Perlin", PerlinNoise.class, PerlinNoise::new).build();
    }
}
