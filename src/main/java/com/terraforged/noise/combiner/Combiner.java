//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.noise.combiner;

import com.terraforged.cereal.Cereal;
import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.value.DataList;
import com.terraforged.noise.Module;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public abstract class Combiner implements Module {
    private final float min;
    private final float max;
    protected final Module[] sources;

    public Combiner(Module... sources) {
        float min = 0.0F;
        float max = 0.0F;
        if (sources.length > 0) {
            min = sources[0].minValue();
            max = sources[0].maxValue();

            for(int i = 1; i < sources.length; ++i) {
                Module next = sources[i];
                min = this.minTotal(min, next);
                max = this.maxTotal(max, next);
            }
        }

        this.min = min;
        this.max = max;
        this.sources = sources;
    }

    public float getValue(float x, float y) {
        float result = 0.0F;
        if (this.sources.length > 0) {
            result = this.sources[0].getValue(x, y);

            for(int i = 1; i < this.sources.length; ++i) {
                Module module = this.sources[i];
                float value = module.getValue(x, y);
                result = this.combine(result, value);
            }
        }

        return result;
    }

    public float minValue() {
        return this.min;
    }

    public float maxValue() {
        return this.max;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Combiner combiner = (Combiner)o;
            if (Float.compare(combiner.min, this.min) != 0) {
                return false;
            } else {
                return Float.compare(combiner.max, this.max) != 0 ? false : Arrays.equals(this.sources, combiner.sources);
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = this.min != 0.0F ? Float.floatToIntBits(this.min) : 0;
        result = 31 * result + (this.max != 0.0F ? Float.floatToIntBits(this.max) : 0);
        return 31 * result + Arrays.hashCode(this.sources);
    }

    protected abstract float minTotal(float var1, Module var2);

    protected abstract float maxTotal(float var1, Module var2);

    protected abstract float combine(float var1, float var2);

    private static DataFactory<Combiner> constructor(Function<Module[], Combiner> constructor) {
        return (data, spec, context) -> {
            DataList list = data.getList("modules");
            List<Module> modules = Cereal.deserialize(list, Module.class);
            return (Combiner)constructor.apply(modules.toArray(new Module[0]));
        };
    }

    public static DataSpec<Combiner> spec(String name, Function<Module[], Combiner> constructor) {
        return DataSpec.builder(name, Combiner.class, constructor(constructor)).addList("modules", Module.class, c -> Arrays.asList(c.sources)).build();
    }
}
