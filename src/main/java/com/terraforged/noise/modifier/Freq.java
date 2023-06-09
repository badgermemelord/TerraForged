// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.modifier;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.Module;

public class Freq extends Modifier
{
    private final Module x;
    private final Module y;
    private static final DataFactory<Freq> factory;
    
    public Freq(final Module source, final Module x, final Module y) {
        super(source);
        this.x = x;
        this.y = y;
    }
    
    @Override
    public String getSpecName() {
        return "Freq";
    }
    
    @Override
    public float getValue(final float x, final float y) {
        final float fx = this.x.getValue(x, y);
        final float fy = this.y.getValue(x, y);
        return this.source.getValue(x * fx, y * fy);
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
        final Freq freq = (Freq)o;
        return this.x.equals(freq.x) && this.y.equals(freq.y);
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.x.hashCode();
        result = 31 * result + this.y.hashCode();
        return result;
    }
    
    public static DataSpec<Freq> spec() {
        return Modifier.sourceBuilder(Freq.class, Freq.factory).addObj("x", Module.class, f -> f.x).addObj("y", Module.class, f -> f.y).build();
    }
    
    static {
        factory = ((data, spec, context) -> new Freq(spec.get("source", data, Module.class, context), spec.get("x", data, Module.class, context), spec.get("y", data, Module.class, context)));
    }
}
