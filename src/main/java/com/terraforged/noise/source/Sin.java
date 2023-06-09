// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.source;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.value.DataValue;
import com.terraforged.noise.Module;
import com.terraforged.noise.util.NoiseUtil;

public class Sin extends NoiseSource
{
    private final Module alpha;
    private static final DataFactory<Sin> factory;
    
    public Sin(final Builder builder) {
        super(builder);
        this.alpha = builder.getSource();
    }
    
    @Override
    public String getSpecName() {
        return "Sin";
    }
    
    @Override
    public float getValue(float x, float y, final int seed) {
        final float a = this.alpha.getValue(x, y);
        x *= this.frequency;
        y *= this.frequency;
        float noise;
        if (a == 0.0f) {
            noise = NoiseUtil.sin(x);
        }
        else if (a == 1.0f) {
            noise = NoiseUtil.sin(y);
        }
        else {
            final float sx = NoiseUtil.sin(x);
            final float sy = NoiseUtil.sin(y);
            noise = NoiseUtil.lerp(sx, sy, a);
        }
        return NoiseUtil.map(noise, -1.0f, 1.0f, 2.0f);
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
        final Sin sin = (Sin)o;
        return this.alpha.equals(sin.alpha);
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.alpha.hashCode();
        return result;
    }
    
    public static DataSpec<Sin> spec() {
        return DataSpec.builder("Sin", Sin.class, Sin.factory).add("frequency", (Object)1.0f, s -> s.frequency).addObj("alpha", s -> s.alpha).build();
    }
    
    static {
        final Sin sin;
        factory = ((data, spec, context) -> {
            new Sin(new Builder().frequency(spec.get("frequency", data, DataValue::asDouble)).source(spec.get("alpha", data, Module.class, context)));
            return sin;
        });
    }
}
