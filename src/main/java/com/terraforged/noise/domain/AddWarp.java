// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.domain;

import com.terraforged.cereal.spec.Context;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.value.DataObject;

public class AddWarp implements Domain
{
    private final Domain a;
    private final Domain b;
    
    public AddWarp(final Domain a, final Domain b) {
        this.a = a;
        this.b = b;
    }
    
    @Override
    public String getSpecName() {
        return "AddWarp";
    }
    
    @Override
    public float getOffsetX(final float x, final float y) {
        return this.a.getOffsetX(x, y) + this.b.getOffsetX(x, y);
    }
    
    @Override
    public float getOffsetY(final float x, final float y) {
        return this.a.getOffsetY(x, y) + this.b.getOffsetY(x, y);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final AddWarp addWarp = (AddWarp)o;
        return this.a.equals(addWarp.a) && this.b.equals(addWarp.b);
    }
    
    @Override
    public int hashCode() {
        int result = this.a.hashCode();
        result = 31 * result + this.b.hashCode();
        return result;
    }
    
    private static AddWarp create(final DataObject data, final DataSpec<?> spec, final Context context) {
        return new AddWarp(spec.get("warp_1", data, Domain.class, context), spec.get("warp_2", data, Domain.class, context));
    }
    
    public static DataSpec<? extends Domain> spec() {
        return DataSpec.builder("CumulativeWarp", AddWarp.class, AddWarp::create).addObj("warp_1", Domain.class, w -> w.a).addObj("warp_2", Domain.class, w -> w.b).build();
    }
}
