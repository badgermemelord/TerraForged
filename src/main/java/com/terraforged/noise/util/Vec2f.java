//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.noise.util;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.spec.SpecName;
import com.terraforged.cereal.value.DataValue;
import java.util.Objects;

public class Vec2f implements SpecName {
    public static final Vec2f ZERO = new Vec2f(0.0F, 0.0F);
    public final float x;
    public final float y;
    private static final DataFactory<Vec2f> factory = (data, spec, context) -> new Vec2f(
            spec.get("x", data, DataValue::asFloat), spec.get("y", data, DataValue::asFloat)
    );

    public Vec2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public int getBlockX() {
        return (int)this.x;
    }

    public int getBlockY() {
        return (int)this.y;
    }

    public Vec2f add(float x, float y) {
        return new Vec2f(this.x + x, this.y + y);
    }

    public float dist2(float x, float y) {
        float dx = this.x - x;
        float dy = this.y - y;
        return dx * dx + dy * dy;
    }

    public Vec2i toInt() {
        return new Vec2i(this.getBlockX(), this.getBlockY());
    }

    public String getSpecName() {
        return "Vec2f";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Vec2f vec2f = (Vec2f)o;
            return Float.compare(vec2f.x, this.x) == 0 && Float.compare(vec2f.y, this.y) == 0;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.x, this.y});
    }

    public static DataSpec<Vec2f> spec() {
        return DataSpec.builder(Vec2f.class, factory).add("x", 0.0F, v -> v.x).add("y", 0.0F, v -> v.y).build();
    }
}
