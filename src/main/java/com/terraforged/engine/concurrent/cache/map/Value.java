// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.concurrent.cache.map;

public class Value
{
    public final int id;
    
    public Value(final int id) {
        this.id = id;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Value value = (Value)o;
        return this.id == value.id;
    }
    
    @Override
    public int hashCode() {
        return this.id;
    }
}
