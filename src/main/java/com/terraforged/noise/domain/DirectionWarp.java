// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.domain;

import com.terraforged.cereal.spec.Context;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.value.DataObject;
import com.terraforged.noise.Module;
import com.terraforged.noise.util.NoiseUtil;

public class DirectionWarp implements Domain
{
    private final Module direction;
    private final Module strength;
    
    public DirectionWarp(final Module direction, final Module strength) {
        this.direction = direction;
        this.strength = strength;
    }
    
    @Override
    public String getSpecName() {
        return "DirectionWarp";
    }
    
    @Override
    public float getOffsetX(final float x, final float y) {
        final float angle = this.direction.getValue(x, y) * 6.2831855f;
        return NoiseUtil.sin(angle) * this.strength.getValue(x, y);
    }
    
    @Override
    public float getOffsetY(final float x, final float y) {
        final float angle = this.direction.getValue(x, y) * 6.2831855f;
        return NoiseUtil.cos(angle) * this.strength.getValue(x, y);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final DirectionWarp that = (DirectionWarp)o;
        return this.direction.equals(that.direction) && this.strength.equals(that.strength);
    }
    
    @Override
    public int hashCode() {
        int result = this.direction.hashCode();
        result = 31 * result + this.strength.hashCode();
        return result;
    }
    
    private static DirectionWarp create(final DataObject data, final DataSpec<?> spec, final Context context) {
        return new DirectionWarp(spec.get("direction", data, Module.class, context), spec.get("strength", data, Module.class, context));
    }
    
    public static DataSpec<? extends Domain> spec() {
        return DataSpec.builder("DirectionWarp", DirectionWarp.class, DirectionWarp::create).addObj("direction", Module.class, w -> w.direction).addObj("strength", Module.class, w -> w.strength).build();
    }
}
