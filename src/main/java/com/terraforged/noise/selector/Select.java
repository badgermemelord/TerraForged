// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.selector;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.value.DataValue;
import com.terraforged.noise.Module;
import com.terraforged.noise.func.Interpolation;

public class Select extends Selector
{
    protected final Module control;
    protected final Module source0;
    protected final Module source1;
    protected final float lowerBound;
    protected final float upperBound;
    protected final float edgeFalloff;
    protected final float lowerCurveMin;
    protected final float lowerCurveMax;
    protected final float lowerCurveRange;
    protected final float upperCurveMin;
    protected final float upperCurveMax;
    protected final float upperCurveRange;
    private static final DataFactory<Select> factory;
    
    public Select(final Module control, final Module source0, final Module source1, final float lowerBound, final float upperBound, final float edgeFalloff, final Interpolation interpolation) {
        super(control, new Module[] { source0, source1 }, interpolation);
        this.control = control;
        this.source0 = source0;
        this.source1 = source1;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.edgeFalloff = edgeFalloff;
        this.lowerCurveMin = lowerBound - edgeFalloff;
        this.lowerCurveMax = lowerBound + edgeFalloff;
        this.lowerCurveRange = this.lowerCurveMax - this.lowerCurveMin;
        this.upperCurveMin = upperBound - edgeFalloff;
        this.upperCurveMax = upperBound + edgeFalloff;
        this.upperCurveRange = this.upperCurveMax - this.upperCurveMin;
    }
    
    @Override
    public String getSpecName() {
        return "Select";
    }
    
    public float selectValue(final float x, final float y, final float value) {
        if (this.edgeFalloff == 0.0f) {
            if (value < this.lowerCurveMax) {
                return this.source0.getValue(x, y);
            }
            if (value > this.upperCurveMin) {
                return this.source1.getValue(x, y);
            }
            return this.source0.getValue(x, y);
        }
        else {
            if (value < this.lowerCurveMin) {
                return this.source0.getValue(x, y);
            }
            if (value < this.lowerCurveMax) {
                final float alpha = (value - this.lowerCurveMin) / this.lowerCurveRange;
                return this.blendValues(this.source0.getValue(x, y), this.source1.getValue(x, y), alpha);
            }
            if (value < this.upperCurveMin) {
                return this.source1.getValue(x, y);
            }
            if (value < this.upperCurveMax) {
                final float alpha = (value - this.upperCurveMin) / this.upperCurveRange;
                return this.blendValues(this.source1.getValue(x, y), this.source0.getValue(x, y), alpha);
            }
            return this.source0.getValue(x, y);
        }
    }
    
    public static DataSpec<Select> spec() {
        return DataSpec.builder(Select.class, Select.factory).add("lower_bound", (Object)0, s -> s.lowerBound).add("upper_bound", (Object)1, s -> s.upperBound).add("falloff", (Object)0, s -> s.edgeFalloff).add("interp", (Object)Interpolation.LINEAR, s -> s.interpolation).addObj("control", Module.class, s -> s.selector).addObj("lower", Module.class, s -> s.source0).addObj("upper", Module.class, s -> s.source1).build();
    }
    
    static {
        factory = ((data, spec, context) -> new Select(spec.get("control", data, Module.class, context), spec.get("lower", data, Module.class, context), spec.get("upper", data, Module.class, context), spec.get("lower_bound", data, DataValue::asFloat), spec.get("upper_bound", data, DataValue::asFloat), spec.get("falloff", data, DataValue::asFloat), spec.get("interp", data, v -> v.asEnum(Interpolation.class))));
    }
}
