// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.modifier;

import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.Module;

public class Cache extends Modifier
{
    private final Value value;
    
    public Cache(final Module source) {
        super(source);
        this.value = new Value();
    }
    
    @Override
    public String getSpecName() {
        return "Cache";
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
    public float modify(final float x, final float y, final float noiseValue) {
        return 0.0f;
    }
    
    @Override
    public float getValue(final float x, final float y) {
        final Value value = this.value;
        if (value.matches(x, y)) {
            return value.value;
        }
        return value.set(x, y, this.source.getValue(x, y));
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
        final Cache cache = (Cache)o;
        return this.value.equals(cache.value);
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.value.hashCode();
        return result;
    }
    
    public static DataSpec<Cache> spec() {
        return Modifier.spec(Cache.class, Cache::new);
    }
    
    private static class Value
    {
        private float x;
        private float y;
        private float value;
        private boolean empty;
        
        private Value() {
            this.x = 0.0f;
            this.y = 0.0f;
            this.value = 0.0f;
            this.empty = true;
        }
        
        private boolean matches(final float x, final float y) {
            return !this.empty && x == this.x && y == this.y;
        }
        
        private float set(final float x, final float y, final float value) {
            this.x = x;
            this.y = y;
            this.value = value;
            this.empty = false;
            return value;
        }
    }
}
