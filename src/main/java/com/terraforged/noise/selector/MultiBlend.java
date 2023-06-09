// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.selector;

import com.terraforged.cereal.Cereal;
import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.value.DataValue;
import com.terraforged.noise.Module;
import com.terraforged.noise.func.Interpolation;
import com.terraforged.noise.util.NoiseUtil;

import java.util.Arrays;

public class MultiBlend extends Selector
{
    private final Node[] nodes;
    private final int maxIndex;
    private final float blend;
    private final float blendRange;
    private static final DataFactory<MultiBlend> factory;
    
    public MultiBlend(final float blend, final Interpolation interpolation, final Module control, final Module... sources) {
        super(control, sources, interpolation);
        final float spacing = 1.0f / sources.length;
        final float radius = spacing / 2.0f;
        final float blendRange = radius * blend;
        final float cellRadius = (radius - blendRange) / 2.0f;
        this.blend = blend;
        this.nodes = new Node[sources.length];
        this.maxIndex = sources.length - 1;
        this.blendRange = blendRange;
        for (int i = 0; i < sources.length; ++i) {
            final float pos = i * spacing + radius;
            final float min = (i == 0) ? 0.0f : (pos - cellRadius);
            final float max = (i == this.maxIndex) ? 1.0f : (pos + cellRadius);
            this.nodes[i] = new Node(sources[i], min, max);
        }
    }
    
    @Override
    public String getSpecName() {
        return "MultiBlend";
    }
    
    public float selectValue(final float x, final float y, final float selector) {
        final int index = NoiseUtil.round(selector * this.maxIndex);
        Node max;
        Node min = max = this.nodes[index];
        if (this.blendRange == 0.0f) {
            return min.source.getValue(x, y);
        }
        if (selector > min.max) {
            max = this.nodes[index + 1];
        }
        else {
            if (selector >= min.min) {
                return min.source.getValue(x, y);
            }
            min = this.nodes[index - 1];
        }
        float alpha = (selector - min.max) / this.blendRange;
        alpha = NoiseUtil.clamp(alpha, 0.0f, 1.0f);
        return this.blendValues(min.source.getValue(x, y), max.source.getValue(x, y), alpha);
    }
    
    public static DataSpec<MultiBlend> spec() {
        return DataSpec.builder(MultiBlend.class, MultiBlend.factory).add("blend_range", (Object)0, m -> m.blend).add("interp", (Object)Interpolation.LINEAR, m -> m.interpolation).addObj("control", Module.class, m -> m.selector).addList("modules", Module.class, m -> Arrays.asList(m.sources)).build();
    }
    
    static {
        factory = ((data, spec, context) -> new MultiBlend((float)spec.get("blend_range", data, DataValue::asInt), spec.get("interp", data, v -> v.asEnum(Interpolation.class)), spec.get("control", data, Module.class, context), (Module[])Cereal.deserialize(data.getList("modules"), Module.class, context).toArray(new Module[0])));
    }
    
    private static class Node
    {
        private final Module source;
        private final float min;
        private final float max;
        
        private Node(final Module source, final float min, final float max) {
            this.source = source;
            this.min = Math.max(0.0f, min);
            this.max = Math.min(1.0f, max);
        }
        
        @Override
        public String toString() {
            return "Slot{min=" + this.min + ", max=" + this.max + '}';
        }
    }
}
