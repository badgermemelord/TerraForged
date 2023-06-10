//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.noise.source;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.value.DataValue;
import com.terraforged.noise.Module;
import com.terraforged.noise.util.NoiseUtil;

public class Sin extends NoiseSource {
    private final Module alpha;
    private static final DataFactory<Sin> factory = (data, spec, context) -> new Sin(
            new Builder().frequency(spec.get("frequency", data, DataValue::asDouble)).source((Module)spec.get("alpha", data, Module.class, context))
    );

    public Sin(Builder builder) {
        super(builder);
        this.alpha = builder.getSource();
    }

    public String getSpecName() {
        return "Sin";
    }

    public float getValue(float x, float y, int seed) {
        float a = this.alpha.getValue(x, y);
        x *= this.frequency;
        y *= this.frequency;
        float noise;
        if (a == 0.0F) {
            noise = NoiseUtil.sin(x);
        } else if (a == 1.0F) {
            noise = NoiseUtil.sin(y);
        } else {
            float sx = NoiseUtil.sin(x);
            float sy = NoiseUtil.sin(y);
            noise = NoiseUtil.lerp(sx, sy, a);
        }

        return NoiseUtil.map(noise, -1.0F, 1.0F, 2.0F);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null || this.getClass() != o.getClass()) {
            return false;
        } else if (!super.equals(o)) {
            return false;
        } else {
            Sin sin = (Sin)o;
            return this.alpha.equals(sin.alpha);
        }
    }

    public int hashCode() {
        int result = super.hashCode();
        return 31 * result + this.alpha.hashCode();
    }

    public static DataSpec<Sin> spec() {
        return DataSpec.builder("Sin", Sin.class, factory).add("frequency", 1.0F, s -> s.frequency).addObj("alpha", s -> s.alpha).build();
    }
}
