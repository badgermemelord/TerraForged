// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.modifier;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.Module;
import com.terraforged.noise.util.NoiseUtil;

public class VariableCurve extends Modifier
{
    private final Module midpoint;
    private final Module gradient;
    private static final DataFactory<VariableCurve> factory;
    
    public VariableCurve(final Module source, final Module mid, final Module gradient) {
        super(source);
        this.midpoint = mid;
        this.gradient = gradient;
    }
    
    @Override
    public String getSpecName() {
        return "VariCurve";
    }
    
    @Override
    public float modify(final float x, final float y, final float noiseValue) {
        final float mid = this.midpoint.getValue(x, y);
        final float curve = this.gradient.getValue(x, y);
        return NoiseUtil.curve(noiseValue, mid, curve);
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
        final VariableCurve that = (VariableCurve)o;
        return this.midpoint.equals(that.midpoint) && this.gradient.equals(that.gradient);
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.midpoint.hashCode();
        result = 31 * result + this.gradient.hashCode();
        return result;
    }
    
    public static DataSpec<VariableCurve> spec() {
        return Modifier.sourceBuilder("VariCurve", VariableCurve.class, VariableCurve.factory).addObj("midpoint", Module.class, v -> v.midpoint).addObj("gradient", Module.class, v -> v.gradient).build();
    }
    
    static {
        factory = ((data, spec, context) -> new VariableCurve(spec.get("source", data, Module.class, context), spec.get("midpoint", data, Module.class, context), spec.get("gradient", data, Module.class, context)));
    }
}
