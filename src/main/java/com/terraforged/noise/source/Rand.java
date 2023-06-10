//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.noise.source;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.Module;
import com.terraforged.noise.util.Noise;
import com.terraforged.noise.util.NoiseSpec;
import com.terraforged.noise.util.NoiseUtil;

public class Rand implements Module {
    private final int seed;
    private final float frequency;
    private static final DataFactory<Rand> factory = (data, spec, context) -> new Rand(NoiseSource.readData(data, spec, context));

    public Rand(Builder builder) {
        this.seed = builder.getSeed();
        this.frequency = builder.getFrequency();
    }

    public String getSpecName() {
        return "Rand";
    }

    public float getValue(float x, float y) {
        x *= this.frequency;
        y *= this.frequency;
        float value = Noise.white(x, y, this.seed);
        return Math.abs(value);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Rand rand = (Rand)o;
            if (this.seed != rand.seed) {
                return false;
            } else {
                return Float.compare(rand.frequency, this.frequency) == 0;
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = this.seed;
        return 31 * result + (this.frequency != 0.0F ? Float.floatToIntBits(this.frequency) : 0);
    }

    public float getValue(float x, float y, int childSeed) {
        return Noise.white(x, y, NoiseUtil.hash(this.seed, childSeed));
    }

    public int nextInt(float x, float y, int range) {
        float noise = this.getValue(x, y);
        return NoiseUtil.round((float)range * noise / (float)(range + range));
    }

    public int nextInt(float x, float y, int childSeed, int range) {
        float noise = this.getValue(x, y, childSeed);
        return NoiseUtil.round((float)range * noise / (float)(range + range));
    }

    public static DataSpec<Rand> spec() {
        return DataSpec.builder("Rand", Rand.class, factory).add("seed", 0, NoiseSpec.seed(r -> r.seed)).add("frequency", 1.0F, r -> r.frequency).build();
    }
}
