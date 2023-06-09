// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.combiner;

import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.Module;

import java.util.function.Function;

public class Add extends Combiner
{
    public Add(final Module... modules) {
        super(modules);
    }
    
    @Override
    public String getSpecName() {
        return "Add";
    }
    
    @Override
    protected float minTotal(final float total, final Module next) {
        return total + next.minValue();
    }
    
    @Override
    protected float maxTotal(final float total, final Module next) {
        return total + next.maxValue();
    }
    
    @Override
    protected float combine(final float total, final float value) {
        return total + value;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    public static DataSpec<?> spec() {
        return Combiner.spec("Add", (Function<Module[], Combiner>)Add::new);
    }
}
