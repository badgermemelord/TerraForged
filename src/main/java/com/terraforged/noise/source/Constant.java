// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.source;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.value.DataValue;
import com.terraforged.noise.Module;

public class Constant implements Module
{
    private final float value;
    private static final DataFactory<Constant> factory;
    
    public Constant(final Builder builder) {
        this.value = builder.getFrequency();
    }
    
    public Constant(final float value) {
        this.value = value;
    }
    
    @Override
    public String getSpecName() {
        return "Const";
    }
    
    @Override
    public float getValue(final float x, final float y) {
        return this.value;
    }
    
    @Override
    public float minValue() {
        return this.value;
    }
    
    @Override
    public float maxValue() {
        return this.value;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Constant constant = (Constant)o;
        return Float.compare(constant.value, this.value) == 0;
    }
    
    @Override
    public int hashCode() {
        return (this.value != 0.0f) ? Float.floatToIntBits(this.value) : 0;
    }
    
    public static DataSpec<Constant> spec() {
        return DataSpec.builder("Const", Constant.class, Constant.factory).add("value", (Object)1.0f, c -> c.value).build();
    }
    
    static {
        final Constant constant;
        factory = ((data, spec, context) -> {
            new Constant(new Builder().frequency(spec.get("value", data, DataValue::asDouble)));
            return constant;
        });
    }
}
