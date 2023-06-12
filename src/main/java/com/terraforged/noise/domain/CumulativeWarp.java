// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.domain;

import com.terraforged.cereal.spec.Context;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.value.DataObject;

public class CumulativeWarp implements Domain
{
    private final Domain a;
    private final Domain b;
    
    public CumulativeWarp(final Domain a, final Domain b) {
        this.a = a;
        this.b = b;
    }
    
    @Override
    public String getSpecName() {
        return "CumulativeWarp";
    }
    
    @Override
    public float getOffsetX(final float x, final float y) {
        final float ax = this.a.getX(x, y);
        final float ay = this.a.getY(x, y);
        return this.b.getX(ax, ay);
    }
    
    @Override
    public float getOffsetY(final float x, final float y) {
        final float ax = this.a.getX(x, y);
        final float ay = this.a.getY(x, y);
        return this.b.getY(ax, ay);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final CumulativeWarp that = (CumulativeWarp)o;
        return this.a.equals(that.a) && this.b.equals(that.b);
    }
    
    @Override
    public int hashCode() {
        int result = this.a.hashCode();
        result = 31 * result + this.b.hashCode();
        return result;
    }
    
    private static CumulativeWarp create(final DataObject data, final DataSpec<?> spec, final Context context) {
        return new CumulativeWarp(spec.get("warp_1", data, Domain.class, context), spec.get("warp_2", data, Domain.class, context));
    }
    
    public static DataSpec<? extends Domain> spec() {
        return DataSpec.builder("CumulativeWarp", CumulativeWarp.class, CumulativeWarp::create).addObj("warp_1", Domain.class, w -> w.a).addObj("warp_2", Domain.class, w -> w.b).build();
    }
}