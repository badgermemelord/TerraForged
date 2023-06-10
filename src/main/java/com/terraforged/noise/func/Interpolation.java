//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.noise.func;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.util.NoiseUtil;
import java.util.function.Function;

public enum Interpolation implements CurveFunc {
    LINEAR {
        @Override
        public float apply(float f) {
            return f;
        }
    },
    CURVE3 {
        @Override
        public float apply(float f) {
            return NoiseUtil.interpHermite(f);
        }
    },
    CURVE4 {
        @Override
        public float apply(float f) {
            return NoiseUtil.interpQuintic(f);
        }
    };

    private static final DataFactory<Interpolation> factory = (data, spec, context) -> (Interpolation)spec.getEnum("mode", data, Interpolation.class);

    private Interpolation() {
    }

    public String getSpecName() {
        return "Interpolation";
    }

    public abstract float apply(float var1);

    public static DataSpec<Interpolation> spec() {
        return DataSpec.builder("Interpolation", Interpolation.class, factory).add("mode", LINEAR, Function.identity()).build();
    }
}
