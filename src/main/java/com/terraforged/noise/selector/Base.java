// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.selector;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.value.DataValue;
import com.terraforged.noise.Module;
import com.terraforged.noise.func.Interpolation;

public class Base extends Selector
{
    private final Module base;
    protected final float min;
    protected final float max;
    protected final float maxValue;
    protected final float falloff;
    private static final DataFactory<Base> factory;
    
    public Base(final Module base, final Module source, final float falloff, final Interpolation interpolation) {
        super(source, new Module[] { base, source }, interpolation);
        this.base = base;
        this.min = base.maxValue();
        this.max = base.maxValue() + falloff;
        this.falloff = falloff;
        this.maxValue = Math.max(base.maxValue(), source.maxValue());
    }
    
    @Override
    public String getSpecName() {
        return "Base";
    }
    
    @Override
    protected float selectValue(final float x, final float y, final float upperValue) {
        if (upperValue >= this.max) {
            return upperValue;
        }
        final float lowerValue = this.base.getValue(x, y);
        if (this.falloff > 0.0f) {
            final float clamp = Math.max(this.min, upperValue);
            final float alpha = (this.max - clamp) / this.falloff;
            return this.blendValues(upperValue, lowerValue, alpha);
        }
        return lowerValue;
    }
    
    @Override
    public float minValue() {
        return this.base.minValue();
    }
    
    @Override
    public float maxValue() {
        return this.maxValue;
    }
    
    public static DataSpec<Base> spec() {
        return DataSpec.builder("Base", Base.class, Base.factory).add("falloff", (Object)0, b -> b.falloff).add("interpolation", (Object)Interpolation.LINEAR, b -> b.interpolation).addObj("control", Module.class, b -> b.selector).addObj("base", Module.class, b -> b.base).build();
    }
    
    static {
        factory = ((data, spec, context) -> new Base(spec.get("base", data, Module.class, context), spec.get("control", data, Module.class, context), spec.get("falloff", data, DataValue::asFloat), spec.get("interpolation", data, v -> v.asEnum(Interpolation.class))));
    }
}
