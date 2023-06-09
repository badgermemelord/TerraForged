// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.selector;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.value.DataValue;
import com.terraforged.noise.Module;
import com.terraforged.noise.func.Interpolation;

public class VariableBlend extends Selector
{
    private final Module source0;
    private final Module source1;
    private final Module variator;
    private final float midpoint;
    private final float maxBlend;
    private final float minBlend;
    private static final DataFactory<VariableBlend> factory;
    
    public VariableBlend(final Module control, final Module variator, final Module source0, final Module source1, final float midpoint, final float minBlend, final float maxBlend, final Interpolation interpolation) {
        super(control, new Module[] { source0, source1 }, interpolation);
        this.source0 = source0;
        this.source1 = source1;
        this.midpoint = midpoint;
        this.maxBlend = maxBlend;
        this.minBlend = minBlend;
        this.variator = variator;
    }
    
    @Override
    public String getSpecName() {
        return "VariBlend";
    }
    
    @Override
    protected float selectValue(final float x, final float y, final float selector) {
        final float radius = this.minBlend + this.variator.getValue(x, y) * this.maxBlend;
        final float min = Math.max(0.0f, this.midpoint - radius);
        if (selector < min) {
            return this.source0.getValue(x, y);
        }
        final float max = Math.min(1.0f, this.midpoint + radius);
        if (selector > max) {
            return this.source1.getValue(x, y);
        }
        final float alpha = (selector - min) / (max - min);
        return this.blendValues(this.source0.getValue(x, y), this.source1.getValue(x, y), alpha);
    }
    
    public static DataSpec<VariableBlend> spec() {
        return DataSpec.builder("VariBlend", VariableBlend.class, VariableBlend.factory).add("midpoint", (Object)0.5f, v -> v.midpoint).add("blend_min", (Object)0.0f, v -> v.minBlend).add("blend_max", (Object)1.0f, v -> v.maxBlend).add("interp", (Object)Interpolation.LINEAR, v -> v.interpolation).addObj("control", Module.class, v -> v.selector).addObj("variator", Module.class, v -> v.variator).addObj("lower", Module.class, v -> v.source0).addObj("upper", Module.class, v -> v.source1).build();
    }
    
    static {
        factory = ((data, spec, context) -> new VariableBlend(spec.get("control", data, Module.class, context), spec.get("variator", data, Module.class, context), spec.get("lower", data, Module.class, context), spec.get("upper", data, Module.class, context), spec.get("midpoint", data, DataValue::asFloat), spec.get("blend_min", data, DataValue::asFloat), spec.get("blend_max", data, DataValue::asFloat), spec.get("interp", data, v -> v.asEnum(Interpolation.class))));
    }
}
