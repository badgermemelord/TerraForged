//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.noise.source;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.value.DataValue;
import com.terraforged.noise.Module;

public class Constant implements Module {
    private final float value;
    private static final DataFactory<Constant> factory = (data, spec, context) -> new Constant(
            new Builder().frequency(spec.get("value", data, DataValue::asDouble))
    );

    public Constant(Builder builder) {
        this.value = builder.getFrequency();
    }

    public Constant(float value) {
        this.value = value;
    }

    public String getSpecName() {
        return "Const";
    }

    public float getValue(float x, float y) {
        return this.value;
    }

    public float minValue() {
        return this.value;
    }

    public float maxValue() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Constant constant = (Constant)o;
            return Float.compare(constant.value, this.value) == 0;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.value != 0.0F ? Float.floatToIntBits(this.value) : 0;
    }

    public static DataSpec<Constant> spec() {
        return DataSpec.builder("Const", Constant.class, factory).add("value", 1.0F, c -> c.value).build();
    }
}
