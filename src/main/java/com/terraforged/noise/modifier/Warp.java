// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.modifier;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.Module;
import com.terraforged.noise.domain.Domain;

public class Warp extends Modifier
{
    private final Domain domain;
    private static final DataFactory<Warp> factory;
    
    public Warp(final Module source, final Domain domain) {
        super(source);
        this.domain = domain;
    }
    
    @Override
    public String getSpecName() {
        return "Warp";
    }
    
    @Override
    public float getValue(final float x, final float y) {
        return this.source.getValue(this.domain.getX(x, y), this.domain.getY(x, y));
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
        final Warp warp = (Warp)o;
        return this.domain.equals(warp.domain);
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.domain.hashCode();
        return result;
    }
    
    public static DataSpec<Warp> spec() {
        return Modifier.sourceBuilder(Warp.class, Warp.factory).addObj("domain", Domain.class, m -> m.domain).build();
    }
    
    static {
        factory = ((data, spec, context) -> new Warp(spec.get("source", data, Module.class, context), spec.get("domain", data, Domain.class, context)));
    }
}
