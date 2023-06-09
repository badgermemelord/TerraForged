// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.modifier;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.Module;
import com.terraforged.noise.util.NoiseUtil;

public class Modulate extends Modifier
{
    private final Module direction;
    private final Module strength;
    private static final DataFactory<Modulate> factory;
    
    public Modulate(final Module source, final Module direction, final Module strength) {
        super(source);
        this.direction = direction;
        this.strength = strength;
    }
    
    @Override
    public String getSpecName() {
        return "Modulate";
    }
    
    @Override
    public float getValue(final float x, final float y) {
        final float angle = this.direction.getValue(x, y) * 6.2831855f;
        final float strength = this.strength.getValue(x, y);
        final float dx = NoiseUtil.sin(angle) * strength;
        final float dy = NoiseUtil.cos(angle) * strength;
        return this.source.getValue(x + dx, y + dy);
    }
    
    @Override
    public float modify(final float x, final float y, final float noiseValue) {
        return 0.0f;
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
        final Modulate modulate = (Modulate)o;
        return this.direction.equals(modulate.direction) && this.strength.equals(modulate.strength);
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.direction.hashCode();
        result = 31 * result + this.strength.hashCode();
        return result;
    }
    
    public static DataSpec<Modulate> spec() {
        return Modifier.sourceBuilder(Modulate.class, Modulate.factory).addObj("direction", Module.class, m -> m.direction).addObj("strength", Module.class, m -> m.strength).build();
    }
    
    static {
        factory = ((data, spec, context) -> new Modulate(spec.get("source", data, Module.class, context), spec.get("direction", data, Module.class, context), spec.get("strength", data, Module.class, context)));
    }
}
