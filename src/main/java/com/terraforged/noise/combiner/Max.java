// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.combiner;

import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.Module;

import java.util.function.Function;

public class Max extends Combiner
{
    public Max(final Module... modules) {
        super(modules);
    }
    
    @Override
    public String getSpecName() {
        return "Max";
    }
    
    @Override
    protected float minTotal(final float total, final Module next) {
        return this.maxTotal(total, next);
    }
    
    @Override
    protected float maxTotal(final float total, final Module next) {
        return Math.max(total, next.maxValue());
    }
    
    @Override
    protected float combine(final float total, final float value) {
        return Math.max(total, value);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    public static DataSpec<?> spec() {
        return Combiner.spec("Max", (Function<Module[], Combiner>)Max::new);
    }
}
