//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.noise.combiner;

import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.Module;

public class Add extends Combiner {
    public Add(Module... modules) {
        super(modules);
    }

    public String getSpecName() {
        return "Add";
    }

    protected float minTotal(float total, Module next) {
        return total + next.minValue();
    }

    protected float maxTotal(float total, Module next) {
        return total + next.maxValue();
    }

    protected float combine(float total, float value) {
        return total + value;
    }

    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public int hashCode() {
        return super.hashCode();
    }

    public static DataSpec<?> spec() {
        return spec("Add", Add::new);
    }
}
