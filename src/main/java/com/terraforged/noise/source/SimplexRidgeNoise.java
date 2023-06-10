//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.noise.source;

import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.util.Noise;
import com.terraforged.noise.util.NoiseUtil;
import java.util.Arrays;

public class SimplexRidgeNoise extends NoiseSource {
    private static final int RIDGED_MAX_OCTAVE = 30;
    private final float[] spectralWeights = new float[30];
    private final float min;
    private final float max;
    private final float range;

    public SimplexRidgeNoise(Builder builder) {
        super(builder);
        float h = 1.0F;
        float frequency = 1.0F;

        for(int i = 0; i < 30; ++i) {
            this.spectralWeights[i] = NoiseUtil.pow(frequency, -h);
            frequency *= this.lacunarity;
        }

        this.min = 0.0F;
        this.max = SimplexNoise2.max(builder.getOctaves(), builder.getGain());
        this.range = Math.abs(this.max - this.min);
    }

    public String getSpecName() {
        return "SimplexRidge";
    }

    public float getValue(float x, float y, int seed) {
        x *= this.frequency;
        y *= this.frequency;
        float value = 0.0F;
        float weight = 1.0F;
        float offset = 1.0F;
        float amp = 2.0F;

        for(int octave = 0; octave < this.octaves; ++octave) {
            float signal = Noise.singleSimplex(x, y, seed + octave);
            signal = Math.abs(signal);
            signal = offset - signal;
            signal *= signal;
            signal *= weight;
            weight = signal * amp;
            weight = NoiseUtil.clamp(weight, 0.0F, 1.0F);
            value += signal * this.spectralWeights[octave];
            x *= this.lacunarity;
            y *= this.lacunarity;
            amp *= this.gain;
        }

        return NoiseUtil.map(value, this.min, this.max, this.range);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof SimplexRidgeNoise)) {
            return false;
        } else if (!super.equals(o)) {
            return false;
        } else {
            SimplexRidgeNoise that = (SimplexRidgeNoise)o;
            if (Float.compare(that.min, this.min) != 0) {
                return false;
            } else if (Float.compare(that.max, this.max) != 0) {
                return false;
            } else if (Float.compare(that.range, this.range) != 0) {
                return false;
            } else {
                return this.interpolation == that.interpolation;
            }
        }
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(this.spectralWeights);
        result = 31 * result + (this.min != 0.0F ? Float.floatToIntBits(this.min) : 0);
        result = 31 * result + (this.max != 0.0F ? Float.floatToIntBits(this.max) : 0);
        return 31 * result + (this.range != 0.0F ? Float.floatToIntBits(this.range) : 0);
    }

    public static DataSpec<SimplexRidgeNoise> ridgeSpec() {
        return specBuilder("SimplexRidge", SimplexRidgeNoise.class, SimplexRidgeNoise::new).build();
    }
}
