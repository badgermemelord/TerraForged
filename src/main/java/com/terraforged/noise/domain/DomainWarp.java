// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.domain;

import com.terraforged.cereal.spec.Context;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.value.DataObject;
import com.terraforged.noise.Module;

public class DomainWarp implements Domain
{
    private final Module x;
    private final Module y;
    private final Module distance;
    
    public DomainWarp(final Module x, final Module y, final Module distance) {
        this.x = map(x);
        this.y = map(y);
        this.distance = distance;
    }
    
    @Override
    public String getSpecName() {
        return "DomainWarp";
    }
    
    @Override
    public float getOffsetX(final float x, final float y) {
        return this.x.getValue(x, y) * this.distance.getValue(x, y);
    }
    
    @Override
    public float getOffsetY(final float x, final float y) {
        return this.y.getValue(x, y) * this.distance.getValue(x, y);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final DomainWarp that = (DomainWarp)o;
        return this.x.equals(that.x) && this.y.equals(that.y) && this.distance.equals(that.distance);
    }
    
    @Override
    public int hashCode() {
        int result = this.x.hashCode();
        result = 31 * result + this.y.hashCode();
        result = 31 * result + this.distance.hashCode();
        return result;
    }
    
    private static Module map(final Module in) {
        if (in.minValue() == -0.5f && in.maxValue() == 0.5f) {
            return in;
        }
        return in.map(-0.5, 0.5);
    }
    
    private static DomainWarp create(final DataObject data, final DataSpec<?> spec, final Context context) {
        return new DomainWarp(spec.get("x", data, Module.class, context), spec.get("y", data, Module.class, context), spec.get("distance", data, Module.class, context));
    }
    
    public static DataSpec<? extends Domain> spec() {
        return DataSpec.builder("DomainWarp", DomainWarp.class, DomainWarp::create).addObj("x", Module.class, w -> w.x).addObj("y", Module.class, w -> w.y).addObj("distance", Module.class, w -> w.distance).build();
    }
}
