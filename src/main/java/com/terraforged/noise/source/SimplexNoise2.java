//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.noise.source;

import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.util.Noise;
import com.terraforged.noise.util.NoiseUtil;

public class SimplexNoise2 extends NoiseSource {
    private final float min;
    private final float max;
    private final float range;
    private static final float[] signals = new float[]{1.0F, 0.989F, 0.81F, 0.781F, 0.708F, 0.702F, 0.696F};

    public SimplexNoise2(Builder builder) {
        super(builder);
        this.min = -max(builder.getOctaves(), builder.getGain());
        this.max = max(builder.getOctaves(), builder.getGain());
        this.range = this.max - this.min;
    }

    public String getSpecName() {
        return "Simplex2";
    }

    public float getValue(float x, float y, int seed) {
        x *= this.frequency;
        y *= this.frequency;
        float sum = 0.0F;
        float amp = 1.0F;

        for(int i = 0; i < this.octaves; ++i) {
            sum += Noise.singleSimplex(x, y, seed + i) * amp;
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
            SimplexNoise2 that = (SimplexNoise2)o;
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

    protected static float max(int octaves, float gain) {
        float signal = signal(octaves);
        float sum = 0.0F;
        float amp = 1.0F;

        for(int i = 0; i < octaves; ++i) {
            sum += amp * signal;
            amp *= gain;
        }

        return sum;
    }

    private static float signal(int octaves) {
        int index = Math.min(octaves, signals.length - 1);
        return signals[index];
    }

    public static DataSpec<SimplexNoise2> spec() {
        return specBuilder("Simplex2", SimplexNoise2.class, SimplexNoise2::new).build();
    }
}
