// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.selector;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.value.DataValue;
import com.terraforged.noise.Module;
import com.terraforged.noise.func.Interpolation;

public class Blend extends Selector
{
    protected final Module source0;
    protected final Module source1;
    protected final float blend;
    protected final float midpoint;
    protected final float blendLower;
    protected final float blendUpper;
    protected final float blendRange;
    private static final DataFactory<Blend> factory;
    
    public Blend(final Module selector, final Module source0, final Module source1, final float midPoint, final float blendRange, final Interpolation interpolation) {
        super(selector, new Module[] { source0, source1 }, interpolation);
        final float mid = selector.minValue() + (selector.maxValue() - selector.minValue()) * midPoint;
        this.blend = blendRange;
        this.source0 = source0;
        this.source1 = source1;
        this.midpoint = midPoint;
        this.blendLower = Math.max(selector.minValue(), mid - blendRange / 2.0f);
        this.blendUpper = Math.min(selector.maxValue(), mid + blendRange / 2.0f);
        this.blendRange = this.blendUpper - this.blendLower;
    }
    
    @Override
    public String getSpecName() {
        return "Blend";
    }
    
    public float selectValue(final float x, final float y, final float select) {
        if (select < this.blendLower) {
            return this.source0.getValue(x, y);
        }
        if (select > this.blendUpper) {
            return this.source1.getValue(x, y);
        }
        final float alpha = (select - this.blendLower) / this.blendRange;
        return this.blendValues(this.source0.getValue(x, y), this.source1.getValue(x, y), alpha);
    }
    
    public static DataSpec<Blend> spec() {
        return DataSpec.builder(Blend.class, Blend.factory).add("midpoint", (Object)0.5f, b -> b.midpoint).add("blend_range", (Object)0.0f, b -> b.blend).add("interpolation", (Object)Interpolation.LINEAR, b -> b.interpolation).addObj("control", Module.class, b -> b.selector).addObj("lower", Module.class, b -> b.source0).addObj("upper", Module.class, b -> b.source1).build();
    }
    
    static {
        factory = ((data, spec, context) -> new Blend(spec.get("control", data, Module.class, context), spec.get("lower", data, Module.class, context), spec.get("upper", data, Module.class, context), spec.get("midpoint", data, DataValue::asFloat), spec.get("blend_range", data, DataValue::asFloat), spec.getEnum("interpolation", data, Interpolation.class)));
    }
}
