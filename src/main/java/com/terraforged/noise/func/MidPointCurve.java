// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.func;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.value.DataValue;
import com.terraforged.noise.util.NoiseUtil;

public class MidPointCurve implements CurveFunc
{
    private final float mid;
    private final float steepness;
    private static final DataFactory<MidPointCurve> factory;
    
    public MidPointCurve(final float mid, final float steepness) {
        this.mid = mid;
        this.steepness = steepness;
    }
    
    @Override
    public String getSpecName() {
        return "MidCurve";
    }
    
    @Override
    public float apply(final float value) {
        return NoiseUtil.curve(value, this.mid, this.steepness);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final MidPointCurve that = (MidPointCurve)o;
        return Float.compare(that.mid, this.mid) == 0 && Float.compare(that.steepness, this.steepness) == 0;
    }
    
    @Override
    public int hashCode() {
        int result = (this.mid != 0.0f) ? Float.floatToIntBits(this.mid) : 0;
        result = 31 * result + ((this.steepness != 0.0f) ? Float.floatToIntBits(this.steepness) : 0);
        return result;
    }
    
    public static DataSpec<MidPointCurve> spec() {
        return DataSpec.builder("MidCurve", MidPointCurve.class, MidPointCurve.factory).add("midpoint", (Object)0.5f, m -> m.mid).add("steepness", (Object)4, m -> m.steepness).build();
    }
    
    static {
        factory = ((data, spec, context) -> new MidPointCurve(spec.get("midpoint", data, DataValue::asFloat), spec.get("steepness", data, DataValue::asFloat)));
    }
}
