// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.modifier;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.value.DataValue;
import com.terraforged.noise.Module;
import com.terraforged.noise.util.NoiseUtil;

public class PowerCurve extends Modifier
{
    private final float min;
    private final float max;
    private final float mid;
    private final float range;
    private final float power;
    private static final DataFactory<PowerCurve> factory;
    
    public PowerCurve(final Module source, final float power) {
        super(source);
        final float min = source.minValue();
        final float max = source.maxValue();
        final float mid = min + (max - min) / 2.0f;
        this.power = power;
        this.min = mid - NoiseUtil.pow(mid - source.minValue(), power);
        this.max = mid + NoiseUtil.pow(source.maxValue() - mid, power);
        this.range = this.max - this.min;
        this.mid = this.min + this.range / 2.0f;
    }
    
    @Override
    public String getSpecName() {
        return "PowCurve";
    }
    
    @Override
    public float modify(final float x, final float y, float value) {
        if (value >= this.mid) {
            final float part = value - this.mid;
            value = this.mid + NoiseUtil.pow(part, this.power);
        }
        else {
            final float part = this.mid - value;
            value = this.mid - NoiseUtil.pow(part, this.power);
        }
        return NoiseUtil.map(value, this.min, this.max, this.range);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final PowerCurve that = (PowerCurve)o;
        return Float.compare(that.min, this.min) == 0 && Float.compare(that.max, this.max) == 0 && Float.compare(that.mid, this.mid) == 0 && Float.compare(that.range, this.range) == 0 && Float.compare(that.power, this.power) == 0;
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + ((this.min != 0.0f) ? Float.floatToIntBits(this.min) : 0);
        result = 31 * result + ((this.max != 0.0f) ? Float.floatToIntBits(this.max) : 0);
        result = 31 * result + ((this.mid != 0.0f) ? Float.floatToIntBits(this.mid) : 0);
        result = 31 * result + ((this.range != 0.0f) ? Float.floatToIntBits(this.range) : 0);
        result = 31 * result + ((this.power != 0.0f) ? Float.floatToIntBits(this.power) : 0);
        return result;
    }
    
    public static DataSpec<PowerCurve> spec() {
        return Modifier.sourceBuilder("PowCurve", PowerCurve.class, PowerCurve.factory).add("power", (Object)1.0f, p -> p.power).build();
    }
    
    static {
        factory = ((data, spec, context) -> new PowerCurve(spec.get("source", data, Module.class, context), spec.get("power", data, DataValue::asFloat)));
    }
}
