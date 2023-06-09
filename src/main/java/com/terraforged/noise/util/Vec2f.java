// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.util;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.spec.SpecName;
import com.terraforged.cereal.value.DataValue;

import java.util.Objects;

public class Vec2f implements SpecName
{
    public static final Vec2f ZERO;
    public final float x;
    public final float y;
    private static final DataFactory<Vec2f> factory;
    
    public Vec2f(final float x, final float y) {
        this.x = x;
        this.y = y;
    }
    
    public int getBlockX() {
        return (int)this.x;
    }
    
    public int getBlockY() {
        return (int)this.y;
    }
    
    public Vec2f add(final float x, final float y) {
        return new Vec2f(this.x + x, this.y + y);
    }
    
    public float dist2(final float x, final float y) {
        final float dx = this.x - x;
        final float dy = this.y - y;
        return dx * dx + dy * dy;
    }
    
    public Vec2i toInt() {
        return new Vec2i(this.getBlockX(), this.getBlockY());
    }
    
    @Override
    public String getSpecName() {
        return "Vec2f";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Vec2f vec2f = (Vec2f)o;
        return Float.compare(vec2f.x, this.x) == 0 && Float.compare(vec2f.y, this.y) == 0;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }
    
    public static DataSpec<Vec2f> spec() {
        return DataSpec.builder(Vec2f.class, Vec2f.factory).add("x", (Object)0.0f, v -> v.x).add("y", (Object)0.0f, v -> v.y).build();
    }
    
    static {
        ZERO = new Vec2f(0.0f, 0.0f);
        factory = ((data, spec, context) -> new Vec2f(spec.get("x", data, DataValue::asFloat), spec.get("y", data, DataValue::asFloat)));
    }
}
