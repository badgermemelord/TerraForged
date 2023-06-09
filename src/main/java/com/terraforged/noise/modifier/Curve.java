// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.modifier;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.Module;
import com.terraforged.noise.func.CurveFunc;
import com.terraforged.noise.func.MidPointCurve;

public class Curve extends Modifier
{
    private final CurveFunc func;
    private static final DataFactory<Curve> factory;
    
    public Curve(final Module source, final CurveFunc func) {
        super(source);
        this.func = func;
    }
    
    public Curve(final Module source, final float mid, final float steepness) {
        this(source, new MidPointCurve(mid, steepness));
    }
    
    @Override
    public String getSpecName() {
        return "Curve";
    }
    
    @Override
    public float modify(final float x, final float y, final float noiseValue) {
        return this.func.apply(noiseValue);
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
        final Curve curve = (Curve)o;
        return this.func.equals(curve.func);
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.func.hashCode();
        return result;
    }
    
    public static DataSpec<Curve> spec() {
        return Modifier.sourceBuilder(Curve.class, Curve.factory).addObj("curve", CurveFunc.class, c -> c.func).build();
    }
    
    static {
        factory = ((data, spec, context) -> new Curve(spec.get("source", data, Module.class, context), spec.get("curve", data, CurveFunc.class, context)));
    }
}
