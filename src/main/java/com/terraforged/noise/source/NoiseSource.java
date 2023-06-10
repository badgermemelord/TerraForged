//
// Source code recreated from a .class file by Quiltflower
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

public abstract class NoiseSource implements Module {
    protected final int seed;
    protected final int octaves;
    protected final float gain;
    protected final float frequency;
    protected final float lacunarity;
    protected final Interpolation interpolation;

    public NoiseSource(Builder builder) {
        this.seed = builder.getSeed();
        this.octaves = builder.getOctaves();
        this.lacunarity = builder.getLacunarity();
        this.gain = builder.getGain();
        this.frequency = builder.getFrequency();
        this.interpolation = builder.getInterp();
    }

    public float getValue(float x, float y) {
        return this.getValue(x, y, this.seed);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            NoiseSource that = (NoiseSource)o;
            if (this.seed != that.seed) {
                return false;
            } else if (this.octaves != that.octaves) {
                return false;
            } else if (Float.compare(that.gain, this.gain) != 0) {
                return false;
            } else if (Float.compare(that.frequency, this.frequency) != 0) {
                return false;
            } else if (Float.compare(that.lacunarity, this.lacunarity) != 0) {
                return false;
            } else {
                return this.interpolation == that.interpolation;
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = this.seed;
        result = 31 * result + this.octaves;
        result = 31 * result + (this.gain != 0.0F ? Float.floatToIntBits(this.gain) : 0);
        result = 31 * result + (this.frequency != 0.0F ? Float.floatToIntBits(this.frequency) : 0);
        result = 31 * result + (this.lacunarity != 0.0F ? Float.floatToIntBits(this.lacunarity) : 0);
        return 31 * result + this.interpolation.hashCode();
    }

    public abstract float getValue(float var1, float var2, int var3);

    public static Builder readData(DataObject data, DataSpec<?> spec, Context context) {
        Builder builder = new Builder();
        builder.seed(NoiseSpec.seed(data, spec, context));
        builder.gain(spec.get("gain", data, DataValue::asDouble));
        builder.octaves(spec.get("octaves", data, DataValue::asInt));
        builder.frequency(spec.get("frequency", data, DataValue::asDouble));
        builder.lacunarity(spec.get("lacunarity", data, DataValue::asDouble));
        builder.interp((Interpolation)spec.get("interpolation", data, v -> Interpolation.valueOf(v.asString())));
        if (data.has("cell_func")) {
            builder.cellFunc((CellFunc)spec.getEnum("cell_func", data, CellFunc.class));
        }

        if (data.has("edge_func")) {
            builder.edgeFunc((EdgeFunc)spec.getEnum("edge_func", data, EdgeFunc.class));
        }

        if (data.has("dist_func")) {
            builder.distFunc((DistanceFunc)spec.getEnum("dist_func", data, DistanceFunc.class));
        }

        if (data.has("source")) {
            builder.source((Module)spec.get("source", data, Module.class, context));
        }

        return builder;
    }

    private static <S extends NoiseSource> DataFactory<S> constructor(Function<Builder, S> constructor) {
        //deleted (noisesource) cast
        return (data, spec, context) -> (constructor.apply(readData(data, spec, context)));
    }

    public static <S extends NoiseSource> com.terraforged.cereal.spec.DataSpec.Builder<S> specBuilder(
            String name, Class<S> type, Function<Builder, S> constructor
    ) {
        return specBuilder(name, type, constructor(constructor));
    }

    public static <S extends NoiseSource> com.terraforged.cereal.spec.DataSpec.Builder<S> specBuilder(String name, Class<S> type, DataFactory<S> constructor) {
        return DataSpec.builder(name, type, constructor)
                .add("seed", 1337, NoiseSpec.seed(f -> f.seed))
                .add("gain", 0.5F, f -> f.gain)
                .add("octaves", 1, f -> f.octaves)
                .add("frequency", 1.0F, f -> f.frequency)
                .add("lacunarity", 2.0F, f -> f.lacunarity)
                .add("interpolation", Builder.DEFAULT_INTERPOLATION, f -> f.interpolation);
    }
}
