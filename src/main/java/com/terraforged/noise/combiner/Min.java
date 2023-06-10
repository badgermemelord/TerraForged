//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.noise.combiner;

import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.Module;

public class Min extends Combiner {
    public Min(Module... modules) {
        super(modules);
    }

    public String getSpecName() {
        return "Min";
    }

    protected float minTotal(float total, Module next) {
        return Math.min(total, next.minValue());
    }

    protected float maxTotal(float total, Module next) {
        return this.minTotal(total, next);
    }

    protected float combine(float total, float value) {
        return Math.min(total, value);
    }

    public boolean equals(Object o) {
        return super.equals(o);
    }

    public int hashCode() {
        return super.hashCode();
    }

    public static DataSpec<?> spec() {
        return spec("Min", Min::new);
    }
}
