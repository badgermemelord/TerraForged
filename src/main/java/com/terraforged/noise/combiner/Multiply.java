//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.noise.combiner;

import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.Module;

public class Multiply extends Combiner {
    public Multiply(Module... modules) {
        super(modules);
    }

    public String getSpecName() {
        return "Mult";
    }

    protected float minTotal(float total, Module next) {
        return total * next.minValue();
    }

    protected float maxTotal(float total, Module next) {
        return total * next.maxValue();
    }

    protected float combine(float total, float value) {
        return total * value;
    }

    public boolean equals(Object o) {
        return super.equals(o);
    }

    public int hashCode() {
        return super.hashCode();
    }

    public static DataSpec<?> spec() {
        return spec("Mult", Multiply::new);
    }
}
