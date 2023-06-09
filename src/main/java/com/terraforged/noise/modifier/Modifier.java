// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.modifier;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.Module;

import java.util.function.Function;

public abstract class Modifier implements Module
{
    protected final Module source;
    
    public Modifier(final Module source) {
        this.source = source;
    }
    
    @Override
    public float getValue(final float x, final float y) {
        final float value = this.source.getValue(x, y);
        return this.modify(x, y, value);
    }
    
    @Override
    public float minValue() {
        return this.source.minValue();
    }
    
    @Override
    public float maxValue() {
        return this.source.maxValue();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Modifier modifier = (Modifier)o;
        return this.source.equals(modifier.source);
    }
    
    @Override
    public int hashCode() {
        return this.source.hashCode();
    }
    
    @Override
    public String toString() {
        return "Modifier{source=" + this.source + '}';
    }
    
    public abstract float modify(final float p0, final float p1, final float p2);
    
    protected static <M extends Modifier> DataSpec.Builder<M> specBuilder(final Class<M> type, final DataFactory<M> factory) {
        return DataSpec.builder(type.getSimpleName(), type, factory);
    }
    
    protected static <M extends Modifier> DataSpec.Builder<M> sourceBuilder(final Class<M> type, final DataFactory<M> factory) {
        return sourceBuilder(type.getSimpleName(), type, factory);
    }
    
    protected static <M extends Modifier> DataSpec.Builder<M> sourceBuilder(final String name, final Class<M> type, final DataFactory<M> factory) {
        return DataSpec.builder(name, type, factory).addObj("source", Module.class, m -> m.source);
    }
    
    public static <M extends Modifier> DataSpec<M> spec(final Class<M> type, final Function<Module, M> constructor) {
        final DataFactory<M> factory = (data, spec, context) -> constructor.apply(spec.get("source", data, Module.class, context));
        return sourceBuilder(type, factory).build();
    }
}
