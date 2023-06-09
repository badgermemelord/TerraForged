// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.modifier;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.Module;

public class Threshold extends Modifier
{
    private final Module threshold;
    private static final DataFactory<Threshold> factory;
    
    public Threshold(final Module source, final Module threshold) {
        super(source);
        this.threshold = threshold;
    }
    
    @Override
    public String getSpecName() {
        return "Threshold";
    }
    
    @Override
    public float modify(final float x, final float y, final float noiseValue) {
        final float limit = this.threshold.getValue(x, y);
        if (noiseValue < limit) {
            return 0.0f;
        }
        return 1.0f;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final Threshold threshold1 = (Threshold)o;
        return this.threshold.equals(threshold1.threshold);
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.threshold.hashCode();
        return result;
    }
    
    public static DataSpec<Threshold> spec() {
        return Modifier.sourceBuilder(Threshold.class, Threshold.factory).addObj("threshold", Module.class, t -> t.threshold).build();
    }
    
    static {
        factory = ((data, spec, context) -> new Threshold(spec.get("source", data, Module.class, context), spec.get("threshold", data, Module.class, context)));
    }
}
