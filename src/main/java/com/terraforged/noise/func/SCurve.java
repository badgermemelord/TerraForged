// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.func;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.value.DataValue;
import com.terraforged.noise.util.NoiseUtil;

public class SCurve implements CurveFunc
{
    private final float lower;
    private final float upper;
    private static final DataFactory<SCurve> factory;
    
    public SCurve(final float lower, final float upper) {
        this.lower = lower;
        this.upper = ((upper < 0.0f) ? Math.max(-lower, upper) : upper);
    }
    
    @Override
    public String getSpecName() {
        return "SCurve";
    }
    
    @Override
    public float apply(final float value) {
        return NoiseUtil.pow(value, this.lower + this.upper * value);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final SCurve sCurve = (SCurve)o;
        return Float.compare(sCurve.lower, this.lower) == 0 && Float.compare(sCurve.upper, this.upper) == 0;
    }
    
    @Override
    public int hashCode() {
        int result = (this.lower != 0.0f) ? Float.floatToIntBits(this.lower) : 0;
        result = 31 * result + ((this.upper != 0.0f) ? Float.floatToIntBits(this.upper) : 0);
        return result;
    }
    
    public static DataSpec<SCurve> spec() {
        return DataSpec.builder("SCurve", SCurve.class, SCurve.factory).add("lower", (Object)0, s -> s.lower).add("upper", (Object)1, s -> s.upper).build();
    }
    
    static {
        factory = ((data, spec, context) -> new SCurve(spec.get("lower", data, DataValue::asFloat), spec.get("upper", data, DataValue::asFloat)));
    }
}
