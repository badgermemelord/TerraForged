// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.modifier;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.Module;
import com.terraforged.noise.util.NoiseUtil;

public class Grad extends Modifier
{
    private final Module lower;
    private final Module upper;
    private final Module strength;
    private static DataFactory<Grad> factory;
    
    public Grad(final Module source, final Module lower, final Module upper, final Module strength) {
        super(source);
        this.lower = lower;
        this.upper = upper;
        this.strength = strength;
    }
    
    @Override
    public String getSpecName() {
        return "Grad";
    }
    
    @Override
    public float modify(final float x, final float y, final float noiseValue) {
        final float upperBound = this.upper.getValue(x, y);
        if (noiseValue > upperBound) {
            return noiseValue;
        }
        final float amount = this.strength.getValue(x, y);
        final float lowerBound = this.lower.getValue(x, y);
        if (noiseValue < lowerBound) {
            return NoiseUtil.pow(noiseValue, 1.0f - amount);
        }
        final float alpha = 1.0f - (noiseValue - lowerBound) / (upperBound - lowerBound);
        final float power = 1.0f - amount * alpha;
        return NoiseUtil.pow(noiseValue, power);
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
        final Grad grad = (Grad)o;
        return this.lower.equals(grad.lower) && this.upper.equals(grad.upper) && this.strength.equals(grad.strength);
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.lower.hashCode();
        result = 31 * result + this.upper.hashCode();
        result = 31 * result + this.strength.hashCode();
        return result;
    }
    
    public static DataSpec<Grad> spec() {
        return Modifier.sourceBuilder(Grad.class, Grad.factory).addObj("lower", Module.class, g -> g.lower).addObj("upper", Module.class, g -> g.upper).addObj("strength", Module.class, g -> g.strength).build();
    }
    
    static {
        Grad.factory = ((data, spec, context) -> new Grad(spec.get("source", data, Module.class, context), spec.get("lower", data, Module.class, context), spec.get("upper", data, Module.class, context), spec.get("strength", data, Module.class, context)));
    }
}
