// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.modifier;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.value.DataValue;
import com.terraforged.noise.Module;
import com.terraforged.noise.util.NoiseUtil;

import java.util.Arrays;

public class Terrace extends Modifier
{
    private static final float MIN_NOISE_VALUE = 0.0f;
    private static final float MAX_NOISE_VALUE = 0.999999f;
    private final float blend;
    private final float length;
    private final int maxIndex;
    private final Step[] steps;
    private final Module ramp;
    private final Module cliff;
    private final Module rampHeight;
    private static final DataFactory<Terrace> factory;
    
    public Terrace(final Module source, final Module ramp, final Module cliff, final Module rampHeight, final int steps, final float blendRange) {
        super(source);
        this.blend = blendRange;
        this.maxIndex = steps - 1;
        this.steps = new Step[steps];
        this.length = steps * 0.999999f;
        this.ramp = ramp;
        this.cliff = cliff;
        this.rampHeight = rampHeight;
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
        return "Terrace";
    }
    
    @Override
    public float getValue(final float x, final float y) {
        float value = this.source.getValue(x, y);
        value = NoiseUtil.clamp(value, 0.0f, 0.999999f);
        return this.modify(x, y, value);
    }
    
    @Override
    public float modify(final float x, final float y, final float noiseValue) {
        final int index = NoiseUtil.floor(noiseValue * this.steps.length);
        final Step step = this.steps[index];
        if (index == this.maxIndex) {
            return step.value;
        }
        if (noiseValue < step.lowerBound) {
            return step.value;
        }
        if (noiseValue > step.upperBound) {
            final Step next = this.steps[index + 1];
            return next.value;
        }
        final float ramp = 1.0f - this.ramp.getValue(x, y) * 0.5f;
        final float cliff = 1.0f - this.cliff.getValue(x, y) * 0.5f;
        final float alpha = (noiseValue - step.lowerBound) / (step.upperBound - step.lowerBound);
        float value = step.value;
        if (alpha > ramp) {
            final Step next2 = this.steps[index + 1];
            final float rampSize = 1.0f - ramp;
            final float rampAlpha = (alpha - ramp) / rampSize;
            final float rampHeight = this.rampHeight.getValue(x, y);
            value += (next2.value - value) * rampAlpha * rampHeight;
        }
        if (alpha > cliff) {
            final Step next2 = this.steps[index + 1];
            final float cliffAlpha = (alpha - cliff) / (1.0f - cliff);
            value = NoiseUtil.lerp(value, next2.value, cliffAlpha);
        }
        return value;
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
        final Terrace terrace = (Terrace)o;
        return Float.compare(terrace.blend, this.blend) == 0 && Float.compare(terrace.length, this.length) == 0 && this.maxIndex == terrace.maxIndex && Arrays.equals(this.steps, terrace.steps) && this.ramp.equals(terrace.ramp) && this.cliff.equals(terrace.cliff) && this.rampHeight.equals(terrace.rampHeight);
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + ((this.blend != 0.0f) ? Float.floatToIntBits(this.blend) : 0);
        result = 31 * result + ((this.length != 0.0f) ? Float.floatToIntBits(this.length) : 0);
        result = 31 * result + this.maxIndex;
        result = 31 * result + Arrays.hashCode(this.steps);
        result = 31 * result + this.ramp.hashCode();
        result = 31 * result + this.cliff.hashCode();
        result = 31 * result + this.rampHeight.hashCode();
        return result;
    }
    
    public static DataSpec<Terrace> spec() {
        return Modifier.specBuilder(Terrace.class, Terrace.factory).add("steps", (Object)1, s -> s.steps.length).add("blend_range", (Object)1, s -> s.blend).addObj("source", Module.class, s -> s.source).addObj("ramp", Module.class, s -> s.ramp).addObj("cliff", Module.class, s -> s.cliff).addObj("ramp_height", Module.class, s -> s.rampHeight).build();
    }
    
    static {
        factory = ((data, spec, context) -> new Terrace(spec.get("source", data, Module.class, context), spec.get("ramp", data, Module.class, context), spec.get("cliff", data, Module.class, context), spec.get("ramp_height", data, Module.class, context), spec.get("steps", data, DataValue::asInt), spec.get("blend_range", data, DataValue::asFloat)));
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
