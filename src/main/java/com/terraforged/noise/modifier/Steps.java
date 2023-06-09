// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.modifier;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.Module;
import com.terraforged.noise.func.CurveFunc;
import com.terraforged.noise.util.NoiseUtil;

public class Steps extends Modifier
{
    private final Module steps;
    private final Module slopeMin;
    private final Module slopeMax;
    private final CurveFunc curve;
    private static final DataFactory<Steps> factory;
    
    public Steps(final Module source, final Module steps, final Module slopeMin, final Module slopeMax, final CurveFunc slopeCurve) {
        super(source);
        this.steps = steps;
        this.curve = slopeCurve;
        this.slopeMin = slopeMin;
        this.slopeMax = slopeMax;
    }
    
    @Override
    public String getSpecName() {
        return "Steps";
    }
    
    @Override
    public float modify(final float x, final float y, float noiseValue) {
        final float min = this.slopeMin.getValue(x, y);
        final float max = this.slopeMax.getValue(x, y);
        final float stepCount = this.steps.getValue(x, y);
        final float range = max - min;
        if (range <= 0.0f) {
            return (int)(noiseValue * stepCount) / stepCount;
        }
        noiseValue = 1.0f - noiseValue;
        final float value = (int)(noiseValue * stepCount) / stepCount;
        final float delta = noiseValue - value;
        final float alpha = NoiseUtil.map(delta * stepCount, min, max, range);
        return 1.0f - NoiseUtil.lerp(value, noiseValue, this.curve.apply(alpha));
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
        final Steps steps1 = (Steps)o;
        return this.steps.equals(steps1.steps) && this.slopeMin.equals(steps1.slopeMin) && this.slopeMax.equals(steps1.slopeMax) && this.curve.equals(steps1.curve);
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.steps.hashCode();
        result = 31 * result + this.slopeMin.hashCode();
        result = 31 * result + this.slopeMax.hashCode();
        result = 31 * result + this.curve.hashCode();
        return result;
    }
    
    public static DataSpec<Steps> spec() {
        return Modifier.specBuilder(Steps.class, Steps.factory).addObj("curve", CurveFunc.class, s -> s.curve).addObj("source", Module.class, s -> s.source).addObj("steps", Module.class, s -> s.steps).addObj("slope_min", Module.class, s -> s.slopeMin).addObj("slope_max", Module.class, s -> s.slopeMax).build();
    }
    
    static {
        factory = ((data, spec, context) -> new Steps(spec.get("source", data, Module.class, context), spec.get("steps", data, Module.class, context), spec.get("slope_min", data, Module.class, context), spec.get("slope_max", data, Module.class, context), spec.get("curve", data, CurveFunc.class, context)));
    }
}
