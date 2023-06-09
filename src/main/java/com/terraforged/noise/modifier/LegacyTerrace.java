// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.modifier;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.value.DataValue;
import com.terraforged.noise.Module;
import com.terraforged.noise.func.Interpolation;
import com.terraforged.noise.util.NoiseUtil;

public class LegacyTerrace extends Modifier
{
    private final int maxIndex;
    private final float blend;
    private final Step[] steps;
    private final Module lowerCurve;
    private final Module upperCurve;
    private static final DataFactory<LegacyTerrace> factory;
    
    public LegacyTerrace(final Module source, final Module lowerCurve, final Module upperCurve, final int steps, final float blendRange) {
        super(source);
        this.blend = blendRange;
        this.maxIndex = steps - 1;
        this.steps = new Step[steps];
        this.lowerCurve = lowerCurve;
        this.upperCurve = upperCurve;
        final float min = source.minValue();
        final float max = source.maxValue();
        final float range = max - min;
        final float spacing = range / (steps - 1);
        for (int i = 0; i < steps; ++i) {
            final float value = i * spacing;
            this.steps[i] = new Step(value, spacing, blendRange);
        }
    }
    
    @Override
    public String getSpecName() {
        return "LegacyTerrace";
    }
    
    @Override
    public float getValue(final float x, final float y) {
        float value = this.source.getValue(x, y);
        value = NoiseUtil.clamp(value, 0.0f, 1.0f);
        return this.modify(x, y, value);
    }
    
    @Override
    public float modify(final float x, final float y, final float noiseValue) {
        final int index = NoiseUtil.round(noiseValue * this.maxIndex);
        final Step step = this.steps[index];
        if (noiseValue < step.lowerBound) {
            if (index > 0) {
                final Step lower = this.steps[index - 1];
                float alpha = (noiseValue - lower.upperBound) / (step.lowerBound - lower.upperBound);
                alpha = 1.0f - Interpolation.CURVE3.apply(alpha);
                final float range = step.value - lower.value;
                return step.value - alpha * range * this.upperCurve.getValue(x, y);
            }
        }
        else if (noiseValue > step.upperBound && index < this.maxIndex) {
            final Step upper = this.steps[index + 1];
            float alpha = (noiseValue - step.upperBound) / (upper.lowerBound - step.upperBound);
            alpha = Interpolation.CURVE3.apply(alpha);
            final float range = upper.value - step.value;
            return step.value + alpha * range * this.lowerCurve.getValue(x, y);
        }
        return step.value;
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
        final LegacyTerrace that = (LegacyTerrace)o;
        return this.maxIndex == that.maxIndex && Float.compare(that.blend, this.blend) == 0 && this.lowerCurve.equals(that.lowerCurve) && this.upperCurve.equals(that.upperCurve);
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.maxIndex;
        result = 31 * result + ((this.blend != 0.0f) ? Float.floatToIntBits(this.blend) : 0);
        result = 31 * result + this.lowerCurve.hashCode();
        result = 31 * result + this.upperCurve.hashCode();
        return result;
    }
    
    private int getIndex(final float value) {
        final int index = NoiseUtil.round(value * this.maxIndex);
        if (index > this.maxIndex) {
            return this.maxIndex;
        }
        if (index < 0) {
            return 0;
        }
        return index;
    }
    
    public static DataSpec<LegacyTerrace> spec() {
        return Modifier.specBuilder(LegacyTerrace.class, LegacyTerrace.factory).add("steps", (Object)1, s -> s.steps.length).add("blend_range", (Object)1, s -> s.blend).addObj("source", Module.class, s -> s.source).addObj("lower_curve", Module.class, s -> s.lowerCurve).addObj("upper_curve", Module.class, s -> s.upperCurve).build();
    }
    
    static {
        factory = ((data, spec, context) -> new LegacyTerrace(spec.get("source", data, Module.class, context), spec.get("lower_curve", data, Module.class, context), spec.get("upper_curve", data, Module.class, context), spec.get("steps", data, DataValue::asInt), spec.get("blend_range", data, DataValue::asFloat)));
    }
    
    private static class Step
    {
        private final float value;
        private final float lowerBound;
        private final float upperBound;
        
        private Step(final float value, final float distance, final float blendRange) {
            this.value = value;
            final float blend = distance * blendRange;
            final float bound = (distance - blend) / 2.0f;
            this.lowerBound = value - bound;
            this.upperBound = value + bound;
        }
    }
}
