// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.source;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.Module;
import com.terraforged.noise.util.Noise;
import com.terraforged.noise.util.NoiseSpec;
import com.terraforged.noise.util.NoiseUtil;

public class Rand implements Module
{
    private final int seed;
    private final float frequency;
    private static final DataFactory<Rand> factory;
    
    public Rand(final Builder builder) {
        this.seed = builder.getSeed();
        this.frequency = builder.getFrequency();
    }
    
    @Override
    public String getSpecName() {
        return "Rand";
    }
    
    @Override
    public float getValue(float x, float y) {
        x *= this.frequency;
        y *= this.frequency;
        final float value = Noise.white(x, y, this.seed);
        return Math.abs(value);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Rand rand = (Rand)o;
        return this.seed == rand.seed && Float.compare(rand.frequency, this.frequency) == 0;
    }
    
    @Override
    public int hashCode() {
        int result = this.seed;
        result = 31 * result + ((this.frequency != 0.0f) ? Float.floatToIntBits(this.frequency) : 0);
        return result;
    }
    
    public float getValue(final float x, final float y, final int childSeed) {
        return Noise.white(x, y, NoiseUtil.hash(this.seed, childSeed));
    }
    
    public int nextInt(final float x, final float y, final int range) {
        final float noise = this.getValue(x, y);
        return NoiseUtil.round(range * noise / (range + range));
    }
    
    public int nextInt(final float x, final float y, final int childSeed, final int range) {
        final float noise = this.getValue(x, y, childSeed);
        return NoiseUtil.round(range * noise / (range + range));
    }
    
    public static DataSpec<Rand> spec() {
        return DataSpec.builder("Rand", Rand.class, Rand.factory).add("seed", 0, NoiseSpec.seed(r -> r.seed)).add("frequency", (Object)1.0f, r -> r.frequency).build();
    }
    
    static {
        factory = ((data, spec, context) -> new Rand(NoiseSource.readData(data, spec, context)));
    }
}
