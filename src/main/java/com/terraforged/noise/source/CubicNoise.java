//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.noise.source;

import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.util.Noise;
import com.terraforged.noise.util.NoiseUtil;

public class CubicNoise extends NoiseSource {
    private final float min;
    private final float max;
    private final float range;

    public CubicNoise(Builder builder) {
        super(builder);
        this.min = this.calculateBound(-0.75F, builder.getOctaves(), builder.getGain());
        this.max = this.calculateBound(0.75F, builder.getOctaves(), builder.getGain());
        this.range = this.max - this.min;
    }

    public String getSpecName() {
        return "Cubic";
    }

    public float minValue() {
        return this.min;
    }

    public float maxValue() {
        return this.max;
    }

    public float getValue(float x, float y, int seed) {
        x *= this.frequency;
        y *= this.frequency;
        float sum = Noise.singleCubic(x, y, seed);
        float amp = 1.0F;

        for(int i = 0; ++i < this.octaves; sum += Noise.singleCubic(x, y, ++seed) * amp) {
            x *= this.lacunarity;
            y *= this.lacunarity;
            amp *= this.gain;
        }

        return NoiseUtil.map(sum, this.min, this.max, this.range);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null || this.getClass() != o.getClass()) {
            return false;
        } else if (!super.equals(o)) {
            return false;
        } else {
            CubicNoise that = (CubicNoise)o;
            if (Float.compare(that.min, this.min) != 0) {
                return false;
            } else if (Float.compare(that.max, this.max) != 0) {
                return false;
            } else {
                return Float.compare(that.range, this.range) == 0;
            }
        }
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.min != 0.0F ? Float.floatToIntBits(this.min) : 0);
        result = 31 * result + (this.max != 0.0F ? Float.floatToIntBits(this.max) : 0);
        return 31 * result + (this.range != 0.0F ? Float.floatToIntBits(this.range) : 0);
    }

    private float calculateBound(float signal, int octaves, float gain) {
        float amp = 1.0F;
        float value = signal;

        for(int i = 1; i < octaves; ++i) {
            amp *= gain;
            value += signal * amp;
        }

        return value;
    }

    public static DataSpec<CubicNoise> spec() {
        return specBuilder("Cubic", CubicNoise.class, CubicNoise::new).build();
    }
}
