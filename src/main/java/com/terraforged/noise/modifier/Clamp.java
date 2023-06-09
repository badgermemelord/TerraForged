// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.modifier;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.Module;
import com.terraforged.noise.Source;

public class Clamp extends Modifier
{
    private final Module min;
    private final Module max;
    private static final DataFactory<Clamp> factory;
    
    public Clamp(final Module source, final float min, final float max) {
        this(source, Source.constant(min), Source.constant(max));
    }
    
    public Clamp(final Module source, final Module min, final Module max) {
        super(source);
        this.min = min;
        this.max = max;
    }
    
    @Override
    public String getSpecName() {
        return "Clamp";
    }
    
    @Override
    public float minValue() {
        return this.min.minValue();
    }
    
    @Override
    public float maxValue() {
        return this.max.maxValue();
    }
    
    @Override
    public float modify(final float x, final float y, final float noiseValue) {
        final float min = this.min.getValue(x, y);
        final float max = this.max.getValue(x, y);
        if (noiseValue < min) {
            return min;
        }
        if (noiseValue > max) {
            return max;
        }
        return noiseValue;
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
        final Clamp clamp = (Clamp)o;
        return this.min.equals(clamp.min) && this.max.equals(clamp.max);
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.min.hashCode();
        result = 31 * result + this.max.hashCode();
        return result;
    }
    
    public static DataSpec<Clamp> spec() {
        return Modifier.sourceBuilder(Clamp.class, Clamp.factory).addObj("min", Module.class, c -> c.min).addObj("max", Module.class, c -> c.max).build();
    }
    
    static {
        factory = ((data, spec, context) -> new Clamp(spec.get("source", data, Module.class, context), spec.get("min", data, Module.class, context), spec.get("max", data, Module.class, context)));
    }
}
