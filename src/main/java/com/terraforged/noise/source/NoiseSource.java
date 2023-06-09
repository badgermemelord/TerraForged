// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.source;

import com.terraforged.cereal.spec.Context;
import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.value.DataObject;
import com.terraforged.cereal.value.DataValue;
import com.terraforged.noise.Module;
import com.terraforged.noise.func.CellFunc;
import com.terraforged.noise.func.DistanceFunc;
import com.terraforged.noise.func.EdgeFunc;
import com.terraforged.noise.func.Interpolation;
import com.terraforged.noise.util.NoiseSpec;

import java.util.function.Function;

public abstract class NoiseSource implements Module
{
    protected final int seed;
    protected final int octaves;
    protected final float gain;
    protected final float frequency;
    protected final float lacunarity;
    protected final Interpolation interpolation;
    
    public NoiseSource(final Builder builder) {
        this.seed = builder.getSeed();
        this.octaves = builder.getOctaves();
        this.lacunarity = builder.getLacunarity();
        this.gain = builder.getGain();
        this.frequency = builder.getFrequency();
        this.interpolation = builder.getInterp();
    }
    
    @Override
    public float getValue(final float x, final float y) {
        return this.getValue(x, y, this.seed);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final NoiseSource that = (NoiseSource)o;
        return this.seed == that.seed && this.octaves == that.octaves && Float.compare(that.gain, this.gain) == 0 && Float.compare(that.frequency, this.frequency) == 0 && Float.compare(that.lacunarity, this.lacunarity) == 0 && this.interpolation == that.interpolation;
    }
    
    @Override
    public int hashCode() {
        int result = this.seed;
        result = 31 * result + this.octaves;
        result = 31 * result + ((this.gain != 0.0f) ? Float.floatToIntBits(this.gain) : 0);
        result = 31 * result + ((this.frequency != 0.0f) ? Float.floatToIntBits(this.frequency) : 0);
        result = 31 * result + ((this.lacunarity != 0.0f) ? Float.floatToIntBits(this.lacunarity) : 0);
        result = 31 * result + this.interpolation.hashCode();
        return result;
    }
    
    public abstract float getValue(final float p0, final float p1, final int p2);
    
    public static Builder readData(final DataObject data, final DataSpec<?> spec, final Context context) {
        final Builder builder = new Builder();
        builder.seed(NoiseSpec.seed(data, spec, context));
        builder.gain(spec.get("gain", data, DataValue::asDouble));
        builder.octaves(spec.get("octaves", data, DataValue::asInt));
        builder.frequency(spec.get("frequency", data, DataValue::asDouble));
        builder.lacunarity(spec.get("lacunarity", data, DataValue::asDouble));
        builder.interp(spec.get("interpolation", data, v -> Interpolation.valueOf(v.asString())));
        if (data.has("cell_func")) {
            builder.cellFunc(spec.getEnum("cell_func", data, CellFunc.class));
        }
        if (data.has("edge_func")) {
            builder.edgeFunc(spec.getEnum("edge_func", data, EdgeFunc.class));
        }
        if (data.has("dist_func")) {
            builder.distFunc(spec.getEnum("dist_func", data, DistanceFunc.class));
        }
        if (data.has("source")) {
            builder.source(spec.get("source", data, Module.class, context));
        }
        return builder;
    }
    
    private static <S extends NoiseSource> DataFactory<S> constructor(final Function<Builder, S> constructor) {
        return (data, spec, context) -> constructor.apply(readData(data, spec, context));
    }
    
    public static <S extends NoiseSource> DataSpec.Builder<S> specBuilder(final String name, final Class<S> type, final Function<Builder, S> constructor) {
        return specBuilder(name, type, (DataFactory<S>)constructor((Function<Builder, S>)constructor));
    }
    
    public static <S extends NoiseSource> DataSpec.Builder<S> specBuilder(final String name, final Class<S> type, final DataFactory<S> constructor) {
        return DataSpec.builder(name, type, constructor).add("seed", 1337, NoiseSpec.seed(f -> f.seed)).add("gain", 0.5f, f -> f.gain).add("octaves", 1, f -> f.octaves).add("frequency", 1.0f, f -> f.frequency).add("lacunarity", 2.0f, f -> f.lacunarity).add("interpolation", Builder.DEFAULT_INTERPOLATION, f -> f.interpolation);
    }
}
