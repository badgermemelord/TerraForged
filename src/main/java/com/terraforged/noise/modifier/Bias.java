// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.modifier;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.value.DataValue;
import com.terraforged.noise.Module;
import com.terraforged.noise.Source;

public class Bias extends Modifier
{
    private final Module bias;
    private static final DataFactory<Bias> factory;
    
    public Bias(final Module source, final float bias) {
        this(source, Source.constant(bias));
    }
    
    public Bias(final Module source, final Module bias) {
        super(source);
        this.bias = bias;
    }
    
    @Override
    public String getSpecName() {
        return "Bias";
    }
    
    @Override
    public float minValue() {
        return super.minValue() + this.bias.minValue();
    }
    
    @Override
    public float maxValue() {
        return super.maxValue() + this.bias.maxValue();
    }
    
    @Override
    public float modify(final float x, final float y, final float noiseValue) {
        return noiseValue + this.bias.getValue(x, y);
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
        final Bias bias1 = (Bias)o;
        return this.bias.equals(bias1.bias);
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.bias.hashCode();
        return result;
    }
    
    public static DataSpec<Bias> spec() {
        return Modifier.sourceBuilder(Bias.class, Bias.factory).addObj("bias", Module.class, b -> b.bias).build();
    }
    
    static {
        factory = ((data, spec, context) -> new Bias(spec.get("source", data, Module.class, context), spec.get("bias", data, DataValue::asFloat)));
    }
}
