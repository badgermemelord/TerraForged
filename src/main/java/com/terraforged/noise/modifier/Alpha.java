// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.modifier;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.Module;

public class Alpha extends Modifier
{
    private final Module alpha;
    private static final DataFactory<Alpha> factory;
    
    public Alpha(final Module source, final Module alpha) {
        super(source);
        this.alpha = alpha;
    }
    
    @Override
    public String getSpecName() {
        return "Alpha";
    }
    
    @Override
    public float modify(final float x, final float y, final float noiseValue) {
        final float a = this.alpha.getValue(x, y);
        return noiseValue * a + (1.0f - a);
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
        final Alpha alpha1 = (Alpha)o;
        return this.alpha.equals(alpha1.alpha);
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.alpha.hashCode();
        return result;
    }
    
    public static DataSpec<Alpha> spec() {
        return Modifier.sourceBuilder(Alpha.class, Alpha.factory).addObj("alpha", Module.class, a -> a.alpha).build();
    }
    
    static {
        factory = ((data, spec, context) -> new Alpha(spec.get("source", data, Module.class, context), spec.get("alpha", data, Module.class, context)));
    }
}
