// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.util;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.spec.SpecName;
import com.terraforged.cereal.value.DataValue;

import java.util.Objects;

public class Vec2i implements SpecName
{
    public final int x;
    public final int y;
    private static final DataFactory<Vec2i> factory;
    
    public Vec2i(final int x, final int y) {
        this.x = x;
        this.y = y;
    }
    
    public float dist2(final int x, final int y) {
        final int dx = this.x - x;
        final int dy = this.y - y;
        return (float)(dx * dx + dy * dy);
    }
    
    @Override
    public String getSpecName() {
        return "Vec2i";
    }
    
    @Override
    public String toString() {
        return "Vec2i{x=" + this.x + ", y=" + this.y + '}';
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Vec2i vec2i = (Vec2i)o;
        return this.x == vec2i.x && this.y == vec2i.y;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }
    
    public static DataSpec<Vec2i> spec() {
        return DataSpec.builder(Vec2i.class, Vec2i.factory).add("x", (Object)0, v -> v.x).add("y", (Object)0, v -> v.y).build();
    }
    
    static {
        factory = ((data, spec, context) -> new Vec2i(spec.get("x", data, DataValue::asInt), spec.get("y", data, DataValue::asInt)));
    }
}
