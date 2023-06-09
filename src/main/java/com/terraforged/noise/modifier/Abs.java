// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.modifier;

import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.Module;

public class Abs extends Modifier
{
    public Abs(final Module source) {
        super(source);
    }
    
    @Override
    public String getSpecName() {
        return "Abs";
    }
    
    @Override
    public float modify(final float x, final float y, final float noiseValue) {
        return Math.abs(noiseValue);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    @Override
    public String toString() {
        return super.toString();
    }
    
    public static DataSpec<Abs> spec() {
        return Modifier.spec(Abs.class, Abs::new);
    }
}
