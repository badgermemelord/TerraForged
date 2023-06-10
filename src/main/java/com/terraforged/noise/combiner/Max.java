//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.noise.combiner;

import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.Module;

public class Max extends Combiner {
    public Max(Module... modules) {
        super(modules);
    }

    public String getSpecName() {
        return "Max";
    }

    protected float minTotal(float total, Module next) {
        return this.maxTotal(total, next);
    }

    protected float maxTotal(float total, Module next) {
        return Math.max(total, next.maxValue());
    }

    protected float combine(float total, float value) {
        return Math.max(total, value);
    }

    public boolean equals(Object o) {
        return super.equals(o);
    }

    public int hashCode() {
        return super.hashCode();
    }

    public static DataSpec<?> spec() {
        return spec("Max", Max::new);
    }
}
